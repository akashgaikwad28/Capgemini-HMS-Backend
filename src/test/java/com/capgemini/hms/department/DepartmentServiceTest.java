package com.capgemini.hms.department;

import com.capgemini.hms.department.entity.Department;
import com.capgemini.hms.department.repository.DepartmentRepository;
import com.capgemini.hms.department.service.DepartmentService;
import com.capgemini.hms.physician.entity.AffiliatedWith;
import com.capgemini.hms.physician.entity.Physician;
import com.capgemini.hms.physician.repository.AffiliatedWithRepository;
import com.capgemini.hms.physician.repository.PhysicianRepository;
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
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PhysicianRepository physicianRepository;

    @Mock
    private AffiliatedWithRepository affiliatedWithRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;
    private Physician physician;

    @BeforeEach
    void setUp() {
        physician = new Physician(4, "Dr. Cox", "Head Chief", 444444444);
        department = new Department(1, "General Medicine", physician);
        department.setIsDeleted(false);
    }

    @Test
    void saveDepartment_shouldPersistAsActive() {
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Department saved = departmentService.saveDepartment(department);

        assertFalse(saved.getIsDeleted());
        verify(departmentRepository).save(department);
    }

    @Test
    void getAllDepartments_shouldReturnActivePage() {
        Pageable pageable = PageRequest.of(0, 5);
        when(departmentRepository.findAllActive(pageable)).thenReturn(new PageImpl<>(List.of(department)));

        Page<Department> result = departmentService.getAllDepartments(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("General Medicine", result.getContent().get(0).getName());
    }

    @Test
    void getDepartmentById_shouldReturnActiveDepartment() {
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));

        Optional<Department> result = departmentService.getDepartmentById(1);

        assertTrue(result.isPresent());
        assertEquals("General Medicine", result.get().getName());
    }

    @Test
    void setDepartmentHead_shouldUpdateHead() {
        Department existing = new Department(1, "General Medicine", null);
        existing.setIsDeleted(false);

        when(departmentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(physicianRepository.findById(7)).thenReturn(Optional.of(new Physician(7, "Dr. Wen", "Surgeon", 777777777)));
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Department updated = departmentService.setDepartmentHead(1, 7);

        assertNotNull(updated.getHead());
        assertEquals(7, updated.getHead().getEmployeeId());
        verify(departmentRepository).save(existing);
    }

    @Test
    void affiliatePhysician_shouldCreateAndSaveAffiliation() {
        Physician p = new Physician(3, "Dr. Turk", "Surgeon", 333333333);
        Department d = new Department(2, "Surgery", physician);

        when(physicianRepository.findById(3)).thenReturn(Optional.of(p));
        when(departmentRepository.findById(2)).thenReturn(Optional.of(d));
        when(affiliatedWithRepository.save(any(AffiliatedWith.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AffiliatedWith result = departmentService.affiliatePhysician(3, 2, true);

        assertEquals(3, result.getId().getPhysician());
        assertEquals(2, result.getId().getDepartment());
        assertTrue(result.getPrimaryAffiliation());
        verify(affiliatedWithRepository).save(any(AffiliatedWith.class));
    }

    @Test
    void getDepartmentPhysicians_shouldReturnAffiliations() {
        when(affiliatedWithRepository.findByDepartment_DepartmentId(1))
                .thenReturn(List.of(new AffiliatedWith()));

        List<AffiliatedWith> result = departmentService.getDepartmentPhysicians(1);

        assertEquals(1, result.size());
        verify(affiliatedWithRepository).findByDepartment_DepartmentId(1);
    }
}
