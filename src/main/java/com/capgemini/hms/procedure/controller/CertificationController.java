package com.capgemini.hms.procedure.controller;

import com.capgemini.hms.procedure.dto.CertificationRequest;
import com.capgemini.hms.procedure.entity.TrainedIn;
import com.capgemini.hms.procedure.service.CertificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.capgemini.hms.common.dto.ApiResponse;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/certifications")
@Tag(name = "Physician Certifications", description = "Endpoints for managing medical procedure certifications and physician skills")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @PostMapping
    @Operation(summary = "Record certification", description = "Certifies a physician to perform a specific medical procedure")
    public ResponseEntity<ApiResponse<String>> certifyPhysician(@Valid @RequestBody CertificationRequest request) {
        certificationService.certifyPhysician(request);
        return ResponseEntity.ok(ApiResponse.success("Physician certification recorded successfully"));
    }

    @GetMapping("/physician/{id}")
    @Operation(summary = "Get physician skills", description = "Returns a list of all procedures a specific physician is certified to perform")
    public ResponseEntity<ApiResponse<List<TrainedIn>>> getPhysicianSkills(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(certificationService.getPhysicianCertifications(id)));
    }
}
