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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "undergoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Undergoes {

    @EmbeddedId
    private UndergoesId id;

    @ManyToOne
    @MapsId("patient")
    @JoinColumn(name = "patient", referencedColumnName = "ssn")
    private Patient patient;

    @ManyToOne
    @MapsId("procedure")
    @JoinColumn(name = "procedure", referencedColumnName = "code")
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
}
