package com.capgemini.hms.medication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MedicationDTO {
    @NotNull
    @Schema(example = "1", description = "Unique code for the medication")
    private Integer code;

    @NotBlank
    @Schema(example = "Paracetamol", description = "Generic name of the medicine")
    private String name;

    @NotBlank
    @Schema(example = "Panadol", description = "Commercial brand name of the medicine")
    private String brand;

    @NotBlank
    @Schema(example = "Analgesic and antipyretic medication used to treat fever and mild to moderate pain.", description = "Detailed description of the medication's use")
    private String description;

    public MedicationDTO() {}

    public MedicationDTO(Integer code, String name, String brand, String description) {
        this.code = code;
        this.name = name;
        this.brand = brand;
        this.description = description;
    }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
