package com.capgemini.hms.stay;

import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.room.entity.Block;
import com.capgemini.hms.room.entity.BlockId;
import com.capgemini.hms.room.entity.Room;
import com.capgemini.hms.room.repository.RoomRepository;
import com.capgemini.hms.stay.entity.Stay;
import com.capgemini.hms.stay.repository.StayRepository;
import com.capgemini.hms.stay.service.StayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StayServiceTest {

    @Mock
    private StayRepository stayRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private StayService stayService;

    private Patient patient;
    private Room room;
    private Stay stay;

    @BeforeEach
    void setUp() {
        Physician physician = new Physician(1, "Dr. John", "Internist", 111111111);
        patient = new Patient(1001, "Alice", "Street 1", "555-1001", "INS-001", physician);
        room = new Room(101, "Single", new Block(new BlockId(1, 1)), false);
        room.setIsDeleted(false);

        stay = new Stay(3215, patient, room, LocalDateTime.of(2026, 4, 9, 9, 0), null, "Initial notes");
        stay.setIsDeleted(false);
    }

    @Test
    void checkInPatient_shouldSaveStayAndOccupyRoom() {
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));
        when(roomRepository.findById(101)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stayRepository.save(any(Stay.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Stay result = stayService.checkInPatient(1001, 101, LocalDateTime.of(2026, 4, 9, 9, 0), "Admitted");

        assertFalse(result.getIsDeleted());
        assertTrue(room.getUnavailable());
        verify(stayRepository).save(any(Stay.class));
    }

    @Test
    void checkInPatient_shouldRejectOccupiedRoom() {
        room.setUnavailable(true);
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));
        when(roomRepository.findById(101)).thenReturn(Optional.of(room));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> stayService.checkInPatient(1001, 101, LocalDateTime.of(2026, 4, 9, 9, 0), "Admitted"));
        assertTrue(ex.getMessage().contains("currently occupied"));
    }

    @Test
    void checkOutPatient_shouldSetEndAndFreeRoom() {
        room.setUnavailable(true);
        stay.setStayEnd(null);
        when(stayRepository.findById(3215)).thenReturn(Optional.of(stay));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stayRepository.save(any(Stay.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Stay result = stayService.checkOutPatient(3215, "Recovered");

        assertNotNull(result.getStayEnd());
        assertFalse(room.getUnavailable());
        assertTrue(result.getNotes().contains("Checkout Notes"));
    }

    @Test
    void getAllStays_shouldReturnActivePage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(stayRepository.findAllActive(pageable)).thenReturn(new PageImpl<>(List.of(stay)));

        Page<Stay> result = stayService.getAllStays(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateStay_shouldUpdateFields() {
        Stay existing = new Stay(3215, patient, room, LocalDateTime.of(2026, 4, 9, 9, 0), null, "Old");
        existing.setIsDeleted(false);
        Stay updated = new Stay(3215, patient, room, LocalDateTime.of(2026, 4, 10, 9, 0), LocalDateTime.of(2026, 4, 11, 9, 0), "Updated");

        when(stayRepository.findById(3215)).thenReturn(Optional.of(existing));
        when(stayRepository.save(any(Stay.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Stay result = stayService.updateStay(updated);

        assertEquals("Updated", result.getNotes());
        assertEquals(LocalDateTime.of(2026, 4, 10, 9, 0), result.getStayStart());
        assertEquals(LocalDateTime.of(2026, 4, 11, 9, 0), result.getStayEnd());
    }

    @Test
    void deleteStay_shouldSoftDelete() {
        when(stayRepository.findById(3215)).thenReturn(Optional.of(stay));
        when(stayRepository.save(any(Stay.class))).thenAnswer(invocation -> invocation.getArgument(0));

        stayService.deleteStay(3215);

        ArgumentCaptor<Stay> captor = ArgumentCaptor.forClass(Stay.class);
        verify(stayRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsDeleted());
    }

    @Test
    void getActiveStays_shouldReturnActiveStays() {
        when(stayRepository.findByStayEndIsNullActive()).thenReturn(List.of(stay));

        List<Stay> result = stayService.getActiveStays();

        assertEquals(1, result.size());
    }
}
