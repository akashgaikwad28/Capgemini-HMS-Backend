package com.capgemini.hms.reporting.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class DashboardSummaryDTO {
    @Schema(example = "1250", description = "Total number of patients registered in the system")
    private long totalPatients;

    @Schema(example = "45", description = "Total number of active physicians")
    private long totalPhysicians;

    @Schema(example = "82", description = "Total number of active nursing staff")
    private long totalNurses;

    @Schema(example = "15", description = "Number of rooms currently assigned to patients")
    private long occupiedRooms;

    @Schema(example = "50", description = "Total number of inpatient rooms in the hospital")
    private long totalRooms;

    @Schema(example = "75400.50", description = "Total revenue generated from procedures and stays")
    private Double totalRevenue;

    @Schema(example = "12", description = "Number of appointments scheduled for today")
    private long todayAppointments;

    @Schema(example = "1500", description = "Total number of appointments in the system")
    private long totalAppointments;

    public DashboardSummaryDTO() {}

    public DashboardSummaryDTO(long totalPatients, long totalPhysicians, long totalNurses, 
                               long occupiedRooms, long totalRooms, Double totalRevenue, 
                               long todayAppointments, long totalAppointments) {
        this.totalPatients = totalPatients;
        this.totalPhysicians = totalPhysicians;
        this.totalNurses = totalNurses;
        this.occupiedRooms = occupiedRooms;
        this.totalRooms = totalRooms;
        this.totalRevenue = totalRevenue;
        this.todayAppointments = todayAppointments;
        this.totalAppointments = totalAppointments;
    }

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalPhysicians() { return totalPhysicians; }
    public void setTotalPhysicians(long totalPhysicians) { this.totalPhysicians = totalPhysicians; }

    public long getTotalNurses() { return totalNurses; }
    public void setTotalNurses(long totalNurses) { this.totalNurses = totalNurses; }

    public long getOccupiedRooms() { return occupiedRooms; }
    public void setOccupiedRooms(long occupiedRooms) { this.occupiedRooms = occupiedRooms; }

    public long getTotalRooms() { return totalRooms; }
    public void setTotalRooms(long totalRooms) { this.totalRooms = totalRooms; }

    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getTodayAppointments() { return todayAppointments; }
    public void setTodayAppointments(long todayAppointments) { this.todayAppointments = todayAppointments; }

    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long totalAppointments) { this.totalAppointments = totalAppointments; }
}
