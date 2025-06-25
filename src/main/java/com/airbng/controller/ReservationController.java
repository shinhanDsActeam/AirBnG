package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.reservation.ReservationCancelResponse;
import com.airbng.dto.reservation.ReservationDetailResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/{reservation-id}/members/{member-id}/cancel")
    public BaseResponse<ReservationCancelResponse> updateResponse(@PathVariable("reservation-id") Long reservationId, @PathVariable("member-id") Long memberId) {
        log.info("ReservationController.updateResponse");
        return new BaseResponse<>(reservationService.updateReservationState(reservationId, memberId));
    }
    // 예약 등록
    @PostMapping
    public BaseResponse<String> insertReservation(@RequestBody @Valid ReservationInsertRequest request) {
        return new BaseResponse<>(reservationService.insertReservation(request));
    }

    @GetMapping("{reservation-id}/members/{member-id}/detail")
    public BaseResponse<ReservationDetailResponse> getReservationDetail(
            @PathVariable("reservation-id")  @NotNull @Min(1) Long reservationId,
            @PathVariable("member-id") @NotNull @Min(1) Long memberId){
        return new BaseResponse<>(reservationService.findReservationDetail(reservationId, memberId));
    }

}
