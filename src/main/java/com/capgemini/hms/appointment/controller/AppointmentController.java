package com.capgemini.hms.appointment.controller;

import com.capgemini.hms.appointment.dto.AppointmentDTO;
import com.capgemini.hms.appointment.entity.Appointment;
import com.capgemini.hms.appointment.service.AppointmentService;
import com.capgemini.hms.common.dto.ApiResponse;
import com.capgemini.hms.common.dto.PagedResponse;
import com.capgemini.hms.nurse.repository.NurseRepository;
import com.capgemini.hms.patient.repository.PatientRepository;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import com.capgemini.hms.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointment Scheduling", description = "Endpoints for booking, viewing, and managing patient-physician appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientRepository patientRepository;
    private final PhysicianRepository physicianRepository;
    private final NurseRepository nurseRepository;

    public AppointmentController(AppointmentService appointmentService,
                                 PatientRepository patientRepository,
                                 PhysicianRepository physicianRepository,
                                 NurseRepository nurseRepository) {
        this.appointmentService = appointmentService;
        this.patientRepository = patientRepository;
        this.physicianRepository = physicianRepository;
        this.nurseRepository = nurseRepository;
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get my appointments", description = "Returns all appointments scheduled for the current logged-in patient")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getMyAppointments() {
        Integer ssn = getAuthenticatedPatientSsn();
        List<AppointmentDTO> appointments = appointmentService.getPatientAppointments(ssn).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get all appointments", description = "Returns a paginated master list of all active appointments")
    public ResponseEntity<ApiResponse<PagedResponse<AppointmentDTO>>> getAllAppointments(Pageable pageable) {
        Page<Appointment> page = appointmentService.getAllAppointments(pageable);
        List<AppointmentDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PagedResponse<AppointmentDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get appointment by ID", description = "Returns details of a specific active appointment")
    public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointmentById(@PathVariable Integer id) {
        return appointmentService.getAppointmentById(id)
                .map(a -> ResponseEntity.ok(ApiResponse.success(convertToDTO(a))))
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE', 'DOCTOR', 'PATIENT')")
    @Operation(summary = "Book an appointment", description = "Schedules a new meeting. Patients can only book for themselves.")
    public ResponseEntity<ApiResponse<AppointmentDTO>> bookAppointment(@Valid @RequestBody AppointmentDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            dto.setPatientSsn(getAuthenticatedPatientSsn());
        }
        
        // Validation: Doctor must exist
        if (!physicianRepository.existsById(dto.getPhysicianId())) {
             throw new RuntimeException("Validation Failed: Physician with ID " + dto.getPhysicianId() + " does not exist.");
        }

        Appointment appointment = convertToEntity(dto);
        Appointment saved = appointmentService.bookAppointment(appointment);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(saved), "Appointment booked successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE', 'DOCTOR')")
    @Operation(summary = "Update appointment", description = "Updates an existing appointment record")
    public ResponseEntity<ApiResponse<AppointmentDTO>> updateAppointment(@PathVariable Integer id, @Valid @RequestBody AppointmentDTO dto) {
        dto.setAppointmentId(id);
        Appointment appointment = convertToEntity(dto);
        Appointment updated = appointmentService.updateAppointment(appointment);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updated), "Appointment updated successfully"));
    }

    @GetMapping("/physician/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Get physician schedule", description = "Returns all active appointments for a specific doctor")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getPhysicianSchedule(@PathVariable Integer id) {
        List<AppointmentDTO> schedule = appointmentService.getPhysicianSchedule(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    @Operation(summary = "Cancel appointment", description = "Performs a soft-delete on an appointment record")
    public ResponseEntity<ApiResponse<String>> cancelAppointment(@PathVariable Integer id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully"));
    }

    private AppointmentDTO convertToDTO(Appointment a) {
        return new AppointmentDTO(
                a.getAppointmentId(),
                a.getPatient().getSsn(),
                a.getPrepNurse() != null ? a.getPrepNurse().getEmployeeId() : null,
                a.getPhysician().getEmployeeId(),
                a.getStart(),
                a.getEnd(),
                a.getExaminationRoom()
        );
    }

    private Appointment convertToEntity(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(dto.getAppointmentId() != null ? dto.getAppointmentId() : (int) (System.currentTimeMillis() & 0xfffffff));
        appointment.setPatient(patientRepository.findById(dto.getPatientSsn())
                .orElseThrow(() -> new RuntimeException("Patient not found")));
        appointment.setPhysician(physicianRepository.findById(dto.getPhysicianId())
                .orElseThrow(() -> new RuntimeException("Physician not found")));
        
        if (dto.getPrepNurseId() != null) {
            appointment.setPrepNurse(nurseRepository.findById(dto.getPrepNurseId())
                    .orElseThrow(() -> new RuntimeException("Nurse not found")));
        }
        
        appointment.setStart(dto.getStart());
        appointment.setEnd(dto.getEnd());
        appointment.setExaminationRoom(dto.getExaminationRoom());
        return appointment;
    }

    private Integer getAuthenticatedPatientSsn() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getPatientSsn() == null) {
            throw new RuntimeException("Your account is not linked to a clinical patient record.");
        }
        return userDetails.getPatientSsn();
    }
}
