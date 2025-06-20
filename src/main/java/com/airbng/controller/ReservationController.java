package com.airbng.controller;

import com.airbng.dto.ReservationSearchResponse;
import com.airbng.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationSearchResponse>> findAllReservationById(
            @RequestParam Boolean isDropper,
            @RequestParam Long memberId,
            @RequestParam(required = false) String state,
            @PathVariable("cursorId") Long cursorId
    ) {
        String role = isDropper ? "DROPPER" : "KEEPER";
        List<ReservationSearchResponse> reservations = reservationService.findAllReservationById(memberId, role, state, cursorId);

        return ResponseEntity.ok(reservations);
    }
}
