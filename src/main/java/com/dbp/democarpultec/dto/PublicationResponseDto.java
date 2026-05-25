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
public class PublicationResponseDto {

    private Long id;
    private Boolean fromUTEC;
    private Boolean driverToPassenger;
    private Integer seats;
    private String titulo;
    private String descripcion;
    private String destinationOrOrigin;
    private LocalDateTime departureTime;
    private Long authorId;
    private Long rideId;
}
