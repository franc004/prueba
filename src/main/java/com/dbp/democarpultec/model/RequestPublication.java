package com.dbp.democarpultec.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"publication_id", "requester_id"})
        }
)
public class RequestPublication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Publicación a la que se está respondiendo
    @ManyToOne(optional = false)
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    // Usuario que hace la solicitud/propuesta
    @ManyToOne(optional = false)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    // 1 -> el requester está actuando como conductor
    // 0 -> el requester está actuando como pasajero
    @Column(nullable = false)
    private Boolean requesterIsDriver;

    // Si requesterIsDriver = true, seats = asientos que ofrece
    // Si requesterIsDriver = false, seats = asientos que pide
    @Column(nullable = false)
    private Integer seats;

    private String message;

    // Si requestIsDriver = 1, está actuando como destino final del CONDUCTOR
    // si es 0, es pick up point para el PASAJERO
    private String pickupPointOrDestine;

    // Ejemplo: PENDING, ACCEPTED, REJECTED, CANCELLED
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}
