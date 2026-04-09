package com.capgemini.hms.oncall.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnCallId implements Serializable {
    private Integer nurse;
    private Integer blockFloor;
    private Integer blockCode;
    private LocalDateTime onCallStart;
    private LocalDateTime onCallEnd;
}
