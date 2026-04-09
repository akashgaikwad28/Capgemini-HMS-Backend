package com.capgemini.hms.procedure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainedInId implements Serializable {
    @Column(name = "physician")
    private Integer physician;

    @Column(name = "treatment")
    private Integer treatment;
}
