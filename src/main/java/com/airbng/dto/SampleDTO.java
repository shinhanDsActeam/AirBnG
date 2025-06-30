package com.airbng.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

