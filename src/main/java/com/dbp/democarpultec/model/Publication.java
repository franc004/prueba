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
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // true = sale desde UTEC
    // false = va hacia UTEC
    @Column(nullable = false)
    private Boolean fromUTEC;

    // true = publicación de conductor
    // false = publicación de pasajero
    @Column(nullable = false)
    private Boolean driverToPassenger;

    // Si driverToPassenger = true, seats = asientos disponibles
    // Si driverToPassenger = false, seats = asientos solicitados
    @Column(nullable = false)
    private Integer seats;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    // Si fromUTEC = true, representa el destino
    // Si fromUTEC = false, representa el origen hacia UTEC
    @Column(nullable = false)
    private String destinationOrOrigin;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Una publicación puede tener muchas solicitudes
    @Builder.Default
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestPublication> requests = new ArrayList<>();

    // Una publicación puede terminar generando un ride
    @OneToOne(mappedBy = "publication", cascade = CascadeType.ALL)
    private Ride ride;
}
