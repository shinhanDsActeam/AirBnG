package com.airbng.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendTextRequest {
    @NotBlank
    private String text;

    /** 클라이언트에서 생성한 멱등 키(UUID 등). 동일 키 중복 전송 시 서버는 최초 저장건을 그대로 반환 */
    @NotBlank
    private String msgId;
}
