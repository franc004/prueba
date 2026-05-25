package com.dbp.democarpultec.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dueño del vehículo
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String plate;

    @NotBlank
    @Column(nullable = false)
    private String brand;

    @NotBlank
    @Column(nullable = false)
    private String model;

    private String color;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer seats;
}
