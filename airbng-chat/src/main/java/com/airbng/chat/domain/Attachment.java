package com.airbng.chat.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("attachment")
@CompoundIndex(name="msg_kind_idx", def="{ 'messageId': 1, 'kind': 1 }")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Attachment {
    @Id
    private String id;

    @Indexed private String convId;
    @Indexed private String messageId;  // Message의 msgId

    private String kind;                // "image" | "file"
    private String storage;             // "s3" | "gcs" | "local"
    private String key;                 // 버킷 내 경로
    private String mime;
    private Long   size;
    private Integer width;              // nullable
    private Integer height;             // nullable
}
