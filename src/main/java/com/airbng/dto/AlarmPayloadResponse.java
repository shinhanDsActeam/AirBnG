package com.airbng.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmPayloadResponse {
    private Long eventId;
    private String data;
}
