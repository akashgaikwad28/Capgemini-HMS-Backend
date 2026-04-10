package com.capgemini.hms.nurse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NurseDTO {
    @NotNull
    @Schema(example = "501", description = "Unique employee ID of the nurse")
    private Integer employeeId;

    @NotBlank
    @Schema(example = "Nurse Jackie", description = "Full name of the nurse")
    private String name;

    @NotBlank
    @Schema(example = "Head Nurse", description = "Current position or rank within the nursing staff")
    private String position;

    @NotNull
    @Schema(example = "true", description = "Whether the nurse is currently registered/licensed")
    private Boolean registered;

    @NotNull
    @Schema(example = "800000001", description = "Social Security Number of the nurse")
    private Integer ssn;

    public NurseDTO() {}

    public NurseDTO(Integer employeeId, String name, String position, Boolean registered, Integer ssn) {
        this.employeeId = employeeId;
        this.name = name;
        this.position = position;
        this.registered = registered;
        this.ssn = ssn;
    }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public Boolean getRegistered() { return registered; }
    public void setRegistered(Boolean registered) { this.registered = registered; }

    public Integer getSsn() { return ssn; }
    public void setSsn(Integer ssn) { this.ssn = ssn; }
}
