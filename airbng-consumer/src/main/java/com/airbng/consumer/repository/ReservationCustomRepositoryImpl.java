package com.airbng.consumer.repository;

import com.airbng.common.base.BaseStatus;
import com.airbng.consumer.domain.*;
import com.airbng.consumer.domain.base.MemberRole;
import com.airbng.consumer.domain.base.ReservationState;
import com.airbng.consumer.domain.image.*;
import com.airbng.consumer.domain.jimtype.*;
import com.airbng.consumer.dto.jimType.JimTypeResult;
import com.airbng.consumer.dto.reservation.ReservationSearchResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Repository
@AllArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public List<ReservationSearchResponse> findAllReservationByMemberIdWithCursor(Long memberId, MemberRole role, List<ReservationState> states, Long nextCursorId,
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
        if (MemberRole.KEEPER.equals(role)) {
            roleCondition = r.keeper.memberId.eq(memberId);
        } else if (MemberRole.DROPPER.equals(role)) {
            roleCondition = r.dropper.memberId.eq(memberId);
        }
        // 역할에 따른 조건 where 절에 추가
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
                    .where(where) //roleCondition 포함됨
                    .fetchOne();
            nextCursorId = (maxId != null) ? maxId + 1L : -1L;
        }

        if (nextCursorId != null && nextCursorId > 0L) {
            where.and(r.reservationId.lt(nextCursorId));
        }
        where.and(r.status.eq(BaseStatus.ACTIVE));

        // 쿼리 실행 및 DTO 매핑
        List<Reservation> reservations;

        if (MemberRole.KEEPER.equals(role)) {
            reservations = query
                    .selectFrom(r)
                    .leftJoin(r.keeper, keeper).fetchJoin()
                    .leftJoin(r.locker, l).fetchJoin()
                    .leftJoin(l.lockerImages, li).fetchJoin()
                    .leftJoin(li.image, image).fetchJoin()
                    .leftJoin(r.reservationJimTypes, rjt).fetchJoin()
                    .leftJoin(rjt.jimType, jt).fetchJoin()
                    .where(where)
                    .orderBy(r.reservationId.desc())
                    .limit(limit)
                    .fetch();
        } else if (MemberRole.DROPPER.equals(role)) {
            reservations = query
                    .selectFrom(r)
                    .leftJoin(r.dropper, dropper).fetchJoin()
                    .leftJoin(r.locker, l).fetchJoin()
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

    private ReservationSearchResponse toReservationSearchResponse(Reservation res, MemberRole role) {

        Locker locker = null;

        if (MemberRole.KEEPER.equals(role)) {
            locker = res.getLocker();
        } else if (MemberRole.DROPPER.equals(role))  {
            locker = res.getLocker();
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
                .lockerImage(getLockerUrl(locker))
                .startTime(res.getStartTime())
                .endTime(res.getEndTime())
                .dateOnly(res.getStartTime().toLocalDate().toString())
                .durationHours(Duration.between(res.getStartTime(), res.getEndTime()).toMinutes() / 60.0)
                .jimTypeResults(res.getReservationJimTypes().stream()
                        .map(j -> new JimTypeResult(j.getJimType().getJimTypeId(), j.getJimType().getTypeName()))
                        .toList())
                .build();
    }

    private String getLockerUrl(Locker locker) {
        return locker.getLockerImages().stream()
                .map(LockerImage::getImage)
                .sorted(Comparator.comparing(Image::getImageId))
                .map(Image::getUrl)
                .findFirst()
                .orElse(null);
    }
}
