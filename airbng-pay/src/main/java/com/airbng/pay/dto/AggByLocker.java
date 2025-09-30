// dto
package com.airbng.pay.dto;

import java.math.BigDecimal;

public record AggByLocker(Long lockerId,
                          BigDecimal totalSales,
                          Long totalCount,
                          BigDecimal totalFee) {}