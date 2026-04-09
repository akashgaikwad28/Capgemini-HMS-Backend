package com.capgemini.hms.procedure.entity;

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

import java.time.LocalDateTime;

@Entity
@Table(name = "trained_in")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
