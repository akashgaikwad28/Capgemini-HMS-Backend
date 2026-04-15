package com.capgemini.hms.room.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "block")
/**
 * JPA entity representing a hospital block (floor + code). Uses an embedded
 * composite key (`BlockId`) and serves as a structural container for `Room`
 * entities. This entity contains only identity data and mapping annotations;
 * lifecycle operations should be handled by service components.
 */
public class Block {

    @EmbeddedId
    private BlockId id;

    public Block() {
    }

    public Block(BlockId id) {
        this.id = id;
    }

    public BlockId getId() {
        return id;
    }

    public void setId(BlockId id) {
        this.id = id;
    }
}
