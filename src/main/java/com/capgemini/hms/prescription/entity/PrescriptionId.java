package com.capgemini.hms.prescription.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionId implements Serializable {
    private Integer physician;
    private Integer patient;
    private Integer medication;
    private LocalDateTime date;
}
