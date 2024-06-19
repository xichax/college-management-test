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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
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
        student = new Student("1", "S1", "John Doe", 20, null, null, null, "CSE");
        department = new Department("1", "CSE", "CS", true, null, null, null, null);
        department.setStudents(new ArrayList<>());
    }

    @Test
    void testSaveStudent() {
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(departmentRepository.findByDepartmentId(student.getStudentDepartmentId())).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

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
        Student existingStudent = new Student("1", "S1", "John Doe", 20, null, null, null, "CSE");
        Student updatedStudent = new Student("1", "S1", "Jane Doe", 21, null, null, null, "CSE");

        when(studentRepository.findById("1")).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        Student result = studentService.updateStudent("1", updatedStudent);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getStudentName());
        assertEquals(21, result.getStudentAge());
        assertEquals("CSE", result.getStudentDepartmentId());
        verify(studentRepository, times(1)).save(existingStudent);
    }


    @Test
    void testUpdateStudentNotFound() {
        Student updatedStudent = new Student("1", "S1", "Jane Doe", 21, null, null, null, "CSE");

        when(studentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> {
            studentService.updateStudent("1", updatedStudent);
        });

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testDeleteStudent() {
        when(studentRepository.findByStudentId("1")).thenReturn(Optional.of(student));
        when(departmentRepository.findByDepartmentId("CSE")).thenReturn(Optional.of(department));

        studentService.deleteStudent("1");

        verify(departmentRepository, times(1)).save(department);
        verify(studentRepository, times(1)).delete(student);
        assertFalse(department.getStudents().contains(student));
    }

    @Test
    void testDeleteStudentDepartmentNotFound() {
        when(studentRepository.findByStudentId("1")).thenReturn(Optional.of(student));
        when(departmentRepository.findByDepartmentId("CSE")).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> studentService.deleteStudent("1"));

        verify(departmentRepository, never()).save(any(Department.class));
        verify(studentRepository, never()).delete(any(Student.class));
    }

    @Test
    void testGetStudentsInPages() {
        Student student1 = new Student("2", "S2", "Bharath", 22, null, null, null, "ECE");
        Pageable pageable = PageRequest.of(0, 2);
        List<Student> students = Arrays.asList(student, student1);
        Page<Student> studentPage = new PageImpl<>(students);

        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        List<Student> result = studentService.getStudentsInPages(0, 2);

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getStudentName());
        assertEquals("Bharath", result.get(1).getStudentName());
    }

}

