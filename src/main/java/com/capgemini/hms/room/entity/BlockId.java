package com.capgemini.hms.room.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockId implements Serializable {
    @Column(name = "blockfloor")
    private Integer blockFloor;

    @Column(name = "blockcode")
    private Integer blockCode;
}
