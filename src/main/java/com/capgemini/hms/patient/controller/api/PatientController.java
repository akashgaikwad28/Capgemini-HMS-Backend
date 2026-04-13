package com.capgemini.hms.patient.controller.api;

import com.capgemini.hms.patient.dto.PatientDTO;
import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.patient.service.PatientService;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.PhysicianRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import com.capgemini.hms.security.services.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.capgemini.hms.common.dto.ApiResponse;
import com.capgemini.hms.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patient Management", description = "Endpoints for registering, searching, and managing patient records")
public class PatientController {

    private final PatientService patientService;
    private final PhysicianRepository physicianRepository;

    public PatientController(PatientService patientService, PhysicianRepository physicianRepository) {
        this.patientService = patientService;
        this.physicianRepository = physicianRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Returns the clinical profile of the current logged-in patient")
    public ResponseEntity<ApiResponse<PatientDTO>> getMyProfile() {
        Integer ssn = getAuthenticatedPatientSsn();
        return patientService.getPatientBySsn(ssn)
                .map(p -> ResponseEntity.ok(ApiResponse.success(convertToDTO(p))))
                .orElseThrow(() -> new RuntimeException("Clinical record not found for your account."));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile", description = "Allows a patient to update their own contact information")
    public ResponseEntity<ApiResponse<PatientDTO>> updateMyProfile(@Valid @RequestBody PatientDTO dto) {
        Integer ssn = getAuthenticatedPatientSsn();
        dto.setSsn(ssn);
        Patient patient = convertToEntity(dto);
        Patient updated = patientService.updatePatient(patient);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updated), "Profile updated successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get all patients", description = "Returns a paginated list of all active patients")
    public ResponseEntity<ApiResponse<PagedResponse<PatientDTO>>> getAllPatients(Pageable pageable) {
        Page<Patient> page = patientService.getAllPatients(pageable);
        List<PatientDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        PagedResponse<PatientDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), 
                page.getTotalElements(), page.getTotalPages(), page.isLast());
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{ssn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and #ssn == principal.patientSsn)")
    @Operation(summary = "Get patient by SSN", description = "Returns detailed information of a single active patient")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientBySsn(@PathVariable Integer ssn) {
        return patientService.getPatientBySsn(ssn)
                .map(p -> ResponseEntity.ok(ApiResponse.success(convertToDTO(p))))
                .orElseThrow(() -> new RuntimeException("Patient not found with SSN: " + ssn));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    @Operation(summary = "Register a new patient", description = "Creates a new patient record in the system")
    public ResponseEntity<ApiResponse<PatientDTO>> registerPatient(@Valid @RequestBody PatientDTO patientDTO) {
        Patient patient = convertToEntity(patientDTO);
        Patient saved = patientService.registerPatient(patient);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(saved), "Patient registered successfully"));
    }

    @PutMapping("/{ssn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    @Operation(summary = "Update patient", description = "Updates an existing patient record")
    public ResponseEntity<ApiResponse<PatientDTO>> updatePatient(@PathVariable Integer ssn, @Valid @RequestBody PatientDTO patientDTO) {
        patientDTO.setSsn(ssn);
        Patient patient = convertToEntity(patientDTO);
        Patient updated = patientService.updatePatient(patient);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updated), "Patient updated successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Search patients", description = "Filters active patients by name or phone number with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<PatientDTO>>> searchPatients(@RequestParam String query, Pageable pageable) {
        Page<Patient> page = patientService.searchPatients(query, pageable);
        List<PatientDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        PagedResponse<PatientDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), 
                page.getTotalElements(), page.getTotalPages(), page.isLast());
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @DeleteMapping("/{ssn}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete patient record", description = "Performs a soft-delete on a patient record by SSN")
    public ResponseEntity<ApiResponse<String>> deletePatient(@PathVariable Integer ssn) {
        patientService.deletePatient(ssn);
        return ResponseEntity.ok(ApiResponse.success("Patient record deleted successfully"));
    }

    private PatientDTO convertToDTO(Patient p) {
        return new PatientDTO(
                p.getSsn(),
                p.getName(),
                p.getAddress(),
                p.getPhone(),
                p.getInsuranceId(),
                p.getPcp() != null ? p.getPcp().getEmployeeId() : null);
    }

    private Patient convertToEntity(PatientDTO dto) {
        Physician pcp = null;
        if (dto.getPcpId() != null) {
            pcp = physicianRepository.findById(dto.getPcpId())
                    .orElseThrow(() -> new RuntimeException("Primary Care Physician not found"));
        }

        return new Patient(
                dto.getSsn(),
                dto.getName(),
                dto.getAddress(),
                dto.getPhone(),
                dto.getInsuranceId(),
                pcp
        );
    }

    private Integer getAuthenticatedPatientSsn() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getPatientSsn() == null) {
            throw new RuntimeException("Your account is not linked to a clinical patient record.");
        }
        return userDetails.getPatientSsn();
    }
}
