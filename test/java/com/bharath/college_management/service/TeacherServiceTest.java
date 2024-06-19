package com.bharath.college_management.service;

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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
class TeacherServiceTest {

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
        teacher = new Teacher("1", "T1", "John Doe", 30, null, null, null, "Computer Science");
        department = new Department("1", "Computer Science", "CS", true, null, null, null, null);
    }

    @Test
    void testSaveTeacher() {
        when(departmentRepository.findByDepartmentId("Computer Science")).thenReturn(Optional.of(department));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        Teacher savedTeacher = teacherService.saveTeacher(teacher);

        assertNotNull(savedTeacher);
        assertEquals("John Doe", savedTeacher.getTeacherName());
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
        Teacher updatedTeacher = new Teacher("1", "T1", "Jane Doe", 31, null, null, null, "Computer Science");

        when(teacherRepository.findById("1")).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(updatedTeacher)).thenReturn(updatedTeacher);

        assertEquals("Jane Doe", teacherService.updateTeacher("1", updatedTeacher).getTeacherName());
    }

    @Test
    void testDeleteTeacher() {
        when(teacherRepository.findByTeacherId("1")).thenReturn(Optional.of(teacher));

        teacherService.deleteTeacher("1");

        verify(departmentRepository, times(1)).save(department);
        verify(teacherRepository, times(1)).delete(teacher);
    }

    @Test
    void testGetTeachersInPages() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Teacher> teacherPage = new PageImpl<>(Collections.singletonList(teacher));

        when(teacherRepository.findAll(pageable)).thenReturn(teacherPage);

        List<Teacher> teachers = teacherService.getTeachersInPages(0, 2);

        assertEquals(1, teachers.size());
        assertEquals("CSE", teachers.get(0).getTeacherDepartmentId());
    }

}


