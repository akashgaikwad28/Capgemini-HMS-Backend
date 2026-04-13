package com.capgemini.hms.prescription.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class PrescriptionId implements Serializable {
    private Integer physician;
    private Integer patient;
    private Integer medication;
    private LocalDateTime date;

    public PrescriptionId() {
    }

    public PrescriptionId(Integer physician, Integer patient, Integer medication, LocalDateTime date) {
        this.physician = physician;
        this.patient = patient;
        this.medication = medication;
        this.date = date;
    }

    public Integer getPhysician() {
        return physician;
    }

    public void setPhysician(Integer physician) {
        this.physician = physician;
    }

    public Integer getPatient() {
        return patient;
    }

    public void setPatient(Integer patient) {
        this.patient = patient;
    }

    public Integer getMedication() {
        return medication;
    }

    public void setMedication(Integer medication) {
        this.medication = medication;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionId that = (PrescriptionId) o;
        return Objects.equals(physician, that.physician) && 
               Objects.equals(patient, that.patient) && 
               Objects.equals(medication, that.medication) && 
               Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(physician, patient, medication, date);
    }
}
