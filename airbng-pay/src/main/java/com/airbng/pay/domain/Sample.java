package com.airbng.pay.domain;

import com.airbng.common.base.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Sample extends BaseTime {
    @Id
    private Long id;
}
