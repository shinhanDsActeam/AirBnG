package com.airbng.domain;

import com.airbng.domain.base.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location extends BaseTime {
    @NonNull
    private Long locationId;
    @NonNull
    private String address;
    @NonNull
    private String addressEnglish;
}
