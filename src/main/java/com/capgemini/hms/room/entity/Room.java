package com.capgemini.hms.room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @Column(name = "roomnumber")
    private Integer roomNumber;

    @Column(name = "roomtype", nullable = false)
    private String roomType;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "blockfloor", referencedColumnName = "blockfloor"),
        @JoinColumn(name = "blockcode", referencedColumnName = "blockcode")
    })
    private Block block;

    @Column(name = "unavailable", nullable = false)
    private Boolean unavailable;
}
