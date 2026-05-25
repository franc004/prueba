package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RidePassengerRequestDto {

    @NotNull
    private Long passengerId;

    @NotNull
    private Long rideId;

    @NotNull
    @Min(1)
    private Integer seatsReserved;

    private String pickupPoint;
}
