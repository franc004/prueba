package com.dbp.democarpultec.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.dbp.democarpultec.model.enums.Carreras;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(unique = true)
    private String studentCode;

    private Carreras career;

    private Integer cycle;

    // Rating general del usuario.
    // Puedes actualizarlo desde el service cuando reciba reviews.
    private Double rating;

    // Vehículos del usuario
    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    // Publicaciones creadas por el usuario
    @Builder.Default
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publication> publications = new ArrayList<>();

    // Solicitudes/propuestas hechas por el usuario a publicaciones
    @Builder.Default
    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestPublication> requests = new ArrayList<>();

    // Rides donde este usuario fue conductor
    @Builder.Default
    @OneToMany(mappedBy = "driver")
    private List<Ride> ridesAsDriver = new ArrayList<>();

    // Rides donde este usuario fue pasajero
    @Builder.Default
    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RidePassenger> ridesAsPassenger = new ArrayList<>();

    // Reviews que este usuario escribió
    @Builder.Default
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsWritten = new ArrayList<>();

    // Reviews que este usuario recibió
    @Builder.Default
    @OneToMany(mappedBy = "reviewed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewsReceived = new ArrayList<>();
}
