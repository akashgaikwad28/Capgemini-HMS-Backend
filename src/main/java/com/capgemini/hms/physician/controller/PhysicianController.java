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

