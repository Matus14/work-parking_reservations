package com.parkingtracker.parking_reservation.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParkingSpotResponseDTO {

    private Long id;
    private String code;
    private boolean isActive;
}
