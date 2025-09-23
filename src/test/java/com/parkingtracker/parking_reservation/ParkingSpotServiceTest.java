package com.parkingtracker.parking_reservation;


import com.parkingtracker.parking_reservation.DTO.ParkingSpotRequestDTO;
import com.parkingtracker.parking_reservation.DTO.ParkingSpotResponseDTO;
import com.parkingtracker.parking_reservation.entity.ParkingSpot;
import com.parkingtracker.parking_reservation.repository.ParkingSpotRepository;
import com.parkingtracker.parking_reservation.service.ParkingSpotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotServiceTest {

    @Mock
    private ParkingSpotRepository repository;

    @InjectMocks
    private ParkingSpotService service;

    @Captor
    private ArgumentCaptor<ParkingSpot> spotCaptor;


                                   // ====== CREATE =====

    @Test
    void create_checkOfTheDataAvailabilityForSave() {

        ParkingSpotRequestDTO request = new ParkingSpotRequestDTO();
        request.setCode("A1");
        request.setActive(true);

        when(repository.existsByCode("A1")).thenReturn(false);
        when(repository.save(any(ParkingSpot.class)))
                .thenAnswer(inv -> {
                    ParkingSpot s = inv.getArgument(0);
                    s.setId(1L);
                    return s;
                });

        ParkingSpotResponseDTO dto = service.createSpot(request);

        assertThat(dto.getCode()).isEqualTo("A1");
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.isActive()).isTrue();

        verify(repository).save(any(ParkingSpot.class));
    }

    @Test
    void create_checkIfThereIsDuplicityInCreateSpot() {

        ParkingSpotRequestDTO request = new ParkingSpotRequestDTO();
        request.setCode("A1");
        request.setActive(true);

        when(repository.existsByCode("A1")).thenReturn(true);

        assertThatThrownBy(() -> service.createSpot(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Parking spot code already exists");

        verify(repository, never()).save(any(ParkingSpot.class));
    }

    @Test
    void create_checkIfParkingSpotCodeIsEmpty() {

        ParkingSpotRequestDTO request = new ParkingSpotRequestDTO();
        request.setCode("  ");
        request.setActive(true);

        assertThatThrownBy(()-> service.createSpot(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Can not be null or empty");

        verify(repository, never()).save(any(ParkingSpot.class));
    }

    @Test
    void create_checkIfParkingSpotCodeIsNull() {

        ParkingSpotRequestDTO request = new ParkingSpotRequestDTO();
        request.setCode(null);
        request.setActive(true);

        assertThatThrownBy(() -> service.createSpot(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Can not be null or empty");

    }

    @Test
    void create_takesValuesFromRequestAndSendsProperEntityToRepository() {

        ParkingSpotRequestDTO request = new ParkingSpotRequestDTO();
        request.setCode("B2");
        request.setActive(false);

        when(repository.existsByCode("B2")).thenReturn(false);
        when(repository.save(any(ParkingSpot.class)))
                .thenAnswer(inv -> {
                    ParkingSpot s = inv.getArgument(0);
                    s.setId(3L);
                    return s;
                });

        ParkingSpotResponseDTO dto = service.createSpot(request);

        verify(repository).save(spotCaptor.capture());
        ParkingSpot saved = spotCaptor.getValue();
        assertThat(saved.getCode()).isEqualTo("B2");
        assertThat(saved.isActive()).isFalse();

        assertThat(dto.getCode()).isEqualTo("B2");
        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.isActive()).isFalse();

    }


                                 //====== SHOW ALL ======
    @Test
    void showAll_returnsListOfDto() {

        ParkingSpot s1 = ParkingSpot.builder().code("A1").id(1L).active(true).build();
        ParkingSpot s2 = ParkingSpot.builder().code("B2").id(2L).active(false).build();
        when(repository.findAll()).thenReturn(List.of(s1,s2));


        List<ParkingSpotResponseDTO> result = service.findAllSpots();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("A1");
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).getCode()).isEqualTo("A1");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).isActive()).isFalse();

        verify(repository).findAll();

    }

    @Test
    void showAll_returnEmptyListWhenNoDataAvailable() {

        when(repository.findAll()).thenReturn(List.of());

        List<ParkingSpotResponseDTO> result = service.findAllSpots();

        assertThat(result).isEmpty();
        verify(repository).findAll();

    }


                              //====== SHOW BY ID ======

    @Test
    void showById_returnsDtoWhenExists() {

        ParkingSpot s = ParkingSpot.builder().id(4L).code("12F").active(true).build();
        when(repository.findById(4L)).thenReturn(Optional.of(s));

        ParkingSpotResponseDTO dto = service.findById(4L);

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getCode()).isEqualTo("12F");
        assertThat(dto.isActive()).isTrue();

        verify(repository).findById(4L);

    }

    @Test
    void showById_throwsNotFoundMessage (){

        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> service.findById(5L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Parking spot not found");

        verify(repository).findById(5L);
    }


                                   //====== DELETE ======


    @Test
    void delete_removeSpotIfExists() {

        when(repository.existsById(82L)).thenReturn(true);

        service.deleteSpot(82L);

        verify(repository).existsById(82L);
        verify(repository).deleteById(82L);
    }

    @Test
    void delete_throwsNotFoundMessage() {

        when(repository.existsById(3L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteSpot(3L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Parking spot not found for delete");

        verify(repository).existsById(3L);
        verify(repository, never()).deleteById(anyLong());
    }
}
