package com.dbp.democarpultec.dto;

import com.dbp.democarpultec.model.enums.Carreras;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String lastName;

    @NotBlank
    @Email
    private String email;

    private String phone;
    private String studentCode;
    private Carreras career;
    private Integer cycle;
    private Double rating;
}
