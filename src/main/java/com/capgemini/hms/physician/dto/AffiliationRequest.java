package com.capgemini.hms.physician.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class AffiliationRequest {
    @NotNull
    @Schema(example = "101", description = "Employee ID of the physician")
    private Integer physicianId;

    @NotNull
    @Schema(example = "1", description = "ID of the department")
    private Integer departmentId;

    @NotNull
    @Schema(example = "true", description = "Whether this is the physician's primary department")
    private Boolean primary;

    public AffiliationRequest() {}

    public AffiliationRequest(Integer physicianId, Integer departmentId, Boolean primary) {
        this.physicianId = physicianId;
        this.departmentId = departmentId;
        this.primary = primary;
    }

    public Integer getPhysicianId() { return physicianId; }
    public void setPhysicianId(Integer physicianId) { this.physicianId = physicianId; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public Boolean getPrimary() { return primary; }
    public void setPrimary(Boolean primary) { this.primary = primary; }
}
