package com.capgemini.hms.department.controller;

import com.capgemini.hms.department.dto.DepartmentDTO;
import com.capgemini.hms.department.entity.Department;
import com.capgemini.hms.department.service.DepartmentService;
import com.capgemini.hms.physician.dto.AffiliationRequest;
import com.capgemini.hms.physician.entity.AffiliatedWith;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.PhysicianRepository;
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
@RequestMapping("/api/v1/departments")
@Tag(name = "Department Management", description = "Endpoints for managing hospital departments and staff affiliations")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final PhysicianRepository physicianRepository;

    public DepartmentController(DepartmentService departmentService, PhysicianRepository physicianRepository) {
        this.departmentService = departmentService;
        this.physicianRepository = physicianRepository;
    }

    @GetMapping
    @Operation(summary = "Get all departments", description = "Returns a paginated list of all active hospital departments")
    public ResponseEntity<ApiResponse<PagedResponse<DepartmentDTO>>> getAllDepartments(Pageable pageable) {
        Page<Department> page = departmentService.getAllDepartments(pageable);
        List<DepartmentDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PagedResponse<DepartmentDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Returns details of a specific active department")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentById(@PathVariable Integer id) {
        return departmentService.getDepartmentById(id)
                .map(d -> ResponseEntity.ok(ApiResponse.success(convertToDTO(d))))
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create department", description = "Creates a new department record")
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        Department dept = convertToEntity(departmentDTO);
        Department saved = departmentService.saveDepartment(dept);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(saved), "Department created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update department", description = "Updates an existing department record")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(@PathVariable Integer id, @Valid @RequestBody DepartmentDTO departmentDTO) {
        departmentDTO.setDepartmentId(id);
        Department dept = convertToEntity(departmentDTO);
        Department updated = departmentService.updateDepartment(dept);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updated), "Department updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete department", description = "Performs a soft-delete on a department record")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Integer id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department record deleted successfully"));
    }

    @PutMapping("/{id}/head")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Set department head", description = "Assigns a physician as the head of a department")
    public ResponseEntity<ApiResponse<DepartmentDTO>> setHead(@PathVariable Integer id, @RequestParam Integer physicianId) {
        Department updated = departmentService.setDepartmentHead(id, physicianId);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updated), "Department head updated successfully"));
    }

    @PostMapping("/affiliate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Affiliate physician", description = "Maps a physician to a department")
    public ResponseEntity<ApiResponse<String>> affiliatePhysician(@Valid @RequestBody AffiliationRequest request) {
        departmentService.affiliatePhysician(
                request.getPhysicianId(), 
                request.getDepartmentId(), 
                request.getPrimary());
        return ResponseEntity.ok(ApiResponse.success("Physician affiliated successfully"));
    }

    @GetMapping("/{id}/physicians")
    @Operation(summary = "Get department physicians", description = "Returns a list of employee IDs affiliated with a department")
    public ResponseEntity<ApiResponse<List<Integer>>> getDepartmentPhysicians(@PathVariable Integer id) {
        List<Integer> physicianIds = departmentService.getDepartmentPhysicians(id).stream()
                .map(a -> a.getPhysician().getEmployeeId())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(physicianIds));
    }

    private DepartmentDTO convertToDTO(Department d) {
        return new DepartmentDTO(
                d.getDepartmentId(), 
                d.getName(), 
                d.getHead() != null ? d.getHead().getEmployeeId() : null);
    }

    private Department convertToEntity(DepartmentDTO dto) {
        Physician head = null;
        if (dto.getHeadId() != null) {
            head = physicianRepository.findById(dto.getHeadId())
                    .orElseThrow(() -> new RuntimeException("Physician not found"));
        }
        return new Department(dto.getDepartmentId(), dto.getName(), head);
    }
}
