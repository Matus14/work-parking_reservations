package com.parkingtracker.parking_reservation.service;


import com.parkingtracker.parking_reservation.DTO.ParkingSpotRequestDTO;
import com.parkingtracker.parking_reservation.DTO.ParkingSpotResponseDTO;
import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import com.parkingtracker.parking_reservation.repository.ParkingSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ParkingSpotService {

    @Autowired
    private ParkingSpotRepository repository;

    public ParkingSpotResponseDTO createSpot(ParkingSpotRequestDTO request){
       if(request.getCode() == null || request.getCode().trim().isEmpty()){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not be null or empty");
       }
       if(repository.existsByCode(request.getCode().trim())){
           throw new ResponseStatusException(HttpStatus.CONFLICT, "Parking spot code already exists");
       }

       ParkingSpot entity = ParkingSpot.builder()
               .code(request.getCode().trim())
               .active(request.isActive())
               .build();

       ParkingSpot saved = repository.save(entity);
       return toDto(saved);
    }

    public List<ParkingSpotResponseDTO> findAllSpots() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public ParkingSpotResponseDTO findById(Long id) {
        ParkingSpot spt = repository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Parking spot not found"));
        return toDto(spt);

    }

    public void  deleteSpot(Long id){
        if(!repository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking spot not found for delete");
        }
        repository.deleteById(id);
    }

    private ParkingSpotResponseDTO toDto(ParkingSpot p){
        return new ParkingSpotResponseDTO(
                p.getId(),
                p.getCode(),
                p.isActive()
        );
    }
}
