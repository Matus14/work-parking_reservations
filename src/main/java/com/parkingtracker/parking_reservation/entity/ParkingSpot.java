package com.parkingtracker.parking_reservation.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_spot", uniqueConstraints = {
        @UniqueConstraint(name = "uk_spot_code", columnNames = "code") // preco prave to co je v zatvorke
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 50, unique = true)
    private String code;  // napr. A1, B2

    @Column(nullable = false)
    private boolean active = true;  // defaultne je miesto aktívne
}


