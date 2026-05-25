package com.dbp.democarpultec.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ride_id", "reviewer_id", "reviewed_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ride donde ocurrió la interacción
    @NotNull
    @ManyToOne(optional = false) //muchas reviews para un solo ride
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    // Usuario que califica
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    // Usuario calificado
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "reviewed_id", nullable = false)
    private User reviewed;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(length = 500)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
