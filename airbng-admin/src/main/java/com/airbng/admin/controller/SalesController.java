package com.airbng.admin.controller;

import com.airbng.admin.dto.response.PeriodSalesResponse;
import com.airbng.admin.dto.response.StorageSalesResponse;
import com.airbng.admin.exception.SalesException;
import com.airbng.admin.service.SalesService;
import com.airbng.common.base.LockerType;
import com.airbng.platform.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public BaseResponse<Page<PeriodSalesResponse>> findByPeriodSales(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("settlementDate").descending());
        Page<PeriodSalesResponse> result = salesService.getPeriodSales(startDate, endDate, pageable);
//        if (result.isEmpty()) throw new SalesException(INVALID_DATE);

        return new BaseResponse<>(result);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/storage")
    public BaseResponse<Page<StorageSalesResponse>> findByStorageSales(
            LockerType lockerType,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size ){

        Pageable pageable = PageRequest.of(page, size, Sort.by("totalSales").descending());
        Page<StorageSalesResponse> result = salesService.getStorageSales(lockerType, startDate, endDate, pageable);
//        if (result.isEmpty()) throw new SalesException(INVALID_DATE);

        return new BaseResponse<>(result);
    }
}
