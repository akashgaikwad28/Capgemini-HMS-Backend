package com.capgemini.hms.room.repository;

import com.capgemini.hms.room.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query("SELECT r FROM Room r WHERE r.isDeleted = false")
    Page<Room> findAllActive(Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.isDeleted = false AND r.roomType = :roomType")
    List<Room> findByRoomTypeActive(@Param("roomType") String roomType);

    @Query("SELECT r FROM Room r WHERE r.isDeleted = false AND r.unavailable = :unavailable")
    List<Room> findByUnavailableActive(@Param("unavailable") Boolean unavailable);

    @Query("SELECT r FROM Room r WHERE r.isDeleted = false AND r.unavailable = false AND r.roomType = :roomType")
    List<Room> findAvailableByRoomTypeActive(@Param("roomType") String roomType);

    @Query("SELECT r FROM Room r WHERE r.isDeleted = false AND r.unavailable = true AND r.roomType = :roomType")
    List<Room> findUnavailableByRoomTypeActive(@Param("roomType") String roomType);

    @Query("SELECT r FROM Room r WHERE r.isDeleted = false AND r.unavailable = false")
    List<Room> findAvailableRooms();

    long countByUnavailableTrue();

    @Query("SELECT COUNT(r) FROM Room r WHERE r.isDeleted = false")
    long countActive();
}
