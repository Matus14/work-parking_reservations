package com.parkingtracker.parking_reservation.service;


import com.parkingtracker.parking_reservation.entity.Reservation;
import com.parkingtracker.parking_reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repository;


    public Reservation create(Reservation reservation){
        return repository.save(reservation);
    }

    public List<Reservation> findAll(){
        return repository.findAll();
    }

    public Reservation findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
}
