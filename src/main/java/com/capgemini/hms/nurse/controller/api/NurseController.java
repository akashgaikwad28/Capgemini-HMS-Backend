package com.capgemini.hms.nurse.controller.api;

import com.capgemini.hms.common.service.StaffService;
import com.capgemini.hms.nurse.dto.NurseDTO;
import com.capgemini.hms.nurse.entity.Nurse;
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
@RequestMapping("/api/v1/nurses")
@Tag(name = "Nursing Staff Management", description = "Endpoints for managing hospital nurses and their qualification status")
public class NurseController {

    private final StaffService staffService;

    public NurseController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    @Operation(summary = "Get all nurses", description = "Returns a paginated list of all active nursing staff")
    public ResponseEntity<ApiResponse<PagedResponse<NurseDTO>>> getAllNurses(Pageable pageable) {
        Page<Nurse> page = staffService.getAllNurses(pageable);
        List<NurseDTO> content = page.getContent().stream()
                .map(n -> new NurseDTO(n.getEmployeeId(), n.getName(), n.getPosition(), n.getRegistered(), n.getSsn()))
                .collect(Collectors.toList());
        PagedResponse<NurseDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get nurse by ID", description = "Returns profile details for a specific active nurse")
    public ResponseEntity<ApiResponse<NurseDTO>> getNurseById(@PathVariable Integer id) {
        return staffService.getNurseById(id)
                .map(n -> ResponseEntity.ok(ApiResponse.success(new NurseDTO(n.getEmployeeId(), n.getName(), n.getPosition(), n.getRegistered(), n.getSsn()))))
                .orElseThrow(() -> new RuntimeException("Nurse not found with ID: " + id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new nurse", description = "Adds a new nurse record to the system")
    public ResponseEntity<ApiResponse<NurseDTO>> createNurse(@Valid @RequestBody NurseDTO nurseDTO) {
        Nurse nurse = new Nurse(
                nurseDTO.getEmployeeId(),
                nurseDTO.getName(),
                nurseDTO.getPosition(),
                nurseDTO.getRegistered(),
                nurseDTO.getSsn()
        );
        Nurse saved = staffService.saveNurse(nurse);
        return ResponseEntity.ok(ApiResponse.success(new NurseDTO(saved.getEmployeeId(), saved.getName(), saved.getPosition(), saved.getRegistered(), saved.getSsn()), "Nurse record created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update nurse profile", description = "Updates an existing nurse record")
    public ResponseEntity<ApiResponse<NurseDTO>> updateNurse(@PathVariable Integer id, @Valid @RequestBody NurseDTO nurseDTO) {
        nurseDTO.setEmployeeId(id);
        Nurse nurse = new Nurse(
                nurseDTO.getEmployeeId(),
                nurseDTO.getName(),
                nurseDTO.getPosition(),
                nurseDTO.getRegistered(),
                nurseDTO.getSsn()
        );
        Nurse updated = staffService.updateNurse(nurse);
        return ResponseEntity.ok(ApiResponse.success(new NurseDTO(updated.getEmployeeId(), updated.getName(), updated.getPosition(), updated.getRegistered(), updated.getSsn()), "Nurse record updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove nurse record", description = "Performs a soft-delete on a nurse profile")
    public ResponseEntity<ApiResponse<String>> deleteNurse(@PathVariable Integer id) {
        staffService.deleteNurse(id);
        return ResponseEntity.ok(ApiResponse.success("Nurse record deleted successfully"));
    }
}
