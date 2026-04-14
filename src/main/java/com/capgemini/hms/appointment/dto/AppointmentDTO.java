package com.capgemini.hms.appointment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AppointmentDTO {
    @Schema(example = "1", description = "Unique ID of the appointment")
    private Integer appointmentId;

    @NotNull
    @Schema(example = "100000001", description = "SSN of the patient")
    private Integer patientSsn;

    @Schema(example = "John Doe", description = "Full name of the patient")
    private String patientName;

    @Schema(example = "501", description = "Employee ID of the nurse preparing the appointment (optional)")
    private Integer prepNurseId;

    @NotNull
    @Schema(example = "101", description = "Employee ID of the physician")
    private Integer physicianId;

    @Schema(example = "Dr. Smith", description = "Full name of the physician")
    private String physicianName;

    @NotNull
    @Schema(example = "2026-04-10T14:30:00", description = "Start time of the appointment")
    private LocalDateTime start;

    @NotNull
    @Schema(example = "2026-04-10T15:00:00", description = "End time of the appointment")
    private LocalDateTime end;

    @NotBlank
    @Schema(example = "Room A-102", description = "Physical location or room for the examination")
    private String examinationRoom;

    public AppointmentDTO() {}

    public AppointmentDTO(Integer appointmentId, Integer patientSsn, Integer prepNurseId, 
                          Integer physicianId, LocalDateTime start, LocalDateTime end, String examinationRoom) {
        this.appointmentId = appointmentId;
        this.patientSsn = patientSsn;
        this.prepNurseId = prepNurseId;
        this.physicianId = physicianId;
        this.start = start;
        this.end = end;
        this.examinationRoom = examinationRoom;
    }

    public AppointmentDTO(Integer appointmentId, Integer patientSsn, String patientName, Integer prepNurseId, 
                          Integer physicianId, String physicianName, LocalDateTime start, LocalDateTime end, String examinationRoom) {
        this.appointmentId = appointmentId;
        this.patientSsn = patientSsn;
        this.patientName = patientName;
        this.prepNurseId = prepNurseId;
        this.physicianId = physicianId;
        this.physicianName = physicianName;
        this.start = start;
        this.end = end;
        this.examinationRoom = examinationRoom;
    }

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public Integer getPatientSsn() { return patientSsn; }
    public void setPatientSsn(Integer patientSsn) { this.patientSsn = patientSsn; }

    public Integer getPrepNurseId() { return prepNurseId; }
    public void setPrepNurseId(Integer prepNurseId) { this.prepNurseId = prepNurseId; }

    public Integer getPhysicianId() { return physicianId; }
    public void setPhysicianId(Integer physicianId) { this.physicianId = physicianId; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public String getExaminationRoom() { return examinationRoom; }
    public void setExaminationRoom(String examinationRoom) { this.examinationRoom = examinationRoom; }
}
