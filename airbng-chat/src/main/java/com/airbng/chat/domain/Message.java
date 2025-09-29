package com.airbng.chat.domain;

import com.airbng.chat.domain.model.AttachmentEmbedded;
import com.airbng.chat.domain.model.RefundCard;
import com.airbng.chat.domain.model.ReservationCard;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("message")
@CompoundIndex(name="conv_seq_uq",   def="{ 'convId': 1, 'seq': 1 }",   unique = true)
@CompoundIndex(name="conv_msgid_uq", def="{ 'convId': 1, 'msgId': 1 }", unique = true)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Message {
    @Id
    private String id;

    @Indexed private String convId;
    @Indexed private Long   seq;

    @Indexed private String msgId;      // 멱등키(클라이언트 UUID)

    @Indexed private Long senderId;
    private String senderName;

    private String type;                // text|image|file|system|reservation
    private String text;                // nullable

    private ReservationCard reservation;
    private RefundCard refund;

    private List<AttachmentEmbedded> attachments;

    private Instant sentAt;
    private Boolean deleted;
}