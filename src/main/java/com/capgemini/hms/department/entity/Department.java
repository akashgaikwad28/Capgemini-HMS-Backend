package com.capgemini.hms.department.entity;

import com.capgemini.hms.physician.entity.Physician;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @Column(name = "departmentid")
    private Integer departmentId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "head", referencedColumnName = "employeeid")
    private Physician head;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public Department() {
    }

    public Department(Integer departmentId, String name, Physician head) {
        this.departmentId = departmentId;
        this.name = name;
        this.head = head;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Physician getHead() {
        return head;
    }

    public void setHead(Physician head) {
        this.head = head;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
