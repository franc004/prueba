package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDto {

    @NotNull
    private Long ownerId;

    @NotBlank
    private String plate;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    private String color;

    @NotNull
    @Min(1)
    private Integer seats;
}
