package com.capgemini.hms.reporting;

import com.capgemini.hms.appointment.entity.Appointment;
import com.capgemini.hms.appointment.repository.AppointmentRepository;
import com.capgemini.hms.department.entity.Department;
import com.capgemini.hms.department.repository.DepartmentRepository;
import com.capgemini.hms.nurse.entity.Nurse;
import com.capgemini.hms.nurse.repository.NurseRepository;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.physician.entity.AffiliatedWith;
import com.capgemini.hms.physician.entity.AffiliatedWithId;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.AffiliatedWithRepository;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import com.capgemini.hms.procedure.entity.Undergoes;
import com.capgemini.hms.procedure.entity.UndergoesId;
import com.capgemini.hms.procedure.repository.UndergoesRepository;
import com.capgemini.hms.reporting.dto.DepartmentStatsDTO;
import com.capgemini.hms.reporting.dto.DashboardSummaryDTO;
import com.capgemini.hms.reporting.service.ReportingService;
import com.capgemini.hms.room.entity.Room;
import com.capgemini.hms.room.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private PhysicianRepository physicianRepository;
    @Mock private NurseRepository nurseRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private AppointmentRepository appointmentRepository;
    @Mock private UndergoesRepository undergoesRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private AffiliatedWithRepository affiliatedWithRepository;

    @InjectMocks
    private ReportingService reportingService;

    @Test
    void getSummary_shouldNormalizeNullRevenueToZero() {
        when(patientRepository.count()).thenReturn(4L);
        when(physicianRepository.count()).thenReturn(9L);
        when(nurseRepository.count()).thenReturn(3L);
        when(roomRepository.countByUnavailableTrue()).thenReturn(2L);
        when(roomRepository.count()).thenReturn(36L);
        when(undergoesRepository.calculateTotalRevenue()).thenReturn(null);
        when(appointmentRepository.countByStartBetween(any(), any())).thenReturn(5L);

        DashboardSummaryDTO result = reportingService.getSummary();

        assertEquals(4L, result.getTotalPatients());
        assertEquals(9L, result.getTotalPhysicians());
        assertEquals(0.0, result.getTotalRevenue());
        assertEquals(5L, result.getTodayAppointments());
    }

    @Test
    void getDepartmentStats_shouldMapHeadAndCount() {
        Physician head = new Physician(4, "Dr. Cox", "Head Chief", 444444444);
        Department department = new Department(1, "General Medicine", head);
        when(departmentRepository.findAll()).thenReturn(List.of(department));
        when(affiliatedWithRepository.countByDepartment_DepartmentId(1)).thenReturn(8L);

        List<DepartmentStatsDTO> result = reportingService.getDepartmentStats();

        assertEquals(1, result.size());
        assertEquals("General Medicine", result.get(0).getDepartmentName());
        assertEquals("Dr. Cox", result.get(0).getHeadName());
        assertEquals(8L, result.get(0).getPhysicianCount());
    }
}
