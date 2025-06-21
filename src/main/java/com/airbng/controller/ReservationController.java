package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.reservation.ReservationInsertRequest;
import com.airbng.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    // 예약 등록
    @PostMapping
    public BaseResponse<String> insertReservation(@RequestBody ReservationInsertRequest request) {
        return new BaseResponse<>(reservationService.insertReservation(request));
    }

}
