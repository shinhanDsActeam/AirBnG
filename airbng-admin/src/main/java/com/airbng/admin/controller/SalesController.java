package com.airbng.admin.controller;

import com.airbng.admin.dto.response.PeriodSalesResponse;
import com.airbng.admin.dto.response.StorageSalesResponse;
import com.airbng.admin.exception.SalesException;
import com.airbng.admin.service.SalesService;
import com.airbng.platform.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.airbng.platform.common.response.status.BaseResponseStatus.INVALID_DATE;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/sales/period")
    public BaseResponse<PeriodSalesResponse> findByPeriodSales(LocalDateTime startDate, LocalDateTime endDate) {
        PeriodSalesResponse result = salesService.getPeriodSales(startDate, endDate)
                .orElseThrow(() -> new SalesException(INVALID_DATE));

        return new BaseResponse<>(result);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/sales/storage")
    public BaseResponse<StorageSalesResponse> findByStorageSales(Long lockerId, LocalDateTime startDate, LocalDateTime endDate) {
        StorageSalesResponse result = salesService.getStorageSales(lockerId, startDate, endDate)
                .orElseThrow(() -> new SalesException(INVALID_DATE));

        return new BaseResponse<>(result);
    }
}
