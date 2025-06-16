package com.airbng.domain.base;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BaseTime {
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
