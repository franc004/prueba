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
public class RequestPublicationResponseDto {

    private Long id;
    private Long publicationId;
    private Long requesterId;
    private Boolean requesterIsDriver;
    private Integer seats;
    private String message;
    private String pickupPointOrDestine;
    private String status;
    private LocalDateTime createdAt;
}
