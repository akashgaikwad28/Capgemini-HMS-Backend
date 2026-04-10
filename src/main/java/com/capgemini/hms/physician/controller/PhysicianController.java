package com.capgemini.hms.physician.controller;

import com.capgemini.hms.common.service.StaffService;
import com.capgemini.hms.physician.dto.PhysicianDTO;
import com.capgemini.hms.physician.entity.Physician;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.capgemini.hms.common.dto.ApiResponse;
import com.capgemini.hms.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/physicians")
@Tag(name = "Physician Management", description = "Endpoints for managing physicians and their profiles")
public class PhysicianController {

    private final StaffService staffService;

    public PhysicianController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    @Operation(summary = "Get all physicians", description = "Returns a paginated list of all active physicians")
    public ResponseEntity<ApiResponse<PagedResponse<PhysicianDTO>>> getAllPhysicians(Pageable pageable) {
        Page<Physician> page = staffService.getAllPhysicians(pageable);
        List<PhysicianDTO> content = page.getContent().stream()
                .map(p -> new PhysicianDTO(p.getEmployeeId(), p.getName(), p.getPosition(), p.getSsn()))
                .collect(Collectors.toList());
        PagedResponse<PhysicianDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get physician by ID", description = "Returns profile details for a specific active physician")
    public ResponseEntity<ApiResponse<PhysicianDTO>> getPhysicianById(@PathVariable Integer id) {
        return staffService.getPhysicianById(id)
                .map(p -> ResponseEntity.ok(ApiResponse.success(new PhysicianDTO(p.getEmployeeId(), p.getName(), p.getPosition(), p.getSsn()))))
                .orElseThrow(() -> new RuntimeException("Physician not found with ID: " + id));
    }


