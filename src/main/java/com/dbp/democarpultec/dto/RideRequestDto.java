package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {

    @NotNull
    private Long publicationId;

    @NotNull
    private Long driverId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private Boolean fromUTEC;

    @NotBlank
    private String destinationOrOrigin;

    @NotNull
    private LocalDateTime departureTime;
}
