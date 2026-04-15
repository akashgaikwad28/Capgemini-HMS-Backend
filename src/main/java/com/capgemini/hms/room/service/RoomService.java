package com.capgemini.hms.room.service;

import com.capgemini.hms.room.entity.Room;
import com.capgemini.hms.room.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
/**
 * Service layer for `Room` entity operations. Encapsulates transactional
 * boundaries for create, update, soft-delete and provides query methods for
 * availability and filtering. Persistence is delegated to `RoomRepository`.
 */
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional
    public Room saveRoom(Room room) {
        room.setIsDeleted(false);
        return roomRepository.save(room);
    }

    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAllActive(pageable);
    }

    public Optional<Room> getRoomByNumber(Integer roomNumber) {
        return roomRepository.findById(roomNumber)
                .filter(r -> !r.getIsDeleted());
    }

    @Transactional
    public Room updateRoom(Room room) {
        Room existing = roomRepository.findById(room.getRoomNumber())
                .filter(r -> !r.getIsDeleted())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        
        existing.setRoomType(room.getRoomType());
        existing.setBlock(room.getBlock());
        existing.setUnavailable(room.getUnavailable());
        
        return roomRepository.save(existing);
    }

    @Transactional
    public void deleteRoom(Integer roomNumber) {
        Room room = roomRepository.findById(roomNumber)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        room.setIsDeleted(true);
        roomRepository.save(room);
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    public List<Room> getUnavailableRooms() {
        return roomRepository.findByUnavailableActive(true);
    }

    public List<Room> getRoomsByType(String roomType) {
        return roomRepository.findByRoomTypeActive(roomType);
    }

    public List<Room> getAvailableRoomsByType(String roomType) {
        return roomRepository.findAvailableByRoomTypeActive(roomType);
    }

    public List<Room> getUnavailableRoomsByType(String roomType) {
        return roomRepository.findUnavailableByRoomTypeActive(roomType);
    }
}
