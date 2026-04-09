package com.capgemini.hms.physician.entity;

import com.capgemini.hms.department.entity.Department;
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
@Table(name = "affiliated_with")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AffiliatedWith {

    @EmbeddedId
    private AffiliatedWithId id;

    @ManyToOne
    @MapsId("physician")
    @JoinColumn(name = "physician", referencedColumnName = "employeeid")
    private Physician physician;

    @ManyToOne
    @MapsId("department")
    @JoinColumn(name = "department", referencedColumnName = "departmentid")
    private Department department;

    @Column(name = "primaryaffiliation", nullable = false)
    private Boolean primaryAffiliation;
}
