package com.bharath.college_management.service;

import com.bharath.college_management.Exceptions.TeacherNotFoundException;
import com.bharath.college_management.entity.Department;
import com.bharath.college_management.entity.Teacher;
import com.bharath.college_management.repository.DepartmentRepository;
import com.bharath.college_management.repository.TeacherRepository;
import com.bharath.college_management.service.impl.TeacherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    private Teacher teacher;
    private Department department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        teacher = new Teacher("1", "T1", "Jane Smith", 30, null, null, null, "CSE");
        department = new Department("1", "CSE", "CS", true, null, null, null, null);
        department.setTeachers(new ArrayList<>());
    }

    @Test
    void testSaveTeacher() {
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(departmentRepository.findByDepartmentId(teacher.getTeacherDepartmentId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        Teacher savedTeacher = teacherService.saveTeacher(teacher);

        assertNotNull(savedTeacher);
        assertEquals("Jane Smith", savedTeacher.getTeacherName());
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void testGetAllTeachers() {
        when(teacherRepository.findAll()).thenReturn(Collections.singletonList(teacher));

        assertEquals(1, teacherService.getAllTeachers().size());
    }

    @Test
    void testGetTeacherById() {
        when(teacherRepository.findByTeacherId("1")).thenReturn(Optional.of(teacher));

        assertEquals("John Doe", teacherService.getTeacherById("1").getTeacherName());
    }

    @Test
    void testGetTeacherByAge() {
        when(teacherRepository.findTeacherByTeacherAge(30)).thenReturn(Collections.singletonList(teacher));

        assertEquals(1, teacherService.getTeacherByAge(30).size());
    }

    @Test
    void testUpdateTeacher() {
        Teacher existingTeacher = new Teacher("1", "T1", "Alice Smith", 34, null, null, null, "CSE");
        Teacher updatedTeacher = new Teacher("1", "T1", "Bob Johnson", 32, null, null, null, "MME");

        when(teacherRepository.findById("1")).thenReturn(Optional.of(existingTeacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);

        Teacher result = teacherService.updateTeacher("1", updatedTeacher);

        assertNotNull(result);
        assertEquals("Bob Johnson", result.getTeacherName());
        assertEquals("MME", result.getTeacherDepartmentId());
        verify(teacherRepository, times(1)).save(existingTeacher);
    }

    @Test
    void testDeleteTeacher() {
        when(teacherRepository.findByTeacherId("1")).thenReturn(Optional.of(teacher));
        when(departmentRepository.findByDepartmentId("CSE")).thenReturn(Optional.of(department));

        teacherService.deleteTeacher("1");

        verify(departmentRepository, times(1)).save(department);
        verify(teacherRepository, times(1)).delete(teacher);
        assertFalse(department.getTeachers().contains(teacher));
    }

    @Test
    void testGetTeachersInPages() {
        Teacher teacher1 = new Teacher("1", "T1", "Bob Johnson", 32, null, null, null, "MME");
        Pageable pageable = PageRequest.of(0, 2);
        List<Teacher> teachers = Arrays.asList(teacher, teacher1);
        Page<Teacher> teacherPage = new PageImpl<>(teachers);

        when(teacherRepository.findAll(pageable)).thenReturn(teacherPage);

        List<Teacher> result = teacherService.getTeachersInPages(0, 2);

        assertEquals(2, result.size());
        assertEquals("Jane Smith", result.get(0).getTeacherName());
        assertEquals("Bob Johnson", result.get(1).getTeacherName());
    }
}




