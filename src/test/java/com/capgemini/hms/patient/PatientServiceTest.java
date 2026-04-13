package com.capgemini.hms.patient;

import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.patient.service.PatientService;
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
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PhysicianRepository physicianRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private Physician physician;

    @BeforeEach
    void setUp() {
        physician = new Physician(1, "Dr. John", "Internist", 111111111);
        patient = new Patient(1001, "Alice", "Street 1", "555-1001", "INS-001", physician);
        patient.setIsDeleted(false);
    }

    @Test
    void registerPatient_shouldSaveNewPatientAndMarkActive() {
        when(patientRepository.existsById(1001)).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient saved = patientService.registerPatient(patient);

        assertFalse(saved.getIsDeleted());
        assertEquals(1001, saved.getSsn());
        verify(patientRepository).save(patient);
    }

    @Test
    void registerPatient_shouldThrowWhenDuplicateExists() {
        when(patientRepository.existsById(1001)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> patientService.registerPatient(patient));
        assertTrue(ex.getMessage().contains("already exists"));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void getAllPatients_shouldReturnActivePatients() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> page = new PageImpl<>(List.of(patient));
        when(patientRepository.findAllActive(pageable)).thenReturn(page);

        Page<Patient> result = patientService.getAllPatients(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Alice", result.getContent().get(0).getName());
        verify(patientRepository).findAllActive(pageable);
    }

    @Test
    void getPatientBySsn_shouldReturnOnlyActivePatient() {
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));

        Optional<Patient> result = patientService.getPatientBySsn(1001);

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    void getPatientBySsn_shouldFilterDeletedPatient() {
        patient.setIsDeleted(true);
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));

        Optional<Patient> result = patientService.getPatientBySsn(1001);

        assertTrue(result.isEmpty());
    }

    @Test
    void updatePatient_shouldUpdateFieldsAndSave() {
        Patient existing = new Patient(1001, "Old", "Old Address", "111", "OLD", physician);
        existing.setIsDeleted(false);
        Patient updated = new Patient(1001, "Alice Updated", "New Address", "555-2000", "INS-002", physician);

        when(patientRepository.findById(1001)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient result = patientService.updatePatient(updated);

        assertEquals("Alice Updated", result.getName());
        assertEquals("New Address", result.getAddress());
        assertEquals("555-2000", result.getPhone());
        assertEquals("INS-002", result.getInsuranceId());
        verify(patientRepository).save(existing);
    }

    @Test
    void deletePatient_shouldMarkPatientDeleted() {
        Patient existing = new Patient(1001, "Alice", "Street 1", "555-1001", "INS-001", physician);
        existing.setIsDeleted(false);
        when(patientRepository.findById(1001)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        patientService.deletePatient(1001);

        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(patientRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsDeleted());
    }
}

// Test environment validated
