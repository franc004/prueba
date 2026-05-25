package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.Min;
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
public class PublicationRequestDto {

    @NotNull
    private Boolean fromUTEC;

    @NotNull
    private Boolean driverToPassenger;

    @NotNull
    @Min(1)
    private Integer seats;

    @NotBlank
    private String titulo;

    private String descripcion;

    @NotBlank
    private String destinationOrOrigin;

    @NotNull
    private LocalDateTime departureTime;

    @NotNull
    private Long authorId;
}
