package com.airbng.pay.domain;

import com.airbng.common.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankInfo {
    @Id
    @Column(columnDefinition = "SMALLINT")
    private Integer bankCode; // 은행 코드

    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private String korCode; // 은행명

    @Column(nullable = false,  columnDefinition = "VARCHAR(30)")
    private String engCode; // 은행명 (영문)

}
