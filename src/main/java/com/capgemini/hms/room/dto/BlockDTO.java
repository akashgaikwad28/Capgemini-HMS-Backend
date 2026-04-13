package com.capgemini.hms.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class BlockDTO {

    @NotNull
    @Schema(example = "1", description = "The floor number where the block is located")
    private Integer blockFloor;

    @NotNull
    @Schema(example = "1", description = "The distinct building code or section number")
    private Integer blockCode;

    public BlockDTO() {}

    public BlockDTO(Integer blockFloor, Integer blockCode) {
        this.blockFloor = blockFloor;
        this.blockCode = blockCode;
    }

    public Integer getBlockFloor() { return blockFloor; }
    public void setBlockFloor(Integer blockFloor) { this.blockFloor = blockFloor; }

    public Integer getBlockCode() { return blockCode; }
    public void setBlockCode(Integer blockCode) { this.blockCode = blockCode; }
}
