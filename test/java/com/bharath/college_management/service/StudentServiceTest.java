package com.bharath.college_management.service;

import com.bharath.college_management.Exceptions.BranchInvalidException;
import com.bharath.college_management.Exceptions.DepartmentNotFoundException;
import com.bharath.college_management.Exceptions.StudentNotFoundException;
import com.bharath.college_management.entity.Department;
import com.bharath.college_management.entity.Student;
import com.bharath.college_management.enums.Branch;
import com.bharath.college_management.repository.DepartmentRepository;
import com.bharath.college_management.repository.StudentRepository;
import com.bharath.college_management.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private Department department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        student = new Student("1", "S1", "John Doe", 20, null, null, null, "Computer Science");
        department = new Department("1", "Computer Science", "CS", true, null, null, null, null);
    }

    @Test
    void testSaveStudent() {
        when(departmentRepository.findByDepartmentId("Computer Science")).thenReturn(Optional.of(department));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student savedStudent = studentService.saveStudent(student);

        assertNotNull(savedStudent);
        assertEquals("John Doe", savedStudent.getStudentName());
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void testGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(Collections.singletonList(student));

        assertEquals(1, studentService.getAllStudents().size());
    }

    @Test
    void testGetStudentById() {
        when(studentRepository.findByStudentId("1")).thenReturn(Optional.of(student));

        assertEquals("John Doe", studentService.getStudentById("1").getStudentName());
    }

    @Test
    void testGetStudentByAge() {
        when(studentRepository.findStudentByStudentAge(20)).thenReturn(Collections.singletonList(student));

        assertEquals(1, studentService.getStudentByAge(20).size());
    }

    @Test
    void testUpdateStudent() {
        Student updatedStudent = new Student("1", "S1", "Jane Doe", 21, null, null, null, "Computer Science");

        when(studentRepository.findById("1")).thenReturn(Optional.of(student));
        when(studentRepository.save(updatedStudent)).thenReturn(updatedStudent);

        assertEquals("Jane Doe", studentService.updateStudent("1", updatedStudent).getStudentName());
    }

    @Test
    void testDeleteStudent() {
        when(studentRepository.findByStudentId("1")).thenReturn(Optional.of(student));

        studentService.deleteStudent("1");

        verify(departmentRepository, times(1)).save(department);
        verify(studentRepository, times(1)).delete(student);
    }

    @Test
    void testGetStudentsInPages() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Student> studentPage = new PageImpl<>(Collections.singletonList(student));

        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        List<Student> departments = studentService.getStudentsInPages(0, 2);

        assertEquals(1, departments.size());
        assertEquals("CSE", departments.get(0).getStudentDepartmentId());
    }

}

