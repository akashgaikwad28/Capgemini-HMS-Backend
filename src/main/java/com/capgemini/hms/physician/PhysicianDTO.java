package com.capgemini.hms.physician.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PhysicianDTO {
    @NotNull
    @Schema(example = "101", description = "Unique employee ID of the physician")
    private Integer employeeId;

    @NotBlank
    @Schema(example = "Dr. Alice Morgan", description = "Full name of the physician")
    private String name;

    @NotBlank
    @Schema(example = "Head of Surgery", description = "Official job title or position")
    private String position;

    @NotNull
    @Schema(example = "900000001", description = "Social Security Number of the physician")
    private Integer ssn;

    public PhysicianDTO() {}

    public PhysicianDTO(Integer employeeId, String name, String position, Integer ssn) {
        this.employeeId = employeeId;
        this.name = name;
        this.position = position;
        this.ssn = ssn;
    }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Integer getSsn() { return ssn; }
    public void setSsn(Integer ssn) { this.ssn = ssn; }
}
