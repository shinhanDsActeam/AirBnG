package com.airbng.chat.service;

import com.airbng.api.consumer.ReservationApi;
import com.airbng.api.consumer.dto.command.ReservationDecisionCommand;
import com.airbng.api.consumer.dto.view.ReservationCardPayload;
import com.airbng.chat.domain.Attachment;
import com.airbng.chat.domain.Message;
import com.airbng.chat.domain.model.AttachmentEmbedded;
import com.airbng.chat.domain.model.LastMessage;
import com.airbng.chat.domain.model.ReservationCard;
import com.airbng.chat.repository.AttachmentRepository;
import com.airbng.chat.repository.MessageRepository;
import com.airbng.chat.util.RedisSequenceService;
import com.airbng.platform.util.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepo;
    private final ConversationService conversationService;
    private final InboxService inboxService;
    private final RedisSequenceService redisSeq;
    private final AttachmentRepository attachmentRepository;
    private final S3Utils s3;
    private final ReservationApi reservationApi;

    // 예약 메시지 전송
    @Override
    public Message sendReservationCard(String convId,
                                       long senderId,
                                       String senderName,
                                       ReservationCardPayload payload,
                                       String msgId) {
        conversationService.assertMember(convId, senderId);

        var duplicated = messageRepo.findByConvIdAndMsgId(convId, msgId);
        if (duplicated.isPresent()) return duplicated.get();

        // convId 안전 검증
        String expected = conversationService.makeConvId(payload.dropperId(), payload.keeperId());
        if (!expected.equals(convId)) {
            throw new IllegalArgumentException("reservation parties != conversation");
        }

        long seq = redisSeq.nextMessageSeq(convId);
        Instant now = Instant.now();

        var card = ReservationCard.builder()
                .reservationId(payload.reservationId())
                .lockerId(payload.lockerId())
                .lockerName(payload.lockerName())
                .address(payload.address())
                .startTime(payload.startTime())
                .endTime(payload.endTime())
                .category(payload.category())
                .pickupMemo(payload.pickupMemo())
                .imgUrl(payload.imgUrl())
                .status(String.valueOf(payload.status()))
                .canApprove(payload.canApprove())
                .build();

        var toSave = Message.builder()
                .convId(convId).seq(seq).msgId(msgId)
                .senderId(senderId).senderName(senderName)
                .type("reservation")
                .reservation(card)
                .sentAt(now).deleted(false)
                .build();

        Message saved;
        try { saved = messageRepo.save(toSave); }
        catch (DuplicateKeyException e) {
            return messageRepo.findByConvIdAndMsgId(convId, msgId).orElseThrow();
        }

        LastMessage last = LastMessage.builder()
                .messageId(saved.getMsgId())
                .senderId(senderId)
                .type("reservation")
                .preview(makeReservationPreview(card))
                .sentAt(now)
                .build();

        conversationService.updateOnNewMessage(convId, last, seq);
        long peer = conversationService.peerIdOf(convId, senderId);
        inboxService.onNewMessage(senderId, peer, convId, last, now, senderId);
        inboxService.onNewMessage(peer, senderId, convId, last, now, senderId);

        return saved;
    }

    // 예약 승인/거절 결정
    @Override
    public Message decideReservation(String convId, long actorId, Long reservationId, boolean approve, String reason) {
        conversationService.assertMember(convId, actorId);

        var result = reservationApi.decide(
                new ReservationDecisionCommand(reservationId, actorId, approve, reason)
        );

        long seq = redisSeq.nextMessageSeq(convId);
        Instant now = Instant.now();

        String reasonPart = "";
        if (!approve) {
            String trimmed = (reason == null) ? "" : reason.trim();
            if (!trimmed.isEmpty()) {
                // 너무 긴 경우 컷(선택)
                String r = trimmed.length() > 500 ? trimmed.substring(0, 500) + "…" : trimmed;
                reasonPart = "\n사유: " + r;
            }
        }

        var text = switch (result.newStatus()) {
            case CONFIRMED -> "예약을 승인했어요.";
            case CANCELLED, REJECTED -> "예약을 거절했어요." + reasonPart;
            default -> "예약 상태가 변경되었어요.";
        };

        var msg = Message.builder()
                .convId(convId).seq(seq)
                .msgId("decision-" + reservationId + "-" + now.toEpochMilli())
                .senderId(actorId).senderName("system")
                .type("system").text(text)
                .sentAt(now).deleted(false)
                .build();

        var saved = messageRepo.save(msg);

        LastMessage last = LastMessage.builder()
                .messageId(saved.getMsgId()).senderId(actorId)
                .type("system").preview(makePreview(text)).sentAt(now).build();
        conversationService.updateOnNewMessage(convId, last, seq);

        long peer = conversationService.peerIdOf(convId, actorId);
        inboxService.onNewMessage(actorId, peer, convId, last, now, actorId);
        inboxService.onNewMessage(peer, actorId, convId, last, now, actorId);

        return saved;
    }

    @Override
    public Message sendText(String convId, long senderId, String senderName, String text, String msgId) {
        // 멤버십 보장
        conversationService.assertMember(convId, senderId);

        // 멱등
        var duplicated = messageRepo.findByConvIdAndMsgId(convId, msgId);
        if (duplicated.isPresent()) return duplicated.get();

        long seq = redisSeq.nextMessageSeq(convId);
        Instant now = Instant.now();

        Message toSave = Message.builder()
                .convId(convId)
                .seq(seq)
                .msgId(msgId)
                .senderId(senderId)
                .senderName(senderName)
                .type("text")
                .text(text)
                .attachments(null)
                .sentAt(now)
                .deleted(false)
                .build();

        Message saved;
        try {
            saved = messageRepo.save(toSave);
        } catch (DuplicateKeyException e) {
            return messageRepo.findByConvIdAndMsgId(convId, msgId).orElseThrow();
        }

        // lastMessage
        LastMessage last = LastMessage.builder()
                .messageId(saved.getMsgId())
                .senderId(senderId)
                .type("text")
                .preview(makePreview(text))
                .sentAt(now)
                .build();

        conversationService.updateOnNewMessage(convId, last, seq);

        // 인박스 갱신(양쪽)
        long peer = conversationService.peerIdOf(convId, senderId);
        inboxService.onNewMessage(senderId, peer, convId, last, now, senderId);
        inboxService.onNewMessage(peer, senderId, convId, last, now, senderId);

        return saved;
    }

    @Override
    public List<Message> getMessages(String convId, Long beforeSeq, int size) {
        var page = PageRequest.of(0, Math.max(1, Math.min(size, 200)), Sort.by(Sort.Direction.DESC, "seq"));

        List<Message> desc = (beforeSeq == null)
                ? messageRepo.findByConvIdOrderBySeqDesc(convId, page)
                : messageRepo.findByConvIdAndSeqLessThanOrderBySeqDesc(convId, beforeSeq, page);

        var asc = new ArrayList<>(desc);
        Collections.reverse(asc);

        refreshSignedUrls(asc, 3600);

        return asc;
    }

    /** 메시지들의 임베디드 첨부를 실제 Attachment와 매핑해 presigned URL/파일명 주입 */
    private void refreshSignedUrls(List<Message> messages, int ttlSeconds) {
        if (messages == null || messages.isEmpty()) return;

        // 필요한 attachmentId 모으기
        Set<String> ids = messages.stream()
                .filter(m -> m.getAttachments() != null)
                .flatMap(m -> m.getAttachments().stream())
                .map(AttachmentEmbedded::getAttachmentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) return;

        // 배치 조회 후 map
        Map<String, Attachment> map = attachmentRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Attachment::getId, a -> a));

        // presign 해서 주입
        for (Message m : messages) {
            if (m.getAttachments() == null) continue;
            for (AttachmentEmbedded e : m.getAttachments()) {
                Attachment att = map.get(e.getAttachmentId());
                if (att == null) continue;

                String signed = s3.presignGetUrl(att.getKey(), ttlSeconds);
                e.setImageUrl(signed);            // 이미지/파일 공통으로 사용
                e.setFileName(att.getFileName());
                // 필요하면 mime/size/width/height도 보정 가능
            }
        }
    }

    private static String makeReservationPreview(ReservationCard card) {
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
        String t1 = card.getStartTime() != null ? dt.format(card.getStartTime()) : "";
        String t2 = card.getEndTime()   != null ? DateTimeFormatter.ofPattern("HH:mm").format(card.getEndTime()) : "";
        return (card.getLockerName() != null ? card.getLockerName() : "예약") + " · " + t1 + (t2.isEmpty() ? "" : "–" + t2);
    }

    private static String makePreview(String text) {
        if (text == null) return "";
        String s = text.trim().replaceAll("\\s+", " ");
        return s.length() > 60 ? s.substring(0, 60) + "…" : s;
    }
}
