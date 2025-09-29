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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private ParkingSpotRepository spotRepository;

    @InjectMocks
    private ReservationService service;

    @Captor
    private ArgumentCaptor<Reservation> reservationCaptor;

                                // ====== CREATE =======
    @Test
    void create_whenCreatingReservation_thenSavesAndReturnsDtoWithLinkedSpot() {

        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setName("Matus");
        request.setStartTime(LocalDateTime.of(2025,11,20,12,10));
        request.setEndTime(LocalDateTime.of(2025,11,21,11,20));
        request.setSpotId(1L);

        ParkingSpot spot = ParkingSpot.builder()
                .code("A1")
                .id(1L)
                .active(true)
                .build();

        when(spotRepository.findById(1L)).thenReturn(Optional.of(spot));
        when(repository.save(any(Reservation.class)))
                .thenAnswer(inv -> {
                    Reservation s = inv.getArgument(0);
                    s.setId(5L);
                    return s;
                });

        ReservationResponseDTO dto = service.create(request);

        assertThat(dto.getName()).isEqualTo("Matus");
        assertThat(dto.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(dto.getEndTime()).isEqualTo(request.getEndTime());
        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getSpotCode()).isEqualTo("A1");
        assertThat(dto.getSpotId()).isEqualTo(1L);

        verify(repository).save(any(Reservation.class));
        verify(spotRepository).findById(1L);
        verifyNoMoreInteractions(repository, spotRepository);

    }

    @Test
    void create_whenCreatingReservationWithNonexistentSpot_thenThrowsNotFoundAndNoSave() {

        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setName("Matus");
        request.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        request.setEndTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        request.setSpotId(99L);

        when(spotRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(()-> service.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("parking spot not found");

        verify(spotRepository).findById(99L);
        verify(repository, never()).save(any(Reservation.class));
        verifyNoMoreInteractions(repository, spotRepository);
    }

    @Test
    void create_whenCreatingReservationWithInvalidTimeRange_thenThrowsBadRequest(){

        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setName("Matus");
        request.setStartTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        request.setEndTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        request.setSpotId(9L);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("StartTime must be before the endTime");

        verifyNoInteractions(spotRepository, repository);
    }

    @Test
    void create_whenCreatingReservationWithSameStartAndEnd_thenThrowsBadRequest(){

        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setName("Matus");
        request.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        request.setEndTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        request.setSpotId(9L);

        assertThatThrownBy(()-> service.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("StartTime must be before the endTime");

        verifyNoInteractions(spotRepository, repository);
    }


    @Test
    void create_whenCreatingReservation_thenCorrectEntityIsSavedAndMatchingDtoIsReturned(){

        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setName("Matus");
        request.setStartTime(LocalDateTime.of(2025, 1, 1, 10, 0));
        request.setEndTime(LocalDateTime.of(2025, 1, 1, 12, 0));
        request.setSpotId(9L);

        ParkingSpot spot = ParkingSpot.builder()
                .code("A1")
                .active(true)
                .id(9L)
                .build();

        when(spotRepository.findById(9L)).thenReturn(Optional.of(spot));
        when(repository.save(any(Reservation.class)))
                .thenAnswer(inv -> {
                    Reservation r = inv.getArgument(0);
                    r.setId(4L);
                    return r;
                });

        ReservationResponseDTO dto = service.create(request);
        verify(repository).save(reservationCaptor.capture());
        Reservation saved = reservationCaptor.getValue();
        assertThat(saved.getName()).isEqualTo("Matus");
        assertThat(saved.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(saved.getEndTime()).isEqualTo(request.getEndTime());
        assertThat(saved.getSpot().getId()).isEqualTo(9L);
        assertThat(saved.getSpot().getCode()).isEqualTo("A1");
        assertThat(saved.getSpot()).isNotNull();

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.getName()).isEqualTo("Matus");
        assertThat(dto.getStartTime()).isEqualTo(request.getStartTime());
        assertThat(dto.getEndTime()).isEqualTo(request.getEndTime());
        assertThat(dto.getSpotCode()).isEqualTo("A1");
        assertThat(dto.getSpotId()).isEqualTo(9L);

        verify(spotRepository).findById(9L);
        verify(repository).save(any(Reservation.class));
        verifyNoMoreInteractions(repository, spotRepository);
    }



                                // ======== FIND ALL  ========

    @Test
    void showAll_whenFindingAllReservations_thenEntitiesAreMappedToDto() {

        Reservation r1 = Reservation.builder()
                .name("Ivan")
                .startTime(LocalDateTime.of(2025,11,1,1,0))
                .endTime(LocalDateTime.of(2025,2,3,2,1))
                .id(9L)
                .build();

        Reservation r2 = Reservation.builder()
                .name("Jakub")
                .startTime(LocalDateTime.of(2025,11,1,1,0))
                .endTime(LocalDateTime.of(2026,2,3,2,1))
                .id(5L)
                .build();

        when(repository.findAll()).thenReturn(List.of(r1,r2));

        List<ReservationResponseDTO> results = service.findAll();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Ivan");
        assertThat(results.get(0).getStartTime()).isEqualTo(LocalDateTime.of(2025,11,1,1,0));
        assertThat(results.get(0).getEndTime()).isEqualTo(LocalDateTime.of(2025,2,3,2,1));
        assertThat(results.get(0).getId()).isEqualTo(9L);


        assertThat(results.get(1).getName()).isEqualTo("Jakub");
        assertThat(results.get(1).getStartTime()).isEqualTo(LocalDateTime.of(2025,11,1,1,0));
        assertThat(results.get(1).getEndTime()).isEqualTo(LocalDateTime.of(2026,2,3,2,1));
        assertThat(results.get(1).getId()).isEqualTo(5L);


        verify(repository).findAll();
    }

    @Test
    void showAll_whenFindingAllReservationsWithEmptyRepo_thenReturnsEmptyList() {

        when(repository.findAll()).thenReturn(List.of());

        List<ReservationResponseDTO> results = service.findAll();
        assertThat(results).isEmpty();

        verify(repository).findAll();
    }

                                // ======= FIND BY ID ======


    @Test
    void findById_whenFindingReservationById_thenReturnsDtoIfExists() {

        Reservation r = Reservation.builder()
                .name("Ivan")
                .startTime(LocalDateTime.of(2025,11,1,1,0))
                .endTime(LocalDateTime.of(2025,2,3,2,1))
                .id(9L)
                .build();

        when(repository.findById(9L)).thenReturn(Optional.of(r));

        ReservationResponseDTO dto = service.findById(9L);

        assertThat(dto.getName()).isEqualTo("Ivan");
        assertThat(dto.getStartTime()).isEqualTo(LocalDateTime.of(2025,11,1,1,0));
        assertThat(dto.getEndTime()).isEqualTo(LocalDateTime.of(2025,2,3,2,1));
        assertThat(dto.getId()).isEqualTo(9L);

        verify(repository).findById(9L);
    }

    @Test
    void findById_whenFindingReservationByInvalidId_thenExceptionIsThrown() {

        when(repository.findById(9L)).thenReturn(Optional.empty());


        assertThatThrownBy(()-> service.findById(9L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Reservation not found");

        verify(repository).findById(9L);
    }


                                    // ======= DELETE =======


    @Test
    void delete_whenDeletingExistingReservation_thenItIsRemoved(){

        when(repository.existsById(9L)).thenReturn(true);

        service.delete(9L);

        verify(repository).existsById(9L);
        verify(repository).deleteById(9L);
    }

    @Test
    void delete_whenDeletingNonExistingReservation_thenExceptionIsThrownAndDeleteNotCalled() {

        when(repository.existsById(2L)).thenReturn(false);

        assertThatThrownBy(()-> service.delete(2L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Reservation not found for delete");

        verify(repository).existsById(2L);
        verify(repository, never()).deleteById(anyLong());
    }


}




