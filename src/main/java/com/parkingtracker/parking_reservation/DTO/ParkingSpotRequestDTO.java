package com.parkingtracker.parking_reservation.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParkingSpotRequestDTO {


    @NotBlank
    private String code;


    private boolean isActive = true;
}
