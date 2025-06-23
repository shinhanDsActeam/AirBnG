package com.airbng.dto;

import java.util.List;

public class ReservationPaging {
    private final List<ReservationSearchResponse> reservations;
    private final Long cursorId;         // 커서 ID, 페이징을 위한 용도
    private final boolean hasNextPage;   // 다음 페이지가 있는지 여부
    private final Long nextCursor;       // 다음 페이지의 커서 ID
    private final Long pageSize = 10L;   // 페이지 크기, 기본값은 10
    private Long totalCount;             // 전체 수

    // 생성자 : 다음 페이지 없을 때
    public ReservationPaging(List<ReservationSearchResponse> reservations) {
        this.reservations = reservations;
        this.cursorId = -1L;
        this.nextCursor = -1L; // 다음 커서 ID는 -1로 설정
        hasNextPage = false;
    }
    // 생성자 : 다음 페이지 있을 때
    public ReservationPaging(List<ReservationSearchResponse> reservations, Long cursorId, Long nextCursor) {
        this.reservations = reservations.subList(0, 10);
        this.cursorId = cursorId;
        this.nextCursor = nextCursor;
        hasNextPage = true;
    }
    
}
