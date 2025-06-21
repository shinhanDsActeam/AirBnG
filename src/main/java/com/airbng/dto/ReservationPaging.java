package com.airbng.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReservationPaging {
    private final List<ReservationSearchResponse> reservations;
    private final Long nextCursorId;      // 다음 페이지의 커서 ID (페이징을 위한 용도)
    private final boolean hasNextPage;   // 다음 페이지가 있는지 여부
    private Long totalCount;            // 전체 수

    //자동 처리 기능은 없어지고,
    //✔ 대신 Builder를 사용하는 곳에서 hasNextPage, cursorId 등을 스스로 판단해서 넘겨줘야함
//    // 생성자 : 다음 페이지 없을 때
//    public ReservationPaging(List<ReservationSearchResponse> reservations) {
//        this.reservations = reservations;
//        this.cursorId = -1L;
//        hasNextPage = false;
//    }
//    // 생성자 : 다음 페이지 있을 때
//    public ReservationPaging(List<ReservationSearchResponse> reservations, Long cursorId) {
//        this.reservations = reservations;
//        this.cursorId = cursorId;
//        hasNextPage = true;
//    }
    
}
