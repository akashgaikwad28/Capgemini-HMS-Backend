package com.capgemini.hms.procedure;

import com.capgemini.hms.procedure.entity.Procedure;
import com.capgemini.hms.procedure.repository.ProcedureRepository;
import com.capgemini.hms.procedure.service.ProcedureService;
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
class ProcedureServiceTest {

    @Mock
    private ProcedureRepository procedureRepository;

    @InjectMocks
    private ProcedureService procedureService;

    private Procedure procedure;

    @BeforeEach
    void setUp() {
        procedure = new Procedure(1, "Reverse Rhinopodoplasty", 1500.0);
        procedure.setIsDeleted(false);
    }

    @Test
    void saveProcedure_shouldMarkActive() {
        when(procedureRepository.save(any(Procedure.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Procedure saved = procedureService.saveProcedure(procedure);

        assertFalse(saved.getIsDeleted());
    }

    @Test
    void getAllProcedures_shouldReturnActivePage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(procedureRepository.findAllActive(pageable)).thenReturn(new PageImpl<>(List.of(procedure)));

        Page<Procedure> result = procedureService.getAllProcedures(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateProcedure_shouldUpdateFields() {
        Procedure existing = new Procedure(1, "Old", 100.0);
        existing.setIsDeleted(false);
        Procedure updated = new Procedure(1, "New", 200.0);

        when(procedureRepository.findById(1)).thenReturn(Optional.of(existing));
        when(procedureRepository.save(any(Procedure.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Procedure result = procedureService.updateProcedure(updated);

        assertEquals("New", result.getName());
        assertEquals(200.0, result.getCost());
    }

    @Test
    void searchProcedures_shouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        when(procedureRepository.searchActive("reverse", pageable)).thenReturn(new PageImpl<>(List.of(procedure)));

        Page<Procedure> result = procedureService.searchProcedures("reverse", pageable);

        assertEquals(1, result.getTotalElements());
        verify(procedureRepository).searchActive("reverse", pageable);
    }

    @Test
    void deleteProcedure_shouldSoftDelete() {
        when(procedureRepository.findById(1)).thenReturn(Optional.of(procedure));
        when(procedureRepository.save(any(Procedure.class))).thenAnswer(invocation -> invocation.getArgument(0));

        procedureService.deleteProcedure(1);

        ArgumentCaptor<Procedure> captor = ArgumentCaptor.forClass(Procedure.class);
        verify(procedureRepository).save(captor.capture());
        assertTrue(captor.getValue().getIsDeleted());
    }
}
