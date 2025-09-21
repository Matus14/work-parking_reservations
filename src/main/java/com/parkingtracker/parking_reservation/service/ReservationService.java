package com.parkingtracker.parking_reservation.service;


import com.parkingtracker.parking_reservation.DTO.ReservationRequestDTO;
import com.parkingtracker.parking_reservation.DTO.ReservationResponseDTO;
import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import com.parkingtracker.parking_reservation.entity.Reservation;
import com.parkingtracker.parking_reservation.repository.ParkingSpotRepository;
import com.parkingtracker.parking_reservation.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository repository;

    @Autowired
    private ParkingSpotRepository spotRepository;


    public ReservationResponseDTO create(ReservationRequestDTO req){
        if(req.getStartTime().isAfter(req.getEndTime()) || req.getStartTime().isEqual(req.getEndTime())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time cant be after end time");
        }

        ParkingSpot spot = spotRepository.findById(req.getSpotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "parking spot not found"));

        Reservation entity = Reservation.builder()
                .name(req.getName())
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .spot(spot)
                .build();

        Reservation saved = repository.save(entity);
        return toDto(saved);

    }

    public List<ReservationResponseDTO> findAll(){
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public ReservationResponseDTO findById(Long id) {
        Reservation res = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));
        return toDto(res);
    }

    public void delete(Long id){
       if(!repository.existsById(id)){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found for delete");
       }
       repository.deleteById(id);
    }

    private ReservationResponseDTO toDto(Reservation r){
        return new ReservationResponseDTO(
                r.getId(),
                r.getName(),
                r.getStartTime(),
                r.getEndTime(),
                r.getCreatedAt(),
                r.getSpot() != null ? r.getSpot().getId() : null,
                r.getSpot() != null ? r.getSpot().getCode() : null
        );
    }

}
