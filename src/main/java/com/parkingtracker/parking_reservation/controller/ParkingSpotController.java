package com.parkingtracker.parking_reservation.controller;


import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import com.parkingtracker.parking_reservation.service.ParkingSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService service;


    @PostMapping
    public ParkingSpot create(@RequestBody ParkingSpot spot){
        return service.createSpot(spot);
    }

    @GetMapping
    public List<ParkingSpot> all() {
        return service.findAllSpots();
    }

    @GetMapping("/{id}")
    public ParkingSpot oneById(@PathVariable Long id){
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.deleteSpot(id);
    }
}
