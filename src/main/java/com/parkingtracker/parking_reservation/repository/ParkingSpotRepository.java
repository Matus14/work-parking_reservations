package com.parkingtracker.parking_reservation.repository;

import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    boolean existsByCode(String code);

}
