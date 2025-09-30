// dto
package com.airbng.pay.dto;

import java.math.BigDecimal;

public record AggTotal(BigDecimal totalSales, Long totalCount, BigDecimal totalFee) {}