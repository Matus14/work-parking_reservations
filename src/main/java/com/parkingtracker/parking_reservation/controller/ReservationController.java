package com.parkingtracker.parking_reservation.controller;


import com.parkingtracker.parking_reservation.entity.Reservation;
import com.parkingtracker.parking_reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;


    @PostMapping
    public Reservation create(@RequestBody Reservation reservation){
        return service.create(reservation);
    }

    @GetMapping
    public List<Reservation> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Reservation findById(@PathVariable Long id){
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.findById(id);
    }
}
