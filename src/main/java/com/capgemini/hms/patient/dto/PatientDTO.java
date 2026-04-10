package com.capgemini.hms.patient.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatientDTO {
    @NotNull
    @Schema(example = "100000001", description = "Social Security Number of the patient (Primary Key)")
    private Integer ssn;

    @NotBlank
    @Size(min = 2, max = 255)
    @Schema(example = "John Smith", description = "Full name of the patient")
    private String name;

    @NotBlank
    @Schema(example = "123 Medical Lane, Health City, HC 12345", description = "Current residential address")
    private String address;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$", message = "Invalid phone number format")
    @Schema(example = "+1-555-0199", description = "Contact phone number")
    private String phone;

    @NotNull
    @Pattern(regexp = "^[A-Z0-9]{5,20}$", message = "Insurance ID must be 5-20 alphanumeric characters")
    @Schema(example = "INS789456", description = "Patient's insurance identification number")
    private String insuranceId;

    @Schema(example = "101", description = "Employee ID of the Primary Care Physician (PCP)")
    private Integer pcpId;

    public PatientDTO() {}

    public PatientDTO(Integer ssn, String name, String address, String phone, String insuranceId, Integer pcpId) {
        this.ssn = ssn;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.insuranceId = insuranceId;
        this.pcpId = pcpId;
    }

    public Integer getSsn() { return ssn; }
    public void setSsn(Integer ssn) { this.ssn = ssn; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getInsuranceId() { return insuranceId; }
    public void setInsuranceId(String insuranceId) { this.insuranceId = insuranceId; }

    public Integer getPcpId() { return pcpId; }
    public void setPcpId(Integer pcpId) { this.pcpId = pcpId; }
}
