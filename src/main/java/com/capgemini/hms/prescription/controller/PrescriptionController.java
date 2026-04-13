package com.capgemini.hms.prescription.controller;

import com.capgemini.hms.medication.entity.Medication;
import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.prescription.dto.PrescriptionRequest;
import com.capgemini.hms.prescription.entity.Prescription;
import com.capgemini.hms.prescription.entity.PrescriptionId;
import com.capgemini.hms.prescription.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.capgemini.hms.common.dto.ApiResponse;
import com.capgemini.hms.security.services.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/prescriptions")
@Tag(name = "Medication Prescriptions", description = "Endpoints for physicians to prescribe medications to patients")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping("/my")
    @Operation(summary = "Get my prescriptions", description = "Returns all medications prescribed to the current logged-in patient")
    public ResponseEntity<ApiResponse<List<Prescription>>> getMyPrescriptions() {
        Integer ssn = getAuthenticatedPatientSsn();
        List<Prescription> prescriptions = prescriptionService.getPatientPrescriptions(ssn);
        return ResponseEntity.ok(ApiResponse.success(prescriptions));
    }

    @PostMapping
    @Operation(summary = "Create a prescription", description = "Records a new medication order prescribed by a physician for a patient")
    public ResponseEntity<ApiResponse<String>> createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        // Build Prescription objects from Request IDs
        PrescriptionId id = new PrescriptionId(
                request.getPhysicianId(), 
                request.getPatientSsn(), 
                request.getMedicationCode(), 
                request.getDate()
        );
        
        Physician doc = new Physician();
        doc.setEmployeeId(request.getPhysicianId());
        
        Patient pat = new Patient();
        pat.setSsn(request.getPatientSsn());
        
        Medication med = new Medication();
        med.setCode(request.getMedicationCode());
        
        Prescription prescription = new Prescription();
        prescription.setId(id);
        prescription.setPhysician(doc);
        prescription.setPatient(pat);
        prescription.setMedication(med);
        prescription.setDose(request.getDose());

        prescriptionService.createPrescription(prescription, request.getAppointmentId());
        
        return ResponseEntity.ok(ApiResponse.success("Prescription created successfully"));
    }

    @GetMapping("/patient/{ssn}")
    @Operation(summary = "Get patient prescriptions", description = "Returns all medications prescribed to a specific patient")
    public ResponseEntity<ApiResponse<List<Prescription>>> getPatientPrescriptions(@PathVariable Integer ssn) {
        return ResponseEntity.ok(ApiResponse.success(prescriptionService.getPatientPrescriptions(ssn)));
    }

    private Integer getAuthenticatedPatientSsn() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getPatientSsn() == null) {
            throw new RuntimeException("Your account is not linked to a clinical patient record.");
        }
        return userDetails.getPatientSsn();
    }
}
