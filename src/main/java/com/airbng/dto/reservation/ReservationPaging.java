package com.airbng.dto.reservation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReservationPaging {
    private final List<ReservationSearchResponse> reservations;
    private final Long nextCursorId;      // 다음 페이지의 커서 ID (페이징을 위한 용도)
    private final boolean hasNextPage;   // 다음 페이지가 있는지 여부
    private Long totalCount;             // 전체 수
}