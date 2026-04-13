package com.capgemini.hms.reporting.controller;

import com.capgemini.hms.reporting.dto.DashboardSummaryDTO;
import com.capgemini.hms.reporting.dto.DepartmentStatsDTO;
import com.capgemini.hms.reporting.service.ReportingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.capgemini.hms.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Reporting & Analytics", description = "Endpoints for hospital administrators to view high-level summaries and statistics")
public class DashboardController {

    private final ReportingService reportingService;

    public DashboardController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary", description = "Returns a high-level overview of hospital resources (Patients, Physicians, Occupancy, Revenue)")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary() {
        return ResponseEntity.ok(ApiResponse.success(reportingService.getSummary()));
    }

    @GetMapping("/departments")
    @Operation(summary = "Get department statistics", description = "Returns detailed statistics for each hospital department")
    public ResponseEntity<ApiResponse<List<DepartmentStatsDTO>>> getDepartmentStats() {
        return ResponseEntity.ok(ApiResponse.success(reportingService.getDepartmentStats()));
    }
}
