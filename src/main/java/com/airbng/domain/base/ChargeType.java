package com.airbng.domain.base;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 수수료 정책 이넘 클래스
 * 나중에 가격 기반 정책을 하고자!! 이넘으로 관리
 * */
public enum ChargeType {

    SAME_DAY {
        @Override
        public Long discountAmount() {
            return 2000L;
        }
    },
    ONE_DAY_BEFORE {
        @Override
        public Long discountAmount() {
            return 1000L;
        }
    },
    BEFORE_THAT {
        @Override
        public Long discountAmount() {
            return 0L;
        }
    };

    public abstract Long discountAmount();

    public static ChargeType from(LocalDateTime startTime){
        long dayBetween = ChronoUnit.DAYS.between(LocalDate.now(), startTime.toLocalDate());
        if(dayBetween<1){
            return SAME_DAY;
        } else if (dayBetween == 1) {
            return ONE_DAY_BEFORE;
        }else {
            return BEFORE_THAT;
        }
    }
}
