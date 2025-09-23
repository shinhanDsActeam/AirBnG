package com.airbng.chat.service;

import com.airbng.chat.domain.Attachment;
import com.airbng.chat.domain.Conversation;
import com.airbng.chat.domain.Message;
import com.airbng.chat.domain.model.AttachmentEmbedded;
import com.airbng.chat.domain.model.LastMessage;
import com.airbng.chat.repository.AttachmentRepository;
import com.airbng.chat.repository.ConversationRepository;
import com.airbng.chat.repository.MessageRepository;
import com.airbng.chat.util.RedisSequenceService;
import com.airbng.platform.util.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MessageRepository messageRepo;
    private final ConversationRepository conversationRepo;
    private final ConversationService conversationService;
    private final InboxService inboxService;
    private final RedisSequenceService redisSeq;
    private final S3Utils s3;

    @Override
    public Attachment save(Attachment a) {
        return attachmentRepository.save(a);
    }

    @Override
    public List<Attachment> findByMessageId(String messageId) {
        return attachmentRepository.findByMessageId(messageId);
    }

    @Override
    public Message uploadAndSend(String convId, long senderId, String senderName,
                                 MultipartFile file, String kind, String msgId) {
        // 1) 방 멤버십 검증
        conversationService.assertMember(convId, senderId);

        // 2) 멱등 (이미 같은 msgId로 메시지가 있으면 그대로 리턴)
        var dup = messageRepo.findByConvIdAndMsgId(convId, msgId);
        if (dup.isPresent()) return dup.get();

        // 3) 업로드 경로 결정
        String prefix = "chat/" + convId + ( "image".equalsIgnoreCase(kind) ? "/images" : "/files" );

        // 4) 이미지라면 width/height 미리 계산
        Integer width = null, height = null;
        if ("image".equalsIgnoreCase(kind)) {
            try (InputStream in = file.getInputStream()) {
                BufferedImage bi = ImageIO.read(in);
                if (bi != null) { width = bi.getWidth(); height = bi.getHeight(); }
            } catch (Exception ignore) {}
        }

        // 5) S3 업로드
        String key = s3.uploadForChat(file, prefix, kind);

        // presigned GET (예: 1시간)
        String signedUrl = s3.presignGetUrl(key, 60 * 60);

        // 원본 파일명(경로 제거)
        String originalName = file.getOriginalFilename();
        if (originalName != null) {
            try { originalName = Paths.get(originalName).getFileName().toString(); }
            catch (Exception ignore) {}
        }

        // 6) Attachment 문서 저장 (messageId = msgId 로 연결)
        Attachment att = Attachment.builder()
                .convId(convId)
                .messageId(msgId)               // Message.msgId 를 FK처럼 참조
                .kind(kind.toLowerCase())
                .storage("s3")
                .key(key)
                .mime(file.getContentType())
                .size(file.getSize())
                .width(width)
                .height(height)
                .imageUrl(signedUrl)
                .fileName(originalName)
                .build();
        att = attachmentRepository.save(att);

        // 7) Message 생성 (타입: image|file, attachments 임베디드 1개)
        long seq = redisSeq.nextMessageSeq(convId);
        Instant now = Instant.now();

        AttachmentEmbedded emb = AttachmentEmbedded.builder()
                .attachmentId(att.getId())
                .kind(att.getKind())
                .mime(att.getMime())
                .size(att.getSize())
                .width(att.getWidth())
                .height(att.getHeight())
                .key(att.getKey())
                .imageUrl(att.getImageUrl())
                .fileName(att.getFileName())
                .build();

        Message toSave = Message.builder()
                .convId(convId)
                .seq(seq)
                .msgId(msgId)
                .senderId(senderId)
                .senderName(senderName)
                .type("image".equalsIgnoreCase(kind) ? "image" : "file")
                .attachments(List.of(emb))
                .text(null)
                .sentAt(now)
                .deleted(false)
                .build();

        Message saved;
        try {
            saved = messageRepo.save(toSave);
        } catch (DuplicateKeyException e) {
            return messageRepo.findByConvIdAndMsgId(convId, msgId).orElseThrow();
        }

        // 8) lastMessage + inbox 갱신
        String preview = "image".equalsIgnoreCase(kind) ? "[이미지]" : "[파일]";
        LastMessage last = LastMessage.builder()
                .messageId(saved.getMsgId())
                .senderId(senderId)
                .type(saved.getType())
                .preview(preview)
                .sentAt(now)
                .build();

        conversationService.updateOnNewMessage(convId, last, seq);
        long peer = conversationService.peerIdOf(convId, senderId);
        inboxService.onNewMessage(senderId, peer, convId, last, now, senderId);
        inboxService.onNewMessage(peer, senderId, convId, last, now, senderId);

        return saved;
    }

    @Override
    public Message deleteAttachment(String attachmentId, long requesterId) {
        // 1) attachment 조회
        Attachment att = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("attachment not found: " + attachmentId));

        // 2) message 조회
        Message msg = messageRepo.findByConvIdAndMsgId(att.getConvId(), att.getMessageId())
                .orElseThrow(() -> new IllegalStateException("message not found for attachment: " + att.getMessageId()));

        // 3) 권한: 대화 참여자 + 보낸 사람만 삭제 가능 (정책에 따라 수정 가능)
        conversationService.assertMember(att.getConvId(), requesterId);
        if (!Objects.equals(msg.getSenderId(), requesterId)) {
            throw new AccessDeniedException("only sender can delete attachment");
        }

        // 4) S3 삭제
        s3.deleteByKey(att.getKey());

        // 5) attachment 문서 삭제
        attachmentRepository.deleteById(attachmentId);

        // 6) message 임베디드에서 제거
        List<AttachmentEmbedded> remain = msg.getAttachments() == null ? List.of() :
                msg.getAttachments().stream()
                        .filter(e -> !attachmentId.equals(e.getAttachmentId()))
                        .collect(Collectors.toList());
        msg.setAttachments(remain);

        // 7) 메시지 후처리:
        //   - 첨부만 있던 메시지였다면, 비운 뒤 soft delete
        //   - 여러 개 중 하나 삭제면 타입/preview 유지
        if ((remain == null || remain.isEmpty()) && msg.getText() == null) {
            msg.setDeleted(true);
        }

        Message saved = messageRepo.save(msg);

        // 8) lastMessage / inbox 갱신
        Conversation conv = conversationRepo.findById(att.getConvId()).orElse(null);
        if (conv != null && conv.getLastMessage() != null
                && att.getMessageId().equals(conv.getLastMessage().getMessageId())) {

            String newPreview;
            if (Boolean.TRUE.equals(saved.getDeleted())) {
                newPreview = "[삭제된 메시지]";
            } else {
                // 여전히 첨부가 남아있다면 기존 타입 기반
                newPreview = "image".equals(saved.getType()) ? "[이미지]" :
                        "file".equals(saved.getType())  ? "[파일]"   :
                                "";
            }

            LastMessage lm = conv.getLastMessage();
            lm.setPreview(newPreview);
            // sentAt/sender/type은 그대로 유지

            // Conversation 업데이트
            conv.setLastMessage(lm);
            conversationRepo.save(conv);

            // 양쪽 인박스 lastMessage 미러 업데이트
            long peerA = conv.getUserA();
            long peerB = conv.getUserB();
            inboxService.onNewMessage(peerA, peerB, conv.getId(), lm, lm.getSentAt(), saved.getSenderId());
            inboxService.onNewMessage(peerB, peerA, conv.getId(), lm, lm.getSentAt(), saved.getSenderId());
        }

        return saved;
    }
}
