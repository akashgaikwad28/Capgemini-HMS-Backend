package com.capgemini.hms.appointment;

import com.capgemini.hms.appointment.entity.Appointment;
import com.capgemini.hms.appointment.repository.AppointmentRepository;
import com.capgemini.hms.appointment.service.AppointmentService;
import com.capgemini.hms.nurse.entity.Nurse;
import com.capgemini.hms.nurse.repository.NurseRepository;
import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
@MockitoSettings(strictness = Strictness.LENIENT)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PhysicianRepository physicianRepository;

    @Mock
    private NurseRepository nurseRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Physician physician;
    private Nurse nurse;
    private Appointment appointment;

    @BeforeEach
    void setUp() {
        physician = new Physician(1, "Dr. John", "Internist", 111111111);
        patient = new Patient(1001, "Alice", "Street 1", "555-1001", "INS-001", physician);
        nurse = new Nurse(101, "Carla", "Head Nurse", true, 111111110);
        appointment = new Appointment(10, patient, nurse, physician,
                LocalDateTime.of(2026, 4, 9, 11, 0),
                LocalDateTime.of(2026, 4, 9, 10, 0),
                "A");
    }

    @Test
    void bookAppointment_shouldSaveValidAppointment() {
        when(patientRepository.existsById(1001)).thenReturn(true);
        when(physicianRepository.existsById(1)).thenReturn(true);
        when(nurseRepository.existsById(101)).thenReturn(true);
        when(appointmentRepository.findOverlappingPhysicianAppointments(any(), any(), any())).thenReturn(List.of());
        when(appointmentRepository.findOverlappingRoomAppointments(any(), any(), any())).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment saved = appointmentService.bookAppointment(appointment);

        assertFalse(saved.getIsDeleted());
        assertEquals(10, saved.getAppointmentId());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void bookAppointment_shouldRejectEndBeforeStart() {
        appointment.setEnd(LocalDateTime.of(2026, 4, 9, 9, 0));
        when(patientRepository.existsById(1001)).thenReturn(true);
        when(physicianRepository.existsById(1)).thenReturn(true);
        when(nurseRepository.existsById(101)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> appointmentService.bookAppointment(appointment));
        assertTrue(ex.getMessage().contains("End time cannot be before start time"));
    }

    @Test
    void bookAppointment_shouldRejectWhenPatientMissing() {
        when(patientRepository.existsById(1001)).thenReturn(false);
        when(physicianRepository.existsById(1)).thenReturn(true);
        when(nurseRepository.existsById(101)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> appointmentService.bookAppointment(appointment));
        assertTrue(ex.getMessage().contains("Patient not found"));
    }

    @Test
    void bookAppointment_shouldRejectWhenDoctorBusy() {
        when(patientRepository.existsById(1001)).thenReturn(true);
        when(physicianRepository.existsById(1)).thenReturn(true);
        when(nurseRepository.existsById(101)).thenReturn(true);
        when(appointmentRepository.findOverlappingPhysicianAppointments(any(), any(), any()))
                .thenReturn(List.of(new Appointment(11, patient, nurse, physician, appointment.getEnd(), appointment.getStart(), "B")));
        when(appointmentRepository.findOverlappingRoomAppointments(any(), any(), any())).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> appointmentService.bookAppointment(appointment));
        assertTrue(ex.getMessage().contains("Physician is already busy"));
    }

    @Test
    void updateAppointment_shouldUpdateExistingAppointment() {
        Appointment existing = new Appointment(10, patient, nurse, physician, appointment.getEnd(), appointment.getStart(), "A");
        existing.setIsDeleted(false);
        Appointment updated = new Appointment(10, patient, nurse, physician,
                LocalDateTime.of(2026, 4, 9, 12, 0),
                LocalDateTime.of(2026, 4, 9, 11, 0),
                "C");

        when(appointmentRepository.findById(10)).thenReturn(Optional.of(existing));
        when(patientRepository.existsById(1001)).thenReturn(true);
        when(physicianRepository.existsById(1)).thenReturn(true);
        when(nurseRepository.existsById(101)).thenReturn(true);
        when(appointmentRepository.findOverlappingPhysicianAppointments(any(), any(), any())).thenReturn(List.of());
        when(appointmentRepository.findOverlappingRoomAppointments(any(), any(), any())).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.updateAppointment(updated);

        assertEquals("C", result.getExaminationRoom());
        assertEquals(LocalDateTime.of(2026, 4, 9, 11, 0), result.getStart());
        assertEquals(LocalDateTime.of(2026, 4, 9, 12, 0), result.getEnd());
    }

    @Test
    void getPhysicianSchedule_shouldReturnList() {
        when(appointmentRepository.findByPhysicianActive(1)).thenReturn(List.of(appointment));

        List<Appointment> result = appointmentService.getPhysicianSchedule(1);

        assertEquals(1, result.size());
        verify(appointmentRepository).findByPhysicianActive(1);
    }

    @Test
    void getAllAppointments_shouldReturnActivePage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(appointmentRepository.findAllActive(pageable)).thenReturn(new PageImpl<>(List.of(appointment)));

        Page<Appointment> result = appointmentService.getAllAppointments(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deleteAppointment_shouldSoftDelete() {
        when(appointmentRepository.findById(10)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        appointmentService.deleteAppointment(10);

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsDeleted());
    }
}
