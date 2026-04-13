package com.capgemini.hms.procedure.entity;

import com.capgemini.hms.nurse.entity.Nurse;
import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.stay.entity.Stay;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "undergoes")
public class Undergoes {

    @EmbeddedId
    private UndergoesId id;

    @ManyToOne
    @MapsId("patient")
    @JoinColumn(name = "patient", referencedColumnName = "ssn")
    private Patient patient;

    @ManyToOne
    @MapsId("procedure")
    @JoinColumn(name = "`procedure`", referencedColumnName = "code")
    private Procedure procedure;

    @ManyToOne
    @MapsId("stay")
    @JoinColumn(name = "stay", referencedColumnName = "stayid")
    private Stay stay;

    @ManyToOne
    @JoinColumn(name = "physician", referencedColumnName = "employeeid")
    private Physician physician;

    @ManyToOne
    @JoinColumn(name = "assistingnurse", referencedColumnName = "employeeid")
    private Nurse assistingNurse;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public Undergoes() {
    }

    public Undergoes(UndergoesId id, Patient patient, Procedure procedure, Stay stay, 
                     Physician physician, Nurse assistingNurse, String notes) {
        this.id = id;
        this.patient = patient;
        this.procedure = procedure;
        this.stay = stay;
        this.physician = physician;
        this.assistingNurse = assistingNurse;
        this.notes = notes;
    }

    public UndergoesId getId() {
        return id;
    }

    public void setId(UndergoesId id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public Stay getStay() {
        return stay;
    }

    public void setStay(Stay stay) {
        this.stay = stay;
    }

    public Physician getPhysician() {
        return physician;
    }

    public void setPhysician(Physician physician) {
        this.physician = physician;
    }

    public Nurse getAssistingNurse() {
        return assistingNurse;
    }

    public void setAssistingNurse(Nurse assistingNurse) {
        this.assistingNurse = assistingNurse;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
