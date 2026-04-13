package com.capgemini.hms.prescription;

import com.capgemini.hms.appointment.entity.Appointment;
import com.capgemini.hms.appointment.repository.AppointmentRepository;
import com.capgemini.hms.medication.entity.Medication;
import com.capgemini.hms.medication.repository.MedicationRepository;
import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import com.capgemini.hms.prescription.entity.Prescription;
import com.capgemini.hms.prescription.entity.PrescriptionId;
import com.capgemini.hms.prescription.repository.PrescriptionRepository;
import com.capgemini.hms.prescription.service.PrescriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private PhysicianRepository physicianRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private PrescriptionService prescriptionService;

    private Physician physician;
    private Patient patient;
    private Medication medication;
    private Appointment appointment;
    private Prescription prescription;

    @BeforeEach
    void setUp() {
        physician = new Physician(1, "Dr. John", "Internist", 111111111);
        patient = new Patient(1001, "Alice", "Street 1", "555-1001", "INS-001", physician);
        medication = new Medication(1, "Procrastin-X", "X", "N/A");
        appointment = new Appointment(13216584, patient, null, physician,
                LocalDateTime.of(2026, 4, 9, 11, 0), LocalDateTime.of(2026, 4, 9, 10, 0), "A");

        PrescriptionId id = new PrescriptionId(1, 1001, 1, LocalDateTime.of(2026, 4, 9, 12, 0));
        prescription = new Prescription(id, physician, patient, medication, null, "5");
    }

    @Test
    void createPrescription_shouldSaveWithAppointment() {
        when(physicianRepository.findById(1)).thenReturn(Optional.of(physician));
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));
        when(medicationRepository.findById(1)).thenReturn(Optional.of(medication));
        when(appointmentRepository.findById(13216584)).thenReturn(Optional.of(appointment));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Prescription result = prescriptionService.createPrescription(prescription, 13216584);

        assertNotNull(result.getAppointment());
        assertEquals(13216584, result.getAppointment().getAppointmentId());
        verify(prescriptionRepository).save(any(Prescription.class));
    }

    @Test
    void createPrescription_shouldSaveWithoutAppointment() {
        when(physicianRepository.findById(1)).thenReturn(Optional.of(physician));
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));
        when(medicationRepository.findById(1)).thenReturn(Optional.of(medication));
        when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Prescription result = prescriptionService.createPrescription(prescription, null);

        assertNull(result.getAppointment());
    }

    @Test
    void createPrescription_shouldRejectMissingPatient() {
        when(physicianRepository.findById(1)).thenReturn(Optional.of(physician));
        when(patientRepository.findById(1001)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> prescriptionService.createPrescription(prescription, null));
        assertTrue(ex.getMessage().contains("Patient not found"));
    }

    @Test
    void createPrescription_shouldRejectAppointmentMismatch() {
        Appointment mismatch = new Appointment(13216584,
                new Patient(2002, "Bob", "Street 2", "555-2002", "INS-002", physician),
                null,
                physician,
                appointment.getEnd(),
                appointment.getStart(),
                "A");
        when(physicianRepository.findById(1)).thenReturn(Optional.of(physician));
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));
        when(medicationRepository.findById(1)).thenReturn(Optional.of(medication));
        when(appointmentRepository.findById(13216584)).thenReturn(Optional.of(mismatch));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> prescriptionService.createPrescription(prescription, 13216584));
        assertTrue(ex.getMessage().contains("consistency mismatch"));
    }

    @Test
    void getPatientPrescriptions_shouldReturnList() {
        when(prescriptionRepository.findByPatient_Ssn(1001)).thenReturn(List.of(prescription));

        List<Prescription> result = prescriptionService.getPatientPrescriptions(1001);

        assertEquals(1, result.size());
        verify(prescriptionRepository).findByPatient_Ssn(1001);
    }
}
