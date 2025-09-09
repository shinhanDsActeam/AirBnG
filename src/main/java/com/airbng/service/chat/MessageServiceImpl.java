package com.airbng.service.chat;

import com.airbng.domain.Reservation;
import com.airbng.domain.chat.Message;
import com.airbng.domain.chat.model.LastMessage;
import com.airbng.domain.chat.model.ReservationCard;
import com.airbng.repository.ReservationRepository;
import com.airbng.repository.chat.MessageRepository;
import com.airbng.util.chat.RedisSequenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepo;
    private final ConversationService conversationService;
    private final InboxService inboxService;
    private final RedisSequenceService redisSeq;

    // 예약 조회용 JPA 레포 주입
    private final ReservationRepository reservationRepository;

//    @Override
//    public Message sendReservation(String convId, long senderId, String senderName, Long reservationId, String msgId) {
//        // 1) 멤버십 검사
//        conversationService.assertMember(convId, senderId);
//
//        // 2) 멱등
//        var duplicated = messageRepo.findByConvIdAndMsgId(convId, msgId);
//        if (duplicated.isPresent()) return duplicated.get();
//
//        // 3) MySQL 예약 조회 (필요 정보 fetch)
//        Reservation r = reservationRepository.findReservationDetailById(reservationId)
//                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
//
//        Long dropperId = r.getDropper().getMemberId();
//        Long keeperId  = r.getKeeper().getMemberId();
//
//        // convId가 예약 참여자와 일치하는지 보장
//        String expectedConvId = conversationService.makeConvId(dropperId, keeperId);
//        if (!expectedConvId.equals(convId)) {
//            throw new IllegalArgumentException("Reservation parties != conversation members");
//        }
//
//        // 4) 카드 페이로드 구성
//        String lockerName  = r.getLocker().getLockerName();            // 필드명은 실제 엔티티에 맞게 수정
//        String address     = r.getLocker().getAddress();
//        // 첫 짐타입 이름(없으면 null)
//        String category = r.getReservationJimTypes() != null && !r.getReservationJimTypes().isEmpty()
//                ? r.getReservationJimTypes().iterator().next().getJimType().getTypeName()
//                : null; // 예시
//        String pickupMemo  = null;                  // 실제 필드명에 맞게 수정
//        String lockerImage = r.getLocker().getLockerImages().stream()
//                .findFirst()
//                .map(li -> {
//                    // 보통 li.getImage().getUrl() 형태가 많음. (필요시 getImageUrl()로 바꿔줘)
//                    if (li.getImage() == null || li.getImage().getUrl() == null) {
//                        throw new IllegalStateException("Locker image missing URL");
//                    }
//                    return li.getImage().getUrl();
//                })
//                .orElseThrow(() -> new IllegalStateException("Locker must have at least one image"));
//
//        ReservationCard card = ReservationCard.builder()
//                .reservationId(r.getReservationId())
//                .lockerId(r.getLocker().getLockerId())
//                .lockerName(lockerName)
//                .address(address)
//                .startTime(r.getStartTime())
//                .endTime(r.getEndTime())
//                .category(category)
//                .pickupMemo(null)
//                .imgUrl(lockerImage)
//                .build();
//
//        // 5) seq 생성 & 메시지 저장
//        long seq = redisSeq.nextMessageSeq(convId);
//        Instant now = Instant.now();
//
//        Message toSave = Message.builder()
//                .convId(convId)
//                .seq(seq)
//                .msgId(msgId)
//                .senderId(senderId)
//                .senderName(senderName)
//                .type("reservation")
//                .reservation(card)      // ⬅⬅⬅ 카드 탑재
//                .attachments(null)
//                .text(null)             // 텍스트는 사용 안 함
//                .sentAt(now)
//                .deleted(false)
//                .build();
//
//        Message saved;
//        try {
//            saved = messageRepo.save(toSave);
//        } catch (DuplicateKeyException e) {
//            return messageRepo.findByConvIdAndMsgId(convId, msgId).orElseThrow();
//        }
//
//        // 6) lastMessage + inbox 갱신
//        String preview = makeReservationPreview(card);
//        LastMessage last = LastMessage.builder()
//                .messageId(saved.getMsgId())
//                .senderId(senderId)
//                .type("reservation")
//                .preview(preview)
//                .sentAt(now)
//                .build();
//
//        conversationService.updateOnNewMessage(convId, last, seq);
//
//        long peer = conversationService.peerIdOf(convId, senderId);
//        inboxService.onNewMessage(senderId, peer, convId, last, now, senderId);
//        inboxService.onNewMessage(peer, senderId, convId, last, now, senderId);
//
//        return saved;
//    }

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
        java.util.Collections.reverse(asc);
        return asc;
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
