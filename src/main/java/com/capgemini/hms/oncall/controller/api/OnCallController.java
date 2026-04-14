package com.capgemini.hms.oncall.controller.api;

import com.capgemini.hms.oncall.dto.OnCallDTO;
import com.capgemini.hms.oncall.entity.OnCall;
import com.capgemini.hms.oncall.service.OnCallService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.capgemini.hms.common.dto.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/shifts")
@Tag(name = "On-Call Assignments", description = "Endpoints for managing nurse on-call schedules and ward coverage")
public class OnCallController {

    private final OnCallService onCallService;

    public OnCallController(OnCallService onCallService) {
        this.onCallService = onCallService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    @Operation(summary = "Assign a shift", description = "Assigns an on-call shift to a nurse for a specific block and floor")
    public ResponseEntity<ApiResponse<OnCallDTO>> assignShift(@Valid @RequestBody OnCallDTO dto) {
        OnCall saved = onCallService.assignShift(
                dto.getNurseId(),
                dto.getBlockFloor(),
                dto.getBlockCode(),
                dto.getOnCallStart(),
                dto.getOnCallEnd()
        );
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(saved), "Shift assigned successfully"));
    }

    @GetMapping("/nurse/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get nurse shifts", description = "Returns all on-call shifts assigned to a specific nurse")
    public ResponseEntity<ApiResponse<List<OnCallDTO>>> getNurseShifts(@PathVariable Integer id) {
        List<OnCallDTO> shifts = onCallService.getNurseShifts(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(shifts));
    }

    @GetMapping("/block/{floor}/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    @Operation(summary = "Get block coverage", description = "Returns all nurse shifts assigned to cover a specific block/floor area")
    public ResponseEntity<ApiResponse<List<OnCallDTO>>> getBlockCoverage(@PathVariable Integer floor, @PathVariable Integer code) {
        List<OnCallDTO> coverage = onCallService.getBlockCoverage(floor, code).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(coverage));
    }

    private OnCallDTO convertToDTO(OnCall o) {
        return new OnCallDTO(
                o.getNurse().getEmployeeId(),
                o.getId().getBlockFloor(),
                o.getId().getBlockCode(),
                o.getId().getOnCallStart(),
                o.getId().getOnCallEnd()
        );
    }
}
