package com.parkingtracker.parking_reservation.repository;

import com.parkingtracker.parking_reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
