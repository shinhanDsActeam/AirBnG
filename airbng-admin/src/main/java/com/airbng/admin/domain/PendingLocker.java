package com.airbng.admin.domain;

import com.airbng.common.base.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PendingLocker extends BaseTime {
    @Id
    private Long id;
}
