package com.parkingtracker.parking_reservation;


import com.parkingtracker.parking_reservation.DTO.ReservationRequestDTO;
import com.parkingtracker.parking_reservation.DTO.ReservationResponseDTO;
import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import com.parkingtracker.parking_reservation.entity.Reservation;
import com.parkingtracker.parking_reservation.repository.ParkingSpotRepository;
import com.parkingtracker.parking_reservation.repository.ReservationRepository;
import com.parkingtracker.parking_reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private ParkingSpotRepository spotRepository;

    @InjectMocks
    private ReservationService service;



    @Test
    void create_throwsBadRequest_whenStartNotBeforeEnd() {
        // Arrange
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setName("Matus");
        req.setStartTime(LocalDateTime.of(2025,1,15,4,0));
        req.setEndTime(LocalDateTime.of(2025,1,15,4,0));
        req.setSpotId(1L);


        // ACt + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("StartTime must be before the endTime");

        verifyNoInteractions(spotRepository, repository);
    }

    @Test
    void create_throwsNotFound_whenSpotMissing() {
        // Arrange
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setName("Matus");
        req.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        req.setEndTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        req.setSpotId(99L);

        when(spotRepository.findById(99L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("parking spot not found");

        verify(spotRepository).findById(99L);
        verify(repository, never()).save(any());
    }

    @Test
    void create_savesReservation_andReturnsResponseDto() {
        // Arrange
        ReservationRequestDTO req = new ReservationRequestDTO();
        req.setName("Matus");
        req.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        req.setEndTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        req.setSpotId(1L);

        ParkingSpot spot = ParkingSpot.builder()
                .id(1L)
                .code("A1")
                .active(true)
                .build();

        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(repository.save(any(Reservation.class))).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(5L); // simulácia priradeného ID z DB
            return r;
        });

        // Act
        ReservationResponseDTO dto = service.create(req);

        // Assert
        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("Matus");
        assertThat(dto.getSpotId()).isEqualTo(1L);
        assertThat(dto.getSpotCode()).isEqualTo("A1");
        assertThat(dto.getStartTime()).isEqualTo(req.getStartTime());
        assertThat(dto.getEndTime()).isEqualTo(req.getEndTime());

        verify(spotRepository).findById(1L);
        verify(repository).save(any(Reservation.class));
    }
}




