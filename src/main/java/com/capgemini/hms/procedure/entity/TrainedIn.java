package com.capgemini.hms.procedure.entity;

import com.capgemini.hms.physician.entity.Physician;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "trained_in")
public class TrainedIn {

    @EmbeddedId
    private TrainedInId id;

    @ManyToOne
    @MapsId("physician")
    @JoinColumn(name = "physician", referencedColumnName = "employeeid")
    private Physician physician;

    @ManyToOne
    @MapsId("treatment")
    @JoinColumn(name = "treatment", referencedColumnName = "code")
    private Procedure procedure;

    @Column(name = "certificationdate", nullable = false)
    private LocalDateTime certificationDate;

    @Column(name = "certificationexpires", nullable = false)
    private LocalDateTime certificationExpires;

    public TrainedIn() {
    }

    public TrainedIn(TrainedInId id, Physician physician, Procedure procedure, LocalDateTime certificationDate, LocalDateTime certificationExpires) {
        this.id = id;
        this.physician = physician;
        this.procedure = procedure;
        this.certificationDate = certificationDate;
        this.certificationExpires = certificationExpires;
    }

    public TrainedInId getId() {
        return id;
    }

    public void setId(TrainedInId id) {
        this.id = id;
    }

    public Physician getPhysician() {
        return physician;
    }

    public void setPhysician(Physician physician) {
        this.physician = physician;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public LocalDateTime getCertificationDate() {
        return certificationDate;
    }

    public void setCertificationDate(LocalDateTime certificationDate) {
        this.certificationDate = certificationDate;
    }

    public LocalDateTime getCertificationExpires() {
        return certificationExpires;
    }

    public void setCertificationExpires(LocalDateTime certificationExpires) {
        this.certificationExpires = certificationExpires;
    }
}
