package com.bharath.college_management.controller;

import com.bharath.college_management.config.jwt.JwtUtils;
import com.bharath.college_management.entity.Student;
import com.bharath.college_management.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void givenListOfStudents_whenGetAllStudents_thenReturnStudentsList() throws Exception {
        // given
        List<Student> students = new ArrayList<>();
        students.add(new Student("1", "S1", "John Doe", 20, null, null, null, "Computer Science"));
        students.add(new Student("2", "S2", "Jane Smith", 21, null, null, null, "Mathematics"));
        given(studentService.getAllStudents()).willReturn(students);

        // when
        ResultActions response = mockMvc.perform(get("/cm/get/s/get-all"));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(students.size())));
    }

    @Test
    public void givenStudentObject_whenCreateStudent_thenReturnSavedStudent() throws Exception {
        // given
        Student student = new Student("1", "S1", "John Doe", 20, null, null, null, "Computer Science");
        given(studentService.saveStudent(any(Student.class))).willReturn(student);

        // when
        ResultActions response = mockMvc.perform(post("/cm/s/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)));

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(student.getId())))
                .andExpect(jsonPath("$.name", is(student.getStudentName())))
                .andExpect(jsonPath("$.age", is(student.getStudentAge())))
                .andExpect(jsonPath("$.department", is(student.getStudentDepartmentId())));
    }

    @Test
    public void givenStudentId_whenGetStudentById_thenReturnStudentObject() throws Exception {
        // given
        String studentId = "1";
        Student student = new Student(studentId, "S1", "John Doe", 20, null, null, null, "Computer Science");
        given(studentService.getStudentById(studentId)).willReturn(student);

        // when
        ResultActions response = mockMvc.perform(get("/cm/get/s/{studentId}", studentId));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(student.getId())))
                .andExpect(jsonPath("$.name", is(student.getStudentName())))
                .andExpect(jsonPath("$.age", is(student.getStudentAge())))
                .andExpect(jsonPath("$.department", is(student.getStudentDepartmentId())));
    }

    @Test
    public void givenAge_whenGetStudentsByAge_thenReturnStudentsList() throws Exception {
        // given
        int age = 20;
        List<Student> students = new ArrayList<>();
        students.add(new Student("1", "S1", "John Doe", age, null, null, null, "Computer Science"));
        given(studentService.getStudentByAge(age)).willReturn(students);

        // when
        ResultActions response = mockMvc.perform(get("/cm/get/s/age/{age}", age));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(students.size())));
    }

    @Test
    public void givenStudentIdAndUpdatedStudent_whenUpdateStudent_thenReturnUpdatedStudentObject() throws Exception {
        // given
        String studentId = "1";
        Student updatedStudent = new Student("1", "S1", "John Doe", 21, null, null, null, "Computer Science");
        given(studentService.updateStudent(studentId, updatedStudent)).willReturn(updatedStudent);

        // when
        ResultActions response = mockMvc.perform(put("/cm/s/update/{studentId}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudent)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedStudent.getId())))
                .andExpect(jsonPath("$.name", is(updatedStudent.getStudentName())))
                .andExpect(jsonPath("$.age", is(updatedStudent.getStudentAge())))
                .andExpect(jsonPath("$.department", is(updatedStudent.getStudentDepartmentId())));
    }

    @Test
    public void givenStudentId_whenDeleteStudent_thenNoContent() throws Exception {
        // given
        String studentId = "1";
        // Mock the service to do nothing (or throw an exception if needed)
        willDoNothing().given(studentService).deleteStudent(studentId);

        // when
        ResultActions response = mockMvc.perform(delete("/cm/s/delete/{studentId}", studentId));

        // then
        response.andExpect(status().isNoContent());
    }

    @Test
    public void givenPageAndSize_whenGetAllStudentsInPages_thenReturnStudentsPage() throws Exception {
        // given
        int page = 0;
        int size = 2;
        List<Student> students = new ArrayList<>();
        students.add(new Student("1", "S1", "John Doe", 20, null, null, null, "Computer Science"));
        students.add(new Student("2", "S2", "Jane Smith", 21, null, null, null, "Mathematics"));
        given(studentService.getStudentsInPages(page, size)).willReturn(students);

        // when
        ResultActions response = mockMvc.perform(get("/cm/get/s/page")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(students.size())));
    }


}

