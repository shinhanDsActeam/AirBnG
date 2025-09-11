package com.airbng.core.domain.jimtype;


import com.airbng.core.domain.base.BaseStatus;
import com.airbng.core.domain.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JimType extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jimTypeId;

    @Column(nullable = false)
    private String typeName;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long pricePerHour;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private BaseStatus status;
}
