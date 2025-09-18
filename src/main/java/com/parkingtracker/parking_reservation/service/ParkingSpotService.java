package com.parkingtracker.parking_reservation.service;


import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import com.parkingtracker.parking_reservation.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingSpotService {

    @Autowired
    private ParkingSpotRepository repository;

    public ParkingSpot createSpot(ParkingSpot spot){
        return repository.save(spot);
    }

    public List<ParkingSpot> findAllSpots() {
        return repository.findAll();
    }

    public ParkingSpot findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void  deleteSpot(Long id){
        repository.deleteById(id);
    }
}
