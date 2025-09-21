package com.airbng.admin.controller;

import com.airbng.admin.dto.response.PeriodSalesResponse;
import com.airbng.admin.dto.response.StorageSalesResponse;
import com.airbng.admin.exception.SalesException;
import com.airbng.admin.service.SalesService;
import com.airbng.platform.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.airbng.platform.common.response.status.BaseResponseStatus.INVALID_DATE;

@RestController
@RequestMapping("/admin/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/period")
    public BaseResponse<List<PeriodSalesResponse>> findByPeriodSales(
            @RequestParam @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") LocalDateTime endDate) {
        List<PeriodSalesResponse> result = salesService.getPeriodSales(startDate, endDate);
        if (result.isEmpty()) throw new SalesException(INVALID_DATE);

        return new BaseResponse<>(result);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/storage")
    public BaseResponse<StorageSalesResponse> findByStorageSales(
            Long lockerId,
            @RequestParam @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") LocalDateTime startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") LocalDateTime endDate) {
        StorageSalesResponse result = salesService.getStorageSales(lockerId, startDate, endDate)
                .orElseThrow(() -> new SalesException(INVALID_DATE));

        return new BaseResponse<>(result);
    }
}
