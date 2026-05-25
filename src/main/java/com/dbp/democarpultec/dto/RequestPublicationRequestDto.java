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
public class RequestPublicationRequestDto {

    @NotNull
    private Long publicationId;

    @NotNull
    private Long requesterId;

    @NotNull
    private Boolean requesterIsDriver;

    @NotNull
    @Min(1)
    private Integer seats;

    private String message;
    private String pickupPointOrDestine;
    private String status;
}
