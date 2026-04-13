package com.capgemini.hms.room.controller.api;

import com.capgemini.hms.common.dto.ApiResponse;
import com.capgemini.hms.common.dto.PagedResponse;
import com.capgemini.hms.room.dto.RoomDTO;
import com.capgemini.hms.room.entity.Block;
import com.capgemini.hms.room.entity.BlockId;
import com.capgemini.hms.room.entity.Room;
import com.capgemini.hms.room.repository.BlockRepository;
import com.capgemini.hms.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Room Management", description = "Endpoints for managing hospital rooms and blocks")
public class RoomController {

    private final RoomService roomService;
    private final BlockRepository blockRepository;

    public RoomController(RoomService roomService, BlockRepository blockRepository) {
        this.roomService = roomService;
        this.blockRepository = blockRepository;
    }

    @GetMapping
    @Operation(summary = "Get all rooms", description = "Returns a paginated list of all active rooms")
    public ResponseEntity<ApiResponse<PagedResponse<RoomDTO>>> getAllRooms(Pageable pageable) {
        Page<Room> page = roomService.getAllRooms(pageable);
        List<RoomDTO> content = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        PagedResponse<RoomDTO> pagedResponse = new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available rooms", description = "Returns a list of all rooms that are currently unoccupied and active")
    public ResponseEntity<ApiResponse<List<RoomDTO>>> getAvailableRooms() {
        List<Room> rooms = roomService.getAvailableRooms();
        List<RoomDTO> content = rooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/unavailable")
    @Operation(summary = "Get unavailable rooms", description = "Returns a list of all rooms that are currently occupied or under maintenance")
    public ResponseEntity<ApiResponse<List<RoomDTO>>> getUnavailableRooms() {
        List<Room> rooms = roomService.getUnavailableRooms();
        List<RoomDTO> content = rooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/type/{roomType}")
    @Operation(summary = "Get rooms by type", description = "Returns a list of rooms filtered by type (e.g., 'Single', 'Double', 'ICU')")
    public ResponseEntity<ApiResponse<List<RoomDTO>>> getRoomsByType(@PathVariable String roomType) {
        List<Room> rooms = roomService.getRoomsByType(roomType);
        List<RoomDTO> content = rooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/available/type/{roomType}")
    @Operation(summary = "Get available rooms by type", description = "Returns unoccupied rooms of a specific type")
    public ResponseEntity<ApiResponse<List<RoomDTO>>> getAvailableRoomsByType(@PathVariable String roomType) {
        List<Room> rooms = roomService.getAvailableRoomsByType(roomType);
        List<RoomDTO> content = rooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/unavailable/type/{roomType}")
    @Operation(summary = "Get unavailable rooms by type", description = "Returns occupied rooms of a specific type")
    public ResponseEntity<ApiResponse<List<RoomDTO>>> getUnavailableRoomsByType(@PathVariable String roomType) {
        List<Room> rooms = roomService.getUnavailableRoomsByType(roomType);
        List<RoomDTO> content = rooms.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(content));
    }

    @GetMapping("/{roomNumber}")
    @Operation(summary = "Get room by number", description = "Returns details for a specific active room")
    public ResponseEntity<ApiResponse<RoomDTO>> getRoomByNumber(@PathVariable Integer roomNumber) {
        return roomService.getRoomByNumber(roomNumber)
                .map(r -> ResponseEntity.ok(ApiResponse.success(convertToDTO(r))))
                .orElseThrow(() -> new RuntimeException("Room not found with number: " + roomNumber));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new room", description = "Adds a new room to the hospital inventory")
    public ResponseEntity<ApiResponse<RoomDTO>> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        Room saved = roomService.saveRoom(room);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(saved), "Room created successfully"));
    }

    @PutMapping("/{roomNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update room details", description = "Updates an existing room record")
    public ResponseEntity<ApiResponse<RoomDTO>> updateRoom(@PathVariable Integer roomNumber, @Valid @RequestBody RoomDTO roomDTO) {
        roomDTO.setRoomNumber(roomNumber);
        Room room = convertToEntity(roomDTO);
        Room updated = roomService.updateRoom(room);
        return ResponseEntity.ok(ApiResponse.success(convertToDTO(updated), "Room details updated successfully"));
    }

    @DeleteMapping("/{roomNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove room record", description = "Performs a soft-delete on a room record")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Integer roomNumber) {
        roomService.deleteRoom(roomNumber);
        return ResponseEntity.ok(ApiResponse.success("Room record deleted successfully"));
    }

    private RoomDTO convertToDTO(Room r) {
        return new RoomDTO(
                r.getRoomNumber(),
                r.getRoomType(),
                r.getBlock().getId().getBlockFloor(),
                r.getBlock().getId().getBlockCode(),
                r.getUnavailable()
        );
    }


    private Room convertToEntity(RoomDTO dto) {
        BlockId blockId = new BlockId(dto.getBlockFloor(), dto.getBlockCode());
        Block block = blockRepository.findById(blockId)
                .orElseThrow(() -> new RuntimeException("Block not found: " + dto.getBlockFloor() + "-" + dto.getBlockCode()));
        
        return new Room(
                dto.getRoomNumber(),
                dto.getRoomType(),
                block,
                dto.getUnavailable()
        );
    }
}
