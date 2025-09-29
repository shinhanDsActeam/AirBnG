package com.airbng.consumer.controller;

import com.airbng.platform.common.response.BaseResponse;
import com.airbng.consumer.domain.base.ReservationState;
import com.airbng.consumer.domain.base.MemberRole;
import com.airbng.consumer.dto.reservation.*;
import com.airbng.consumer.service.ReservationService;
import com.airbng.platform.security.principal.AirbngPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@RestController
@RequestMapping("/reservations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ReservationController {
    private final ReservationService reservationService;

    // 예약 승인 / 거절
    @PatchMapping("/{reservation-id}/confirm")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<ReservationConfirmResponse> confirmReservation(
            @PathVariable("reservation-id") @NotNull @Min(1) Long reservationId,
            @RequestParam("approve") boolean approve,
            @AuthenticationPrincipal AirbngPrincipal principal) {
        return new BaseResponse<>(reservationService.confirmReservation(reservationId, approve, principal.getId()));
    }

    // 예약 취소
    @PatchMapping("/{reservation-id}/cancel")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<ReservationCancelResponse> cancelReservation(
            @RequestHeader(value = "Idempotency-Key") String idemKey,
            @PathVariable("reservation-id") @NotNull @Min(1) Long reservationId,
            @AuthenticationPrincipal AirbngPrincipal principal) {
        log.info("ReservationController.cancelReservation");
        return new BaseResponse<>(reservationService.cancelReservation(reservationId, principal.getId()));
    }

    // 예약 완료
    @PatchMapping("/{reservation-id}/complete")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<ReservationCompleteResponse> completedReservation(
            @PathVariable("reservation-id") @NotNull @Min(1) Long reservationId,
            @AuthenticationPrincipal AirbngPrincipal principal) {
        log.info("ReservationController.completedReservation");
        return new BaseResponse<>(reservationService.completeReservation(reservationId, principal.getId()));
    }

    // 예약 폼 받아오기
    @GetMapping("/form")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<ReservationFormResponse> getReservationForm(@RequestParam("lockerId") @Min(1) @NotNull Long lockerId) {
        return new BaseResponse<>(reservationService.getReservationForm(lockerId));
    }

    // 예약 등록
    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<ReservationInsertResponse> insertReservation(
            @RequestHeader(value = "Idempotency-Key") String idemKey,
            @RequestBody @Valid ReservationInsertRequest request,
            @AuthenticationPrincipal AirbngPrincipal principal) {
        return new BaseResponse<>(
                CREATED_RESERVATION,
                reservationService.insertReservation(idemKey, request, principal)
        );
    }

    @GetMapping("{reservation-id}/members/{member-id}/detail")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<ReservationDetailResponse> getReservationDetail(
            @PathVariable("reservation-id") @NotNull @Min(1) Long reservationId,
            @PathVariable("member-id") @NotNull @Min(1) Long memberId) {
        return new BaseResponse<>(reservationService.findReservationDetail(reservationId, memberId));
    }

    // 예약 조회 + 페이징 처리
    @GetMapping
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<ReservationPaging> findAllReservationById(
            @RequestParam(value = "isDropper") @NotNull Boolean isDropper,
            @RequestParam(value = "memberId") @Min(1) @NotNull Long memberId,
            @RequestParam(required = false) List<ReservationState> state,
            @RequestParam(value = "nextCursorId", required = false) Long nextCursorId,
            @RequestParam(value = "period", required = false, defaultValue = "ALL") String period // 예: "1W", "3M", "6M", "1Y", "2Y"
    ) {

        MemberRole role = isDropper ? MemberRole.DROPPER : MemberRole.KEEPER;
        ReservationPaging response = reservationService.findAllReservationById(memberId, role, state, nextCursorId, period);

        if (response == null || response.getReservations().isEmpty()) {
            return new BaseResponse<>(NO_RESERVATION_CONTNET); // 정상응답하지만 값이 없을때
        }
        return new BaseResponse<>(response); // 값이 있을 때
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<Void> deleteReservation(Long reservationId) {
        reservationService.deleteReservationById(reservationId);
        return new BaseResponse<>(SUCCESS);
    }
}
