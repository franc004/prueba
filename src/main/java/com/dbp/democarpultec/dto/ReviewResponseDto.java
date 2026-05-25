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
public class ReviewResponseDto {

    private Long id;
    private Long rideId;
    private Long reviewerId;
    private Long reviewedId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
