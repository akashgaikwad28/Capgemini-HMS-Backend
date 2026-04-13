package com.capgemini.hms.procedure;

import com.capgemini.hms.nurse.entity.Nurse;
import com.capgemini.hms.nurse.repository.NurseRepository;
import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import com.capgemini.hms.procedure.entity.Procedure;
import com.capgemini.hms.procedure.entity.Undergoes;
import com.capgemini.hms.procedure.entity.UndergoesId;
import com.capgemini.hms.procedure.repository.ProcedureRepository;
import com.capgemini.hms.procedure.repository.UndergoesRepository;
import com.capgemini.hms.procedure.service.MedicalRecordService;
import com.capgemini.hms.room.entity.Block;
import com.capgemini.hms.room.entity.BlockId;
import com.capgemini.hms.room.entity.Room;
import com.capgemini.hms.stay.entity.Stay;
import com.capgemini.hms.stay.repository.StayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class MedicalRecordServiceTest {

    @Mock
    private UndergoesRepository undergoesRepository;

    @Mock
    private ProcedureRepository procedureRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private StayRepository stayRepository;

    @Mock
    private PhysicianRepository physicianRepository;

    @Mock
    private NurseRepository nurseRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private Patient patient;
    private Procedure procedure;
    private Stay stay;
    private Physician physician;
    private Nurse nurse;

    @BeforeEach
    void setUp() {
        physician = new Physician(1, "Dr. John", "Internist", 111111111);
        patient = new Patient(1001, "Alice", "Street 1", "555-1001", "INS-001", physician);
        procedure = new Procedure(6, "Reversible Pancreomyoplasty", 5600.0);
        nurse = new Nurse(101, "Carla", "Head Nurse", true, 111111110);
        stay = new Stay(3215, patient, new Room(111, "Single", new Block(new BlockId(1, 2)), false),
                LocalDateTime.of(2026, 4, 9, 9, 0), null, "notes");
    }

    @Test
    void recordProcedure_shouldSaveValidRecord() {
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));
        when(procedureRepository.findById(6)).thenReturn(Optional.of(procedure));
        when(stayRepository.findById(3215)).thenReturn(Optional.of(stay));
        when(physicianRepository.findById(1)).thenReturn(Optional.of(physician));
        when(nurseRepository.findById(101)).thenReturn(Optional.of(nurse));
        when(undergoesRepository.save(any(Undergoes.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Undergoes result = medicalRecordService.recordProcedure(1001, 6, 3215,
                LocalDateTime.of(2026, 4, 9, 10, 0), 1, 101, "Procedure done");

        assertEquals(1001, result.getId().getPatient());
        assertEquals(6, result.getId().getProcedure());
        assertEquals(3215, result.getId().getStay());
        assertEquals("Procedure done", result.getNotes());
    }

    @Test
    void recordProcedure_shouldRejectMissingPatient() {
        when(patientRepository.findById(1001)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                medicalRecordService.recordProcedure(1001, 6, 3215, LocalDateTime.now(), 1, 101, "notes"));
        assertTrue(ex.getMessage().contains("Patient not found"));
    }

    @Test
    void recordProcedure_shouldRejectMissingNurse() {
        when(patientRepository.findById(1001)).thenReturn(Optional.of(patient));
        when(procedureRepository.findById(6)).thenReturn(Optional.of(procedure));
        when(stayRepository.findById(3215)).thenReturn(Optional.of(stay));
        when(physicianRepository.findById(1)).thenReturn(Optional.of(physician));
        when(nurseRepository.findById(101)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                medicalRecordService.recordProcedure(1001, 6, 3215, LocalDateTime.now(), 1, 101, "notes"));
        assertTrue(ex.getMessage().contains("Nurse not found"));
    }

    @Test
    void getPatientMedicalHistory_shouldReturnList() {
        Undergoes undergoes = new Undergoes(new UndergoesId(1001, 6, 3215, LocalDateTime.of(2026, 4, 9, 10, 0)),
                patient, procedure, stay, physician, nurse, "notes");
        when(undergoesRepository.findByPatient_Ssn(1001)).thenReturn(List.of(undergoes));

        List<Undergoes> result = medicalRecordService.getPatientMedicalHistory(1001);

        assertEquals(1, result.size());
    }

    @Test
    void getStayProcedures_shouldReturnList() {
        Undergoes undergoes = new Undergoes(new UndergoesId(1001, 6, 3215, LocalDateTime.of(2026, 4, 9, 10, 0)),
                patient, procedure, stay, physician, nurse, "notes");
        when(undergoesRepository.findByStay_StayId(3215)).thenReturn(List.of(undergoes));

        List<Undergoes> result = medicalRecordService.getStayProcedures(3215);

        assertEquals(1, result.size());
    }
}
