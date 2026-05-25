package com.dbp.democarpultec.dto;

import com.dbp.democarpultec.model.enums.Carreras;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String studentCode;
    private Carreras career;
    private Integer cycle;
    private Double rating;
}
