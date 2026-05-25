package com.dbp.democarpultec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDto {

    private Long id;
    private Long ownerId;
    private String plate;
    private String brand;
    private String model;
    private String color;
    private Integer seats;
}
