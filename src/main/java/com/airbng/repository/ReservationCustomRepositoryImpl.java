package com.airbng.repository;

import com.airbng.domain.*;
import com.airbng.domain.base.ReservationState;
import com.airbng.domain.image.Image;
import com.airbng.domain.image.LockerImage;
import com.airbng.domain.image.QImage;
import com.airbng.domain.image.QLockerImage;
import com.airbng.domain.jimtype.QJimType;
import com.airbng.domain.jimtype.QReservationJimType;
import com.airbng.dto.jimType.JimTypeResult;
import com.airbng.dto.reservation.ReservationSearchResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public List<ReservationSearchResponse> findAllReservationByIdWithCursor(Long memberId, String role, List<ReservationState> states, Long nextCursorId,
                                                                  Long limit, String period, boolean isHistoryTab) {
        QReservation r = QReservation.reservation;
        QLocker l = QLocker.locker;
        QReservationJimType rjt = QReservationJimType.reservationJimType;
        QJimType jt = QJimType.jimType;
        QMember keeper = new QMember("KEEPER");
        QMember dropper = new QMember("DROPPER");
        QLockerImage li = QLockerImage.lockerImage;
        QImage image = QImage.image;

        BooleanBuilder where = new BooleanBuilder();

        // 역할에 따른 조건 설정
        BooleanExpression roleCondition = null;
        if ("KEEPER".equals(role)) {
            roleCondition = r.keeper.memberId.eq(memberId);
        } else if ("DROPPER".equals(role)) {
            roleCondition = r.dropper.memberId.eq(memberId);
        }
        // 역할에 따른 조건을 where 절에 추가
        if (roleCondition != null) {
            where.and(roleCondition);
        }

        // 상태 필터링
        if (states != null && !states.isEmpty()) {
            where.and(r.state.in(states));
        }

        // 기간 필터링
        if (isHistoryTab && period != null && !"ALL".equals(period)) {
            LocalDateTime after = switch (period) {
                case "1W" -> LocalDateTime.now().minusWeeks(1);
                case "3M" -> LocalDateTime.now().minusMonths(3);
                case "6M" -> LocalDateTime.now().minusMonths(6);
                case "1Y" -> LocalDateTime.now().minusYears(1);
                case "2Y" -> LocalDateTime.now().minusYears(2);
                default -> null;
            };
            if (after != null) {
                where.and(r.endTime.goe(after));
            }
        }

        // nextCursorId 초기화: null이면 maxId + 1
        if (nextCursorId == null) {
            Long maxId = query.select(r.reservationId.max())
                    .from(r)
                    .where(where)
                    .fetchOne();
            nextCursorId = (maxId != null) ? maxId + 1L : -1L;
        }

        if (nextCursorId != null && nextCursorId > 0L) {
            where.and(r.reservationId.lt(nextCursorId));
        }

        // 쿼리 실행 및 DTO 매핑
        List<Reservation> reservations;

        if ("KEEPER".equals(role)) {
            reservations = query
                    .selectFrom(r)
                    .leftJoin(r.keeper, keeper).fetchJoin()
                    .leftJoin(keeper.locker, l).fetchJoin()
                    .leftJoin(l.lockerImages, li).fetchJoin()
                    .leftJoin(li.image, image).fetchJoin()
                    .leftJoin(r.reservationJimTypes, rjt).fetchJoin()
                    .leftJoin(rjt.jimType, jt).fetchJoin()
                    .where(where)
                    .orderBy(r.reservationId.desc())
                    .limit(limit)
                    .fetch();
        } else if ("DROPPER".equals(role)) {
            reservations = query
                    .selectFrom(r)
                    .leftJoin(r.dropper, dropper).fetchJoin()
                    .leftJoin(dropper.locker, l).fetchJoin()
                    .leftJoin(l.lockerImages, li).fetchJoin()
                    .leftJoin(li.image, image).fetchJoin()
                    .leftJoin(r.reservationJimTypes, rjt).fetchJoin()
                    .leftJoin(rjt.jimType, jt).fetchJoin()
                    .where(where)
                    .orderBy(r.reservationId.desc())
                    .limit(limit)
                    .fetch();
        } else {
            reservations = List.of(); // 빈 리스트
        }

        return reservations.stream()
                .map(res -> toReservationSearchResponse(res, role))
                .toList();
    }

    private ReservationSearchResponse toReservationSearchResponse(Reservation res, String role) {

        Locker locker;

        if ("KEEPER".equals(role)) {
            locker = res.getKeeper().getLocker();
        } else {
            locker = res.getDropper().getLocker();
        }

        return ReservationSearchResponse.builder()
                .reservationId(res.getReservationId())
                .keeperId(res.getKeeper().getMemberId())
                .dropperId(res.getDropper().getMemberId())
                .state(res.getState().name())
                .role(role)
                .lockerName(locker.getLockerName())
                .address(locker.getAddress())
                .addressDetail(locker.getAddressDetail())
                .lockerImage(getThumbnailUrl(locker))
                .startTime(res.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .endTime(res.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .dateOnly(res.getStartTime().toLocalDate().toString())
                .durationHours(Duration.between(res.getStartTime(), res.getEndTime()).toMinutes() / 60.0)
                .jimTypeResults(res.getReservationJimTypes().stream()
                        .map(j -> new JimTypeResult(j.getJimType().getJimTypeId(), j.getJimType().getTypeName()))
                        .toList())
                .build();
    }

    private String getThumbnailUrl(Locker locker) {
        return locker.getLockerImages().stream()
                .map(LockerImage::getImage)
                .sorted(Comparator.comparing(Image::getImageId))
                .map(Image::getUrl)
                .findFirst()
                .orElse(null);
    }
//
//
//    @Override
//    public Long findMaxReservationIdByMemberId(Long memberId, String role, List<ReservationState> state) {
//        QReservation r = QReservation.reservation;
//        BooleanBuilder where = new BooleanBuilder();
//        if ("KEEPER".equals(role)) {
//            where.and(r.keeper.memberId.eq(memberId));
//        } else {
//            where.and(r.dropper.memberId.eq(memberId));
//        }
//        if (state != null && !state.isEmpty()) {
//            where.and(r.state.in(state));
//        }
//        return query.select(r.reservationId.max())
//                .from(r)
//                .where(where)
//                .fetchOne();
//    }
}
