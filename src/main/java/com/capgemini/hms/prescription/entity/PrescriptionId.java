package com.capgemini.hms.prescription.entity;

import jakarta.persistence.Column;
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
    @Column(name = "physician")
    private Integer physician;

    @Column(name = "patient")
    private Integer patient;

    @Column(name = "medication")
    private Integer medication;

    @Column(name = "date")
    private LocalDateTime date;
}
