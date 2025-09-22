package com.parkingtracker.parking_reservation;


import com.parkingtracker.parking_reservation.DTO.ParkingSpotRequestDTO;
import com.parkingtracker.parking_reservation.DTO.ParkingSpotResponseDTO;
import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import com.parkingtracker.parking_reservation.repository.ParkingSpotRepository;
import com.parkingtracker.parking_reservation.service.ParkingSpotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotServiceTest {

    @Mock
    private ParkingSpotRepository repository;

    @InjectMocks
    private ParkingSpotService service;


    // HAPPY path test
    @Test
    void create_savesNewSpot_andReturnsDto() {
        // Arrange
        ParkingSpotRequestDTO req = new ParkingSpotRequestDTO();
        req.setCode("A1");
        req.setActive(true);

        when(repository.existsByCode("A1")).thenReturn(false);
        when(repository.save(any(ParkingSpot.class)))
                .thenAnswer(inv -> {
                    ParkingSpot s = inv.getArgument(0);
                    s.setId(1L);   // simulujem, že DB pridelila ID
                    return s;
                });

        // Act
        ParkingSpotResponseDTO dto = service.createSpot(req);

        // Assert
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCode()).isEqualTo("A1");
        assertThat(dto.isActive()).isTrue();
        verify(repository).save(any(ParkingSpot.class));
    }

    // ERROR path test duplicity

    @Test
    void create_throwsConflict_whenCodeExists() {
        // Arrange
        ParkingSpotRequestDTO req = new ParkingSpotRequestDTO();
        req.setCode("A1");
        req.setActive(true);

        when(repository.existsByCode("A1")).thenReturn(true);


        // Act + Assert
        assertThatThrownBy(() -> service.createSpot(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already exists");

        verify(repository, never()).save(any(ParkingSpot.class));
    }


    @Test
    void create_throwsBadRequest_whenCodeBlank(){
        // Arrange
        ParkingSpotRequestDTO req = new ParkingSpotRequestDTO();
        req.setCode("  ");
        req.setActive(true);

        // Act + Assert
        assertThatThrownBy(() -> service.createSpot(req))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("code is required");

        verify(repository, never()).save(any());

    }
}
