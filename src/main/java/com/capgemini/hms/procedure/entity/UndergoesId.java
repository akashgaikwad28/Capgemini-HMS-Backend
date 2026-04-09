package com.capgemini.hms.procedure.entity;

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

public class UndergoesId implements Serializable {
    @Column(name = "patient")
    private Integer patient;

    @Column(name = "`procedure`")
    private Integer procedure;

    @Column(name = "stay")
    private Integer stay;

    @Column(name = "dateundergoes")
    private LocalDateTime dateUndergoes;
}
