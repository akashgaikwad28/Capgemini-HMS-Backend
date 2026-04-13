package com.capgemini.hms.procedure;

import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import com.capgemini.hms.procedure.dto.CertificationRequest;
import com.capgemini.hms.procedure.entity.Procedure;
import com.capgemini.hms.procedure.entity.TrainedIn;
import com.capgemini.hms.procedure.entity.TrainedInId;
import com.capgemini.hms.procedure.repository.ProcedureRepository;
import com.capgemini.hms.procedure.repository.TrainedInRepository;
import com.capgemini.hms.procedure.service.CertificationService;
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
class CertificationServiceTest {

    @Mock
    private TrainedInRepository trainedInRepository;

    @Mock
    private PhysicianRepository physicianRepository;

    @Mock
    private ProcedureRepository procedureRepository;

    @InjectMocks
    private CertificationService certificationService;

    private CertificationRequest request;
    private Physician physician;
    private Procedure procedure;

    @BeforeEach
    void setUp() {
        physician = new Physician(3, "Dr. Turk", "Surgeon", 333333333);
        procedure = new Procedure(1, "Procedure A", 1500.0);
        request = new CertificationRequest();
        request.setPhysicianId(3);
        request.setProcedureCode(1);
        request.setCertificationDate(LocalDateTime.of(2026, 1, 1, 0, 0));
        request.setCertificationExpires(LocalDateTime.of(2026, 12, 31, 23, 59));
    }

    @Test
    void certifyPhysician_shouldSaveValidCertification() {
        when(physicianRepository.findById(3)).thenReturn(Optional.of(physician));
        when(procedureRepository.findById(1)).thenReturn(Optional.of(procedure));
        when(trainedInRepository.save(any(TrainedIn.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainedIn result = certificationService.certifyPhysician(request);

        assertEquals(3, result.getId().getPhysician());
        assertEquals(1, result.getId().getTreatment());
    }

    @Test
    void certifyPhysician_shouldRejectInvalidDates() {
        request.setCertificationExpires(LocalDateTime.of(2025, 1, 1, 0, 0));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> certificationService.certifyPhysician(request));
        assertTrue(ex.getMessage().contains("expiry date cannot be before"));
    }

    @Test
    void certifyPhysician_shouldRejectMissingPhysician() {
        when(physicianRepository.findById(3)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> certificationService.certifyPhysician(request));
        assertTrue(ex.getMessage().contains("Physician not found"));
    }

    @Test
    void getPhysicianCertifications_shouldReturnList() {
        TrainedIn trainedIn = new TrainedIn(new TrainedInId(3, 1), physician, procedure,
                request.getCertificationDate(), request.getCertificationExpires());
        when(trainedInRepository.findByPhysician_EmployeeId(3)).thenReturn(List.of(trainedIn));

        List<TrainedIn> result = certificationService.getPhysicianCertifications(3);

        assertEquals(1, result.size());
    }
}
