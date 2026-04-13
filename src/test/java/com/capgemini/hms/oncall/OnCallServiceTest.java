package com.capgemini.hms.oncall;

import com.capgemini.hms.nurse.entity.Nurse;
import com.capgemini.hms.nurse.repository.NurseRepository;
import com.capgemini.hms.oncall.entity.OnCall;
import com.capgemini.hms.oncall.entity.OnCallId;
import com.capgemini.hms.oncall.repository.OnCallRepository;
import com.capgemini.hms.oncall.service.OnCallService;
import com.capgemini.hms.room.entity.Block;
import com.capgemini.hms.room.entity.BlockId;
import com.capgemini.hms.room.repository.BlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnCallServiceTest {

    @Mock
    private OnCallRepository onCallRepository;

    @Mock
    private NurseRepository nurseRepository;

    @Mock
    private BlockRepository blockRepository;

    @InjectMocks
    private OnCallService onCallService;

    private Nurse nurse;
    private Block block;
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        nurse = new Nurse(101, "Carla", "Head Nurse", true, 111111110);
        nurse.setIsDeleted(false);
        block = new Block(new BlockId(1, 1));
        start = LocalDateTime.of(2026, 4, 9, 8, 0);
        end = LocalDateTime.of(2026, 4, 9, 16, 0);
    }

    @Test
    void assignShift_shouldSaveValidShift() {
        when(nurseRepository.findById(101)).thenReturn(Optional.of(nurse));
        when(blockRepository.findById(new BlockId(1, 1))).thenReturn(Optional.of(block));
        when(onCallRepository.findOverlappingShifts(101, start, end)).thenReturn(List.of());
        when(onCallRepository.save(any(OnCall.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OnCall result = onCallService.assignShift(101, 1, 1, start, end);

        assertEquals(101, result.getId().getNurse());
        assertEquals(1, result.getId().getBlockFloor());
        assertEquals(1, result.getId().getBlockCode());
        verify(onCallRepository).save(any(OnCall.class));
    }

    @Test
    void assignShift_shouldRejectEndBeforeStart() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> onCallService.assignShift(101, 1, 1, end, start));
        assertTrue(ex.getMessage().contains("end time cannot be before start time"));
    }

    @Test
    void assignShift_shouldRejectOverlap() {
        when(nurseRepository.findById(101)).thenReturn(Optional.of(nurse));
        when(blockRepository.findById(new BlockId(1, 1))).thenReturn(Optional.of(block));
        when(onCallRepository.findOverlappingShifts(101, start, end))
                .thenReturn(List.of(new OnCall(new OnCallId(101, 1, 1, start, end), nurse, block)));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> onCallService.assignShift(101, 1, 1, start, end));
        assertTrue(ex.getMessage().contains("already assigned"));
    }

    @Test
    void getNurseShifts_shouldDelegateToRepository() {
        when(onCallRepository.findByNurse_EmployeeId(101))
                .thenReturn(List.of(new OnCall(new OnCallId(101, 1, 1, start, end), nurse, block)));

        List<OnCall> result = onCallService.getNurseShifts(101);

        assertEquals(1, result.size());
    }

    @Test
    void getBlockCoverage_shouldDelegateToRepository() {
        when(onCallRepository.findById_BlockFloorAndId_BlockCode(1, 1))
                .thenReturn(List.of(new OnCall(new OnCallId(101, 1, 1, start, end), nurse, block)));

        List<OnCall> result = onCallService.getBlockCoverage(1, 1);

        assertEquals(1, result.size());
    }

    @Test
    void deleteShift_shouldDeleteByCompositeId() {
        onCallService.deleteShift(101, 1, 1, start, end);

        ArgumentCaptor<OnCallId> captor = ArgumentCaptor.forClass(OnCallId.class);
        verify(onCallRepository).deleteById(captor.capture());
        assertEquals(101, captor.getValue().getNurse());
        assertEquals(1, captor.getValue().getBlockFloor());
        assertEquals(1, captor.getValue().getBlockCode());
    }
}

// Telemetry: Verify test execution coverage
