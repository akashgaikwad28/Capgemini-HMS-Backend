package com.capgemini.hms.medication.controller;

import com.capgemini.hms.common.dto.ApiResponse;
import com.capgemini.hms.common.dto.PagedResponse;
import com.capgemini.hms.medication.dto.MedicationDTO;
import com.capgemini.hms.medication.entity.Medication;
import com.capgemini.hms.medication.service.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/medications")
@Tag(name = "Medication Catalog", description = "Endpoints for managing the hospital's pharmacy and medication list")
public class MedicationController {

    private final MedicationService medicationService;

    public MedicationController(MedicationService medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get all medications", description = "Returns a paginated list of active medicines available in the hospital")
    public ResponseEntity<ApiResponse<PagedResponse<MedicationDTO>>> getAllMedications(Pageable pageable) {
        Page<Medication> page = medicationService.getAllMedications(pageable);
        List<MedicationDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PagedResponse<MedicationDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get medication by code", description = "Returns details for a specific active medicine")
    public ResponseEntity<ApiResponse<MedicationDTO>> getMedicationByCode(@PathVariable Integer code) {
        return medicationService.getMedicationByCode(code)
                .map(m -> ResponseEntity.ok(ApiResponse.success(convertToDTO(m))))
                .orElseThrow(() -> new RuntimeException("Medication not found with code: " + code));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add medication", description = "Registers a new medicine in the hospital catalog")
    public ResponseEntity<ApiResponse<MedicationDTO>> addMedication(@Valid @RequestBody MedicationDTO dto) {
        Medication med = convertToEntity(dto);
        Medication saved = medicationService.saveMedication(med);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(saved), "Medication added to catalog successfully"));
    }

}
