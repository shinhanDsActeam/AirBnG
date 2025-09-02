package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.base.MemberRole;
import com.airbng.dto.reservation.*;
import com.airbng.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.NO_RESERVATION_CONTNET;
import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS;

@RestController
@RequestMapping("/reservations")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ReservationController {
    private final ReservationService reservationService;

    //예약 승인 취소
    @PatchMapping("/{reservation-id}/members/{member-id}/confirm")
    public BaseResponse<ReservationConfirmResponse> confirmResponse(
            @PathVariable("reservation-id") @NotNull @Min(1) Long reservationId,
            @PathVariable("member-id") Long memberId,
            @RequestParam("approve") String approve) {
        return new BaseResponse<>(reservationService.confirmReservationState(reservationId, approve, memberId));
    }

    @PostMapping("/{reservation-id}/members/{member-id}/cancel")
    public BaseResponse<ReservationCancelResponse> updateResponse(@PathVariable("reservation-id") Long reservationId, @PathVariable("member-id") Long memberId) {
        log.info("ReservationController.updateResponse");
        return new BaseResponse<>(reservationService.updateReservationState(reservationId, memberId));
    }

    // 예약 폼 받아오기
    @GetMapping("/form")
    public BaseResponse<ReservationFormResponse> getReservationForm(@RequestParam("lockerId") @Min(1) @NotNull Long lockerId) {
        return new BaseResponse<>(reservationService.getReservationForm(lockerId));
    }

    // 예약 등록
    @PostMapping
    public BaseResponse<BaseStatus> insertReservation(@RequestBody @Valid ReservationInsertRequest request) {
//        BaseStatus status = reservationService.insertReservation(request);

        return new BaseResponse<>(
                reservationService.insertReservation(request)
        );
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
            @RequestParam(required = false) List<ReservationState> state,
            @RequestParam(value = "nextCursorId", required = false) Long nextCursorId,
            @RequestParam(value = "period", required = false, defaultValue = "ALL") String period // 예: "1W", "3M", "6M", "1Y", "2Y"
    ) {

        MemberRole role = isDropper ? MemberRole.DROPPER : MemberRole.KEEPER;
        ReservationPaging response = reservationService.findAllReservationById(memberId, role, state, nextCursorId, period );

        if(response == null || response.getReservations().isEmpty()){
            return new BaseResponse<>(NO_RESERVATION_CONTNET); // 정상응답하지만 값이 없을때
        }
        return new BaseResponse<>(response); // 값이 있을 때
    }

    @PostMapping("/delete")
    public BaseResponse<Void> deleteReservation(Long reservationId){
        reservationService.deleteReservationById(reservationId);
        return new BaseResponse<>(SUCCESS);
    }
}
