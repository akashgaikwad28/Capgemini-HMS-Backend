package com.capgemini.hms.room.entity;

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
    private Integer blockFloor;
    private Integer blockCode;
}
