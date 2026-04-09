package com.capgemini.hms.stay.entity;

import com.capgemini.hms.patient.entity.Patient;
import com.capgemini.hms.room.entity.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stay")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stay {

    @Id
    @Column(name = "stayid")
    private Integer stayId;

    @ManyToOne
    @JoinColumn(name = "patient", referencedColumnName = "ssn")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "room", referencedColumnName = "roomnumber")
    private Room room;

    @Column(name = "staystart", nullable = false)
    private LocalDateTime stayStart;

    @Column(name = "stayend", nullable = false)
    private LocalDateTime stayEnd;
}
