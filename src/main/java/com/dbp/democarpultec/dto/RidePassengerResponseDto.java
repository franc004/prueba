package com.dbp.democarpultec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RidePassengerResponseDto {

    private Long id;
    private Long passengerId;
    private Long rideId;
    private Integer seatsReserved;
    private String pickupPoint;
}
