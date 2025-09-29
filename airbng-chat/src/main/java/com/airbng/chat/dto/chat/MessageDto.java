package com.airbng.chat.dto.chat;

import com.airbng.chat.domain.Message;

import java.util.List;
import java.util.function.Function;

public record MessageDto(
        String id,
        String convId,
        Long   seq,
        String msgId,
        Long   senderId,
        String senderName,
        String type,
        String text,
        Long   sentAtMs,     // ← epoch ms (Instant -> toEpochMilli)
        Boolean deleted,
        List<AttachmentDto> attachments,
        ReservationCardDto reservation,
        RefundDto refund
) {
    public static MessageDto from(
            Message m,
            Function<String,String> signer,                // key -> url
            Function<String,String> keyResolverByAttId     // attId -> key (레거시 호환)
    ) {
        List<AttachmentDto> atts = m.getAttachments()==null ? List.of() :
                m.getAttachments().stream().map(e -> {
                    String key = e.getKey();
                    if (key == null && e.getAttachmentId() != null) {
                        key = keyResolverByAttId.apply(e.getAttachmentId()); // 레거시 보정
                    }
                    String url = (key != null) ? signer.apply(key) : e.getImageUrl(); // 마지막 방어
                    return new AttachmentDto(
                            e.getAttachmentId(), e.getKind(), e.getMime(), e.getSize(),
                            e.getWidth(), e.getHeight(), e.getFileName(), url
                    );
                }).toList();

        ReservationCardDto card = null;
        if (m.getReservation() != null) {
            var r = m.getReservation();
            card = new ReservationCardDto(
                    r.getReservationId(), r.getLockerId(), r.getLockerName(), r.getAddress(),
                    r.getStartTime(), r.getEndTime(), r.getCategory(), r.getPickupMemo(), r.getImgUrl(),
                    r.getStatus(), r.getCanApprove()
            );
        }

        RefundDto refund = RefundDto.from(m.getRefund());

        return new MessageDto(
                m.getId(), m.getConvId(), m.getSeq(), m.getMsgId(),
                m.getSenderId(), m.getSenderName(), m.getType(), m.getText(),
                (m.getSentAt() != null ? m.getSentAt().toEpochMilli() : null),
                Boolean.TRUE.equals(m.getDeleted()),
                atts,
                card,
                refund
        );
    }
}