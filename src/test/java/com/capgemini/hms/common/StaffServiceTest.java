package com.capgemini.hms.common;

import com.capgemini.hms.common.service.StaffService;
import com.capgemini.hms.nurse.entity.Nurse;
import com.capgemini.hms.nurse.repository.NurseRepository;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.PhysicianRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private PhysicianRepository physicianRepository;

    @Mock
    private NurseRepository nurseRepository;

    @InjectMocks
    private StaffService staffService;

    private Physician physician;
    private Nurse nurse;

    @BeforeEach
    void setUp() {
        physician = new Physician(1, "Dr. John", "Internist", 111111111);
        physician.setIsDeleted(false);

        nurse = new Nurse(101, "Carla", "Head Nurse", true, 111111110);
        nurse.setIsDeleted(false);
    }

    @Test
    void savePhysician_shouldMarkActiveAndSave() {
        when(physicianRepository.save(any(Physician.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Physician saved = staffService.savePhysician(physician);

        assertFalse(saved.getIsDeleted());
        verify(physicianRepository).save(physician);
    }

    @Test
    void getAllPhysicians_shouldReturnActivePage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(physicianRepository.findAllActive(pageable)).thenReturn(new PageImpl<>(List.of(physician)));

        Page<Physician> result = staffService.getAllPhysicians(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updatePhysician_shouldUpdateAndSave() {
        Physician existing = new Physician(1, "Old", "Old Position", 999);
        existing.setIsDeleted(false);
        Physician updated = new Physician(1, "Dr. John Updated", "Consultant", 123);

        when(physicianRepository.findById(1)).thenReturn(Optional.of(existing));
        when(physicianRepository.save(any(Physician.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Physician result = staffService.updatePhysician(updated);

        assertEquals("Dr. John Updated", result.getName());
        assertEquals("Consultant", result.getPosition());
        assertEquals(123, result.getSsn());
    }

    @Test
    void deletePhysician_shouldSoftDelete() {
        when(physicianRepository.findById(1)).thenReturn(Optional.of(physician));
        when(physicianRepository.save(any(Physician.class))).thenAnswer(invocation -> invocation.getArgument(0));

        staffService.deletePhysician(1);

        ArgumentCaptor<Physician> captor = ArgumentCaptor.forClass(Physician.class);
        verify(physicianRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsDeleted());
    }

    @Test
    void searchPhysicians_shouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(physicianRepository.searchActive("john", pageable)).thenReturn(new PageImpl<>(List.of(physician)));

        Page<Physician> result = staffService.searchPhysicians("john", pageable);

        assertEquals(1, result.getTotalElements());
        verify(physicianRepository).searchActive("john", pageable);
    }

    @Test
    void saveNurse_shouldMarkActiveAndSave() {
        when(nurseRepository.save(any(Nurse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Nurse saved = staffService.saveNurse(nurse);

        assertFalse(saved.getIsDeleted());
        verify(nurseRepository).save(nurse);
    }

    @Test
    void updateNurse_shouldUpdateAndSave() {
        Nurse existing = new Nurse(101, "Old", "Nurse", true, 222);
        existing.setIsDeleted(false);
        Nurse updated = new Nurse(101, "Laverne", "Senior Nurse", false, 333);

        when(nurseRepository.findById(101)).thenReturn(Optional.of(existing));
        when(nurseRepository.save(any(Nurse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Nurse result = staffService.updateNurse(updated);

        assertEquals("Laverne", result.getName());
        assertEquals("Senior Nurse", result.getPosition());
        assertFalse(result.getRegistered());
    }

    @Test
    void deleteNurse_shouldSoftDelete() {
        when(nurseRepository.findById(101)).thenReturn(Optional.of(nurse));
        when(nurseRepository.save(any(Nurse.class))).thenAnswer(invocation -> invocation.getArgument(0));

        staffService.deleteNurse(101);

        ArgumentCaptor<Nurse> captor = ArgumentCaptor.forClass(Nurse.class);
        verify(nurseRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsDeleted());
    }

    @Test
    void getRegisteredNurses_shouldReturnRegisteredOnly() {
        when(nurseRepository.findByRegisteredActive(true)).thenReturn(List.of(nurse));

        List<Nurse> result = staffService.getRegisteredNurses();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getRegistered());
    }
    // Added for evaluation commit tracking
}
