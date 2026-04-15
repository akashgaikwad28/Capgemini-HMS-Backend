package com.capgemini.hms.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for `Room` payloads used by REST endpoints and views.
 * Carries minimal identity and state fields required for client interactions
 * and input validation. This DTO intentionally avoids exposing entity
 * relationships or persistence identifiers beyond `roomNumber`.
 */
public class RoomDTO {
    @NotNull
    @Schema(example = "101", description = "Unique room number")
    private Integer roomNumber;

    @NotBlank
    @Schema(example = "General", description = "Type of room (Single, Double, Suite, ICU)")
    private String roomType;

    @NotNull
    @Schema(example = "1", description = "Block floor where the room is located")
    private Integer blockFloor;

    @NotNull
    @Schema(example = "1", description = "Block code where the room is located")
    private Integer blockCode;

    @NotNull
    @Schema(example = "false", description = "Availability status of the room")
    private Boolean unavailable;

    public RoomDTO() {}

    public RoomDTO(Integer roomNumber, String roomType, Integer blockFloor, Integer blockCode, Boolean unavailable) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.blockFloor = blockFloor;
        this.blockCode = blockCode;
        this.unavailable = unavailable;
    }

    public Integer getRoomNumber() { return roomNumber; }
    public void setRoomNumber(Integer roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Integer getBlockFloor() { return blockFloor; }
    public void setBlockFloor(Integer blockFloor) { this.blockFloor = blockFloor; }

    public Integer getBlockCode() { return blockCode; }
    public void setBlockCode(Integer blockCode) { this.blockCode = blockCode; }

    public Boolean getUnavailable() { return unavailable; }
    public void setUnavailable(Boolean unavailable) { this.unavailable = unavailable; }
}
