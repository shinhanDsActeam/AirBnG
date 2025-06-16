package com.airbng.domain.jimtype;


import com.airbng.domain.base.BaseTime;
import lombok.*;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JimType extends BaseTime {
    @NonNull
    private Long jimTypeId;
    @NonNull
    private String typeName;
    private String description;
    @NonNull
    private Long pricePerHour;
}
