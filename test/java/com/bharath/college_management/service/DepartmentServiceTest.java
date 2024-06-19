package com.bharath.college_management.service;

import com.bharath.college_management.Exceptions.DepartmentNotFoundException;
import com.bharath.college_management.config.jwt.JwtUtils;
import com.bharath.college_management.entity.Department;
import com.bharath.college_management.entity.Student;
import com.bharath.college_management.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @MockBean
    private JwtUtils jwtUtils;

    private Department department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        department = new Department("1", "CSE", "cse", true, null, null, null, null);
    }

    @Test
    void testGetAllDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department("1", "CSE", "cse", true, null, null, null, null));
        departments.add(new Department("2", "ECE", "ece", true, null, null, null, null));

        when(departmentRepository.findAll()).thenReturn(departments);

        List<Department> result = departmentService.getAllDepartments();

        assertEquals(2, result.size());
    }

    @Test
    void testSaveDepartment() {
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        Department savedDepartment = departmentService.saveDepartment(department);

        assertNotNull(savedDepartment);
        assertEquals("CSE", savedDepartment.getDepartmentName());
    }

    @Test
    void testSortBasedOnDepartmentName() {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department("2", "ECE", "ece", true, null, null, null, null));
        departments.add(new Department("1", "CSE", "cse", true, null, null, null, null));

        when(departmentRepository.findAll(Sort.by(Sort.Direction.ASC, "departmentName"))).thenReturn(departments);

        List<Department> sortedDepartments = departmentService.sortBasedOnDepartmentName("departmentName");

        assertEquals("CSE", sortedDepartments.get(0).getDepartmentName());
        assertEquals("ECE", sortedDepartments.get(1).getDepartmentName());
    }


    @Test
    void testGetAllStudentsUnderDepartmentNotFound() {
        when(departmentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.getAllStudentsUnderDepartment("1");
        });
    }

    @Test
    void testGetDepartmentById() {
        when(departmentRepository.findByDepartmentId("1")).thenReturn(Optional.of(department));

        Department foundDepartment = departmentService.getDepartmentById("1");

        assertEquals("CSE", foundDepartment.getDepartmentName());
    }

    @Test
    void testGetDepartmentByIdNotFound() {
        when(departmentRepository.findByDepartmentId("1")).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.getDepartmentById("1");
        });
    }

    @Test
    void testUpdateDepartment() {
        Department updatedDepartment = new Department("1", "IT", "it", false, null, null, null, null);

        when(departmentRepository.findById("1")).thenReturn(Optional.of(department));
        when(departmentRepository.save(updatedDepartment)).thenReturn(updatedDepartment);

        Department result = departmentService.updateDepartment("1", updatedDepartment);

        assertEquals("IT", result.getDepartmentName());
    }

    @Test
    void testUpdateDepartmentNotFound() {
        Department updatedDepartment = new Department("1", "IT", "it", false, null, null, null, null);

        when(departmentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.updateDepartment("1", updatedDepartment);
        });
    }

    @Test
    void testDeleteDepartment() {
        when(departmentRepository.findById("1")).thenReturn(Optional.of(department));

        departmentService.deleteDepartment("1");

        verify(departmentRepository, times(1)).deleteById("1");
    }

    @Test
    void testDeleteDepartmentNotFound() {
        when(departmentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.deleteDepartment("1");
        });

        verify(departmentRepository, never()).deleteById("1");
    }

    @Test
    void testGetDepartmentsInPages() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Department> departmentPage = new PageImpl<>(Collections.singletonList(department));

        when(departmentRepository.findAll(pageable)).thenReturn(departmentPage);

        List<Department> departments = departmentService.getDepartmentsInPages(0, 2);

        assertEquals(1, departments.size());
        assertEquals("CSE", departments.get(0).getDepartmentName());
    }

}
