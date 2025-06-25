package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.reservation.ReservationCancelResponse;
import com.airbng.dto.reservation.ReservationDetailResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.dto.reservation.ReservationPaging;
import com.airbng.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/reservations")
@Validated
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

    // 예약 조회 + 페이징 처리
    @GetMapping
    public BaseResponse<ReservationPaging> findAllReservationById(
            @RequestParam(value = "isDropper") @NotNull Boolean isDropper,
            @RequestParam(value = "memberId") @Min(1) @NotNull Long memberId,
            @RequestParam(required = false) ReservationState state,
            @RequestParam(value = "nextCursorId", required = false) Long nextCursorId
    ) {

        String role = isDropper ? "DROPPER" : "KEEPER";
        ReservationPaging response = reservationService.findAllReservationById(memberId, role, state, nextCursorId );

        return new BaseResponse<>(response); // 이렇게 객체로 감싼 채로 반환
    }
}
