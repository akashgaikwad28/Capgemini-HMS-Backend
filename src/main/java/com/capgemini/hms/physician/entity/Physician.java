package com.capgemini.hms.physician.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "physician")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Physician {

    @Id
    @Column(name = "employeeid")
    private Integer employeeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "position", nullable = false)
    private String position;

    @Column(name = "ssn", nullable = false)
    private Integer ssn;
}
