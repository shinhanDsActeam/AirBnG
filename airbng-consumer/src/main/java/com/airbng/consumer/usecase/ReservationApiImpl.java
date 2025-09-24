package com.airbng.consumer.usecase;

import com.airbng.api.consumer.ReservationApi;
import com.airbng.api.consumer.dto.command.ReservationDecisionCommand;
import com.airbng.api.consumer.dto.common.ReservationStatus;
import com.airbng.api.consumer.dto.view.ReservationCardPayload;
import com.airbng.api.consumer.dto.view.ReservationDecisionResult;
import com.airbng.consumer.domain.base.ReservationState;
import com.airbng.consumer.repository.ReservationRepository;
import com.airbng.consumer.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
class ReservationApiImpl implements ReservationApi {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    private static ReservationStatus toDtoStatus(ReservationState s) {
        return ReservationStatus.valueOf(s.name());
    }

    @Override
    public ReservationCardPayload getCardPayload(Long reservationId) {
        var r = reservationRepository.findReservationDetailById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var locker = r.getLocker();
        String img = locker.getLockerImages().stream()
                .findFirst()
                .map(li -> li.getImage() != null ? li.getImage().getUrl() : null)
                .orElse(null);

        String category = (r.getReservationJimTypes() == null || r.getReservationJimTypes().isEmpty())
                ? null
                : r.getReservationJimTypes().iterator().next().getJimType().getTypeName();

        return new ReservationCardPayload(
                r.getReservationId(),
                r.getDropper().getMemberId(), r.getDropper().getNickname(),
                r.getKeeper().getMemberId(),  r.getKeeper().getNickname(),
                locker.getLockerId(), locker.getLockerName(), locker.getAddress(),
                r.getStartTime(), r.getEndTime(),
                category, r.getPickupMemo(), img,
                toDtoStatus(r.getState()),
                r.getState() == ReservationState.PENDING
        );
    }

    @Transactional
    @Override
    public ReservationDecisionResult decide(ReservationDecisionCommand cmd) {
        var r = reservationRepository.findByIdForUpdate(cmd.reservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (r.getKeeper() == null || !r.getKeeper().getMemberId().equals(cmd.actorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only keeper can decide");
        }
        if (r.getState() != ReservationState.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "already decided");
        }

        r.updateState(cmd.approve() ? ReservationState.CONFIRMED : ReservationState.CANCELLED);
        r.setDecisionReason(cmd.reason());
        r.setDecidedAt(LocalDateTime.now());
        r.setDecidedBy(cmd.actorId());

        return new ReservationDecisionResult(
                r.getReservationId(),
                toDtoStatus(r.getState()),
                cmd.actorId(),
                r.getDecidedAt()
        );
    }

}
