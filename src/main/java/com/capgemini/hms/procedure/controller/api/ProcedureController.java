package com.capgemini.hms.procedure.controller.api;

import com.capgemini.hms.procedure.dto.ProcedureDTO;
import com.capgemini.hms.procedure.entity.Procedure;
import com.capgemini.hms.procedure.service.ProcedureService;
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
@RequestMapping("/api/v1/procedures")
@Tag(name = "Procedure Management", description = "Endpoints for managing the hospital's list of medical procedures and services")
public class ProcedureController {

    private final ProcedureService procedureService;

    public ProcedureController(ProcedureService procedureService) {
        this.procedureService = procedureService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get all procedures", description = "Returns a paginated list of all active medical procedures and their costs")
    public ResponseEntity<ApiResponse<PagedResponse<ProcedureDTO>>> getAllProcedures(Pageable pageable) {
        Page<Procedure> page = procedureService.getAllProcedures(pageable);
        List<ProcedureDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PagedResponse<ProcedureDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get procedure by code", description = "Returns details for a specific active medical procedure")
    public ResponseEntity<ApiResponse<ProcedureDTO>> getProcedureByCode(@PathVariable Integer code) {
        return procedureService.getProcedureByCode(code)
                .map(p -> ResponseEntity.ok(ApiResponse.success(convertToDTO(p))))
                .orElseThrow(() -> new RuntimeException("Procedure not found with code: " + code));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add procedure", description = "Registers a new medical procedure in the hospital catalog")
    public ResponseEntity<ApiResponse<ProcedureDTO>> addProcedure(@Valid @RequestBody ProcedureDTO dto) {
        Procedure procedure = convertToEntity(dto);
        Procedure saved = procedureService.saveProcedure(procedure);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(saved), "Procedure added to catalog successfully"));
    }

    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update procedure", description = "Updates an existing medical procedure record")
    public ResponseEntity<ApiResponse<ProcedureDTO>> updateProcedure(@PathVariable Integer code, @Valid @RequestBody ProcedureDTO dto) {
        dto.setCode(code);
        Procedure procedure = convertToEntity(dto);
        Procedure updated = procedureService.updateProcedure(procedure);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updated), "Procedure details updated successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Search procedures", description = "Filters active procedures by name with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ProcedureDTO>>> searchProcedures(@RequestParam String query, Pageable pageable) {
        Page<Procedure> page = procedureService.searchProcedures(query, pageable);
        List<ProcedureDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PagedResponse<ProcedureDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    public ResponseEntity<ApiResponse<String>> deleteProcedure(@PathVariable Integer code) {
        procedureService.deleteProcedure(code);
        return ResponseEntity.ok(ApiResponse.success("Procedure record deleted successfully"));
    }

    private ProcedureDTO convertToDTO(Procedure p) {
        return new ProcedureDTO(p.getCode(), p.getName(), p.getCost());
    }

    private Procedure convertToEntity(ProcedureDTO dto) {
        return new Procedure(dto.getCode(), dto.getName(), dto.getCost());
    }
}
