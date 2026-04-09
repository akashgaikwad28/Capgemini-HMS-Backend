package com.capgemini.hms.prescription.entity;

import com.capgemini.hms.appointment.entity.Appointment;
import com.capgemini.hms.medication.entity.Medication;
import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.physician.entity.Physician;
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
@Table(name = "prescribes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @EmbeddedId
    private PrescriptionId id;

    @ManyToOne
    @MapsId("physician")
    @JoinColumn(name = "physician", referencedColumnName = "employeeid")
    private Physician physician;

    @ManyToOne
    @MapsId("patient")
    @JoinColumn(name = "patient", referencedColumnName = "ssn")
    private Patient patient;

    @ManyToOne
    @MapsId("medication")
    @JoinColumn(name = "medication", referencedColumnName = "code")
    private Medication medication;

    @ManyToOne
    @JoinColumn(name = "appointment", referencedColumnName = "appointmentid")
    private Appointment appointment;

    @Column(name = "dose", nullable = false)
    private String dose;
}
