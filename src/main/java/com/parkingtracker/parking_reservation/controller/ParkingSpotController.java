package com.parkingtracker.parking_reservation.controller;


import com.parkingtracker.parking_reservation.DTO.ParkingSpotRequestDTO;
import com.parkingtracker.parking_reservation.DTO.ParkingSpotResponseDTO;
import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import com.parkingtracker.parking_reservation.service.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService service;


    @PostMapping
    public ParkingSpotResponseDTO create(@Valid @RequestBody ParkingSpotRequestDTO request){
        return service.createSpot(request);
    }

    @GetMapping
    public List<ParkingSpotResponseDTO> all() {
        return service.findAllSpots();
    }

    @GetMapping("/{id}")
    public ParkingSpotResponseDTO oneById(@PathVariable Long id){
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteSpot(id);
    }
}
