package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.reservation.ReservationDetailResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {
    private final ReservationService reservationService;
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
