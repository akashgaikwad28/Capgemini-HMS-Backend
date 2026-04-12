package com.capgemini.hms.appointment.entity;

import com.capgemini.hms.nurse.entity.Nurse;
import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.physician.entity.Physician;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @Column(name = "appointmentid")
    private Integer appointmentId;

    @ManyToOne
    @JoinColumn(name = "patient", referencedColumnName = "ssn")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "prepnurse", referencedColumnName = "employeeid")
    private Nurse prepNurse;

    @ManyToOne
    @JoinColumn(name = "physician", referencedColumnName = "employeeid")
    private Physician physician;

    @Column(name = "end_time")
    private LocalDateTime end;

    @Column(name = "start_time")
    private LocalDateTime start;

    @Column(name = "examinationroom", nullable = false)
    private String examinationRoom;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public Appointment() {
    }

    public Appointment(Integer appointmentId, Patient patient, Nurse prepNurse, Physician physician, LocalDateTime end, LocalDateTime start, String examinationRoom) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.prepNurse = prepNurse;
        this.physician = physician;
        this.end = end;
        this.start = start;
        this.examinationRoom = examinationRoom;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Nurse getPrepNurse() {
        return prepNurse;
    }

    public void setPrepNurse(Nurse prepNurse) {
        this.prepNurse = prepNurse;
    }

    public Physician getPhysician() {
        return physician;
    }

    public void setPhysician(Physician physician) {
        this.physician = physician;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public String getExaminationRoom() {
        return examinationRoom;
    }

    public void setExaminationRoom(String examinationRoom) {
        this.examinationRoom = examinationRoom;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
