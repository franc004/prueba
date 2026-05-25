package com.dbp.democarpultec.dto;

import jakarta.validation.constraints.Max;
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
public class ReviewRequestDto {

    @NotNull
    private Long rideId;

    @NotNull
    private Long reviewerId;

    @NotNull
    private Long reviewedId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}
