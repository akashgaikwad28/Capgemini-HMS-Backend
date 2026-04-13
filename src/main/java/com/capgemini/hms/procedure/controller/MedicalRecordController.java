package com.capgemini.hms.procedure.controller;

import com.capgemini.hms.procedure.dto.UndergoesRequest;
import com.capgemini.hms.procedure.entity.Undergoes;
import com.capgemini.hms.procedure.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.capgemini.hms.common.dto.ApiResponse;
import com.capgemini.hms.security.services.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/medical-records")
@Tag(name = "Medical Records & Procedures", description = "Endpoints for recording and retrieving patient procedural history and medical events")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/my")
    @Operation(summary = "Get my medical history", description = "Returns a chronological list of all procedures undergone by the current logged-in patient")
    public ResponseEntity<ApiResponse<List<Undergoes>>> getMyHistory() {
        Integer ssn = getAuthenticatedPatientSsn();
        List<Undergoes> history = medicalRecordService.getPatientMedicalHistory(ssn);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @PostMapping("/procedure")
    @Operation(summary = "Record a procedure", description = "Documents a procedure performed on a patient during a specific stay")
    public ResponseEntity<ApiResponse<String>> recordProcedure(@Valid @RequestBody UndergoesRequest request) {
        medicalRecordService.recordProcedure(
                request.getPatientSsn(),
                request.getProcedureCode(),
                request.getStayId(),
                request.getDateUndergoes(),
                request.getPhysicianId(),
                request.getAssistingNurseId(),
                request.getNotes()
        );
        return ResponseEntity.ok(ApiResponse.success("Medical procedure recorded successfully"));
    }

    @GetMapping("/patient/{ssn}")
    @Operation(summary = "Get patient medical history", description = "Returns a chronological list of all procedures undergone by a patient")
    public ResponseEntity<ApiResponse<List<Undergoes>>> getPatientHistory(@PathVariable Integer ssn) {
        return ResponseEntity.ok(ApiResponse.success(medicalRecordService.getPatientMedicalHistory(ssn)));
    }

    @GetMapping("/stay/{stayId}")
    @Operation(summary = "Get stay procedures", description = "Returns all procedures performed during a specific patient admission")
    public ResponseEntity<ApiResponse<List<Undergoes>>> getStayProcedures(@PathVariable Integer stayId) {
        return ResponseEntity.ok(ApiResponse.success(medicalRecordService.getStayProcedures(stayId)));
    }

    private Integer getAuthenticatedPatientSsn() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails.getPatientSsn() == null) {
            throw new RuntimeException("Your account is not linked to a clinical patient record.");
        }
        return userDetails.getPatientSsn();
    }
}
