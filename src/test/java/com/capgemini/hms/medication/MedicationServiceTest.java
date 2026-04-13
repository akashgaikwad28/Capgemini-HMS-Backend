package com.capgemini.hms.medication;

import com.capgemini.hms.medication.entity.Medication;
import com.capgemini.hms.medication.repository.MedicationRepository;
import com.capgemini.hms.medication.service.MedicationService;
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
class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private MedicationService medicationService;

    private Medication medication;

    @BeforeEach
    void setUp() {
        medication = new Medication(1, "Procrastin-X", "X", "N/A");
        medication.setIsDeleted(false);
    }

    @Test
    void saveMedication_shouldMarkActive() {
        when(medicationRepository.save(any(Medication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Medication saved = medicationService.saveMedication(medication);

        assertFalse(saved.getIsDeleted());
    }

    @Test
    void getAllMedications_shouldReturnActivePage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(medicationRepository.findAllActive(pageable)).thenReturn(new PageImpl<>(List.of(medication)));

        Page<Medication> result = medicationService.getAllMedications(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateMedication_shouldUpdateFields() {
        Medication existing = new Medication(1, "Old", "Old Brand", "Old Desc");
        existing.setIsDeleted(false);
        Medication updated = new Medication(1, "New", "New Brand", "New Desc");

        when(medicationRepository.findById(1)).thenReturn(Optional.of(existing));
        when(medicationRepository.save(any(Medication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Medication result = medicationService.updateMedication(updated);

        assertEquals("New", result.getName());
        assertEquals("New Brand", result.getBrand());
        assertEquals("New Desc", result.getDescription());
    }

    @Test
    void searchMedications_shouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(medicationRepository.searchActive("pro", pageable)).thenReturn(new PageImpl<>(List.of(medication)));

        Page<Medication> result = medicationService.searchMedications("pro", pageable);

        assertEquals(1, result.getTotalElements());
        verify(medicationRepository).searchActive("pro", pageable);
    }

    @Test
    void deleteMedication_shouldSoftDelete() {
        when(medicationRepository.findById(1)).thenReturn(Optional.of(medication));
        when(medicationRepository.save(any(Medication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        medicationService.deleteMedication(1);

        ArgumentCaptor<Medication> captor = ArgumentCaptor.forClass(Medication.class);
        verify(medicationRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsDeleted());
    }
}
