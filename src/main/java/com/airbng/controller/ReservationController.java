package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.ReservationCancelRequest;
import com.airbng.dto.ReservationCancelResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reservations/{reservationId}/members/{memberId}/cancel")
    public BaseResponse<ReservationCancelResponse> updateResponse(@PathVariable("reservationId") Long reservationId, @PathVariable("memberId") Long memberId){
        log.info("ReservationController.updateResponse");
        return new BaseResponse<>(reservationService.updateReservationState(reservationId, memberId));
    }
    // 예약 등록
    @PostMapping
    public BaseResponse<String> insertReservation(@RequestBody @Valid ReservationInsertRequest request) {
        return new BaseResponse<>(reservationService.insertReservation(request));
    }

}
