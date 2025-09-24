package com.airbng.consumer.domain.base;


import java.math.BigDecimal;
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
        public BigDecimal discountAmount(BigDecimal baseAmount) {
            return baseAmount.multiply(new BigDecimal("0.2"));
        }
    },
    ONE_DAY_BEFORE {
        @Override
        public BigDecimal discountAmount(BigDecimal baseAmount) {
            return baseAmount.multiply(new BigDecimal("0.1"));
        }
    },
    BEFORE_THAT {
        @Override
        public BigDecimal discountAmount(BigDecimal baseAmount) {
            return BigDecimal.ZERO;
        }
    };

    public abstract BigDecimal discountAmount(BigDecimal baseAmount);

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
