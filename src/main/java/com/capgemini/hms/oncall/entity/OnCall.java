package com.capgemini.hms.oncall.entity;

import com.capgemini.hms.nurse.entity.Nurse;
import com.capgemini.hms.room.entity.Block;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "on_call")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnCall {

    @EmbeddedId
    private OnCallId id;

    @ManyToOne
    @MapsId("nurse")
    @JoinColumn(name = "nurse", referencedColumnName = "employeeid")
    private Nurse nurse;

    @ManyToOne
    @MapsId("block")
    @JoinColumns({
        @JoinColumn(name = "blockfloor", referencedColumnName = "blockfloor"),
        @JoinColumn(name = "blockcode", referencedColumnName = "blockcode")
    })
    private Block block;
}
