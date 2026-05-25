package com.dbp.democarpultec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDto {

    private Long id;
    private Long publicationId;
    private Long driverId;
    private Long vehicleId;
    private Boolean fromUTEC;
    private String destinationOrOrigin;
    private LocalDateTime departureTime;
}
