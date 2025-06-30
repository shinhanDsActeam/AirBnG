package com.airbng.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Zzim {
    private Long lockerId;
    private Long memberId;

    // 조회용
    private Locker locker;
    private Member member;
}
