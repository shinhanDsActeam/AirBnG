package com.airbng.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class SampleDTO {
    @NotNull
    private String name;
    @NotNull
    private String birth;
    @NotNull
    @Min(1)
    private Long age;
}

