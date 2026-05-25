package com.dbp.democarpultec.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Publicación que originó este viaje
    @OneToOne(optional = false)
    @JoinColumn(name = "publication_id", nullable = false, unique = true)
    private Publication publication;

    // Usuario conductor del viaje confirmado
    @ManyToOne(optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    // Vehículo usado para el viaje
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // true = sale desde UTEC
    // false = va hacia UTEC
    @Column(nullable = false)
    private Boolean fromUTEC;

    // Si fromUTEC = true, representa destino
    // Si fromUTEC = false, representa origen
    @Column(nullable = false)
    private String destinationOrOrigin;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    // Pasajeros confirmados del ride
    @Builder.Default
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RidePassenger> passengers = new ArrayList<>();

    // Reviews asociadas al ride
    @Builder.Default
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

}
