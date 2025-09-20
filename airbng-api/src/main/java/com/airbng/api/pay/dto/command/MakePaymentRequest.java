package com.airbng.api.pay.dto.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MakePaymentRequest {
    @NotNull
    private Long amount;

    @NotNull
    private Long fee;

    @NotNull
    private String method;

    @NotNull @Min(1)
    private Long payerId;

    @NotNull @Min(1)
    private Long payeeId;

    @NotNull @Min(1)
    private Long lockerId;

    @NotNull
    private String idemKeyRaw;
}
