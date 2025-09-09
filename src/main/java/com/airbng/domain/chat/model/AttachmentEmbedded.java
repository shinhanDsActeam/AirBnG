package com.airbng.domain.chat.model;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AttachmentEmbedded {
    private String attachmentId;     // 선택(별도 컬렉션과 연결 시)
    private String kind;             // "image" | "file"
    private String mime;
    private Long   size;             // bsonType: long
    private Integer width;           // optional
    private Integer height;          // optional
}