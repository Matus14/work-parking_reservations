package com.parkingtracker.parking_reservation.controller;


import com.parkingtracker.parking_reservation.DTO.ReservationRequestDTO;
import com.parkingtracker.parking_reservation.DTO.ReservationResponseDTO;
import com.parkingtracker.parking_reservation.entity.Reservation;
import com.parkingtracker.parking_reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService service;


    @PostMapping
    public ReservationResponseDTO create(@Valid @RequestBody ReservationRequestDTO req){
        return service.create(req);
    }

    @GetMapping
    public List<ReservationResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ReservationResponseDTO findById(@PathVariable Long id){
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.findById(id);
    }
}
