package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.ReservationPaging;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    // 예약 등록
    @PostMapping
    public BaseResponse<String> insertReservation(@RequestBody ReservationInsertRequest request) {
        return new BaseResponse<>(reservationService.insertReservation(request));
    }

    @GetMapping
    public BaseResponse<ReservationPaging> findAllReservationById(
            @RequestParam Boolean isDropper,
            @RequestParam Long memberId,
            @RequestParam(required = false) String state,
            @RequestParam("cursorId") Long cursorId,
            @RequestParam("limit") Long limit
    ) {
//        String role = isDropper ? "DROPPER" : "KEEPER";
//        List<ReservationSearchResponse> reservations = reservationService.findAllReservationById(memberId, role, state, cursorId, limit);
//
//        return ResponseEntity.ok(reservations);


        String role = isDropper ? "DROPPER" : "KEEPER";

        ReservationPaging response = reservationService.findAllReservationById(memberId, role, state, cursorId, limit);

        return new BaseResponse<>(response); // 이렇게 객체로 감싼 채로 반환
    }
}
