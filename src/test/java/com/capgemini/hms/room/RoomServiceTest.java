package com.capgemini.hms.room;

import com.capgemini.hms.room.entity.Block;
import com.capgemini.hms.room.entity.BlockId;
import com.capgemini.hms.room.entity.Room;
import com.capgemini.hms.room.repository.RoomRepository;
import com.capgemini.hms.room.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private Block block;

    @BeforeEach
    void setUp() {
        block = new Block(new BlockId(1, 1));
        room = new Room(101, "Single", block, false);
        room.setIsDeleted(false);
    }

    @Test
    void saveRoom_shouldMarkActive() {
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Room saved = roomService.saveRoom(room);

        assertFalse(saved.getIsDeleted());
    }

    @Test
    void getAllRooms_shouldReturnActivePage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(roomRepository.findAllActive(pageable)).thenReturn(new PageImpl<>(List.of(room)));

        Page<Room> result = roomService.getAllRooms(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAvailableRooms_shouldReturnAvailableRooms() {
        when(roomRepository.findAvailableRooms()).thenReturn(List.of(room));

        List<Room> result = roomService.getAvailableRooms();

        assertEquals(1, result.size());
        verify(roomRepository).findAvailableRooms();
    }

    @Test
    void updateRoom_shouldUpdateFields() {
        Room existing = new Room(101, "Old", block, true);
        existing.setIsDeleted(false);
        Room updated = new Room(101, "Deluxe", block, false);

        when(roomRepository.findById(101)).thenReturn(Optional.of(existing));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Room result = roomService.updateRoom(updated);

        assertEquals("Deluxe", result.getRoomType());
        assertFalse(result.getUnavailable());
    }

    @Test
    void deleteRoom_shouldSoftDelete() {
        when(roomRepository.findById(101)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        roomService.deleteRoom(101);

        ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsDeleted());
    }
}
