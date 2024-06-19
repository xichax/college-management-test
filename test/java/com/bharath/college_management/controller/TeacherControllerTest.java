package com.bharath.college_management.controller;

import com.bharath.college_management.config.jwt.JwtUtils;
import com.bharath.college_management.entity.Teacher;
import com.bharath.college_management.service.TeacherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenListOfTeachers_whenGetAllTeachers_thenReturnTeachersList() throws Exception {
        // given
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher("1", "T1", "John Doe", 30, null, null, null, "Computer Science"));
        teachers.add(new Teacher("2", "T2", "Jane Smith", 35, null, null, null, "Maths"));
        given(teacherService.getAllTeachers()).willReturn(teachers);

        // when
        ResultActions response = mockMvc.perform(get("/cm/get/t/get-all"));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(teachers.size())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenTeacherObject_whenCreateTeacher_thenReturnSavedTeacher() throws Exception {
        // given
        Teacher teacher = new Teacher("1", "T1", "John Doe", 30, null, null, null, "Python");
        given(teacherService.saveTeacher(any(Teacher.class))).willReturn(teacher);

        // when
        ResultActions response = mockMvc.perform(post("/cm/t/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)));

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(teacher.getId())))
                .andExpect(jsonPath("$.teacherName", is(teacher.getTeacherName())))
                .andExpect(jsonPath("$.teacherAge", is(teacher.getTeacherAge())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenAge_whenGetTeachersByAge_thenReturnTeachersList() throws Exception {
        // given
        int age = 30;
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher("1", "T1", "John Doe", age, null, null, null, "robotics"));
        given(teacherService.getTeacherByAge(age)).willReturn(teachers);

        // when
        ResultActions response = mockMvc.perform(get("/cm/get/age")
                .param("age", String.valueOf(age)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(teachers.size())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenTeacherIdAndUpdatedTeacher_whenUpdateTeacher_thenReturnUpdatedTeacherObject() throws Exception {
        // given
        String teacherId = "1";
        Teacher updatedTeacher = new Teacher("1", "T1", "John Doe", 31, null, null, null, "Machines");
        given(teacherService.updateTeacher(teacherId, updatedTeacher)).willReturn(updatedTeacher);

        // when
        ResultActions response = mockMvc.perform(put("/cm/t/update/{teacherId}", teacherId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTeacher)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedTeacher.getId())))
                .andExpect(jsonPath("$.teacherName", is(updatedTeacher.getTeacherName())))
                .andExpect(jsonPath("$.teacherAge", is(updatedTeacher.getTeacherAge())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenTeacherId_whenDeleteTeacher_thenNoContent() throws Exception {
        // given
        String teacherId = "1";

        // when
        ResultActions response = mockMvc.perform(delete("/cm/t/delete/{teacherId}", teacherId));

        // then
        response.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenPageAndSize_whenGetTeachersInPages_thenReturnTeachersPage() throws Exception {
        // given
        int page = 0;
        int size = 2;
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(new Teacher("1", "T1", "John Doe", 30, null, null, null, "Computer Science"));
        teachers.add(new Teacher("2", "T2", "Jane Smith", 35, null, null, null, "maths"));
        given(teacherService.getTeachersInPages(page, size)).willReturn(teachers);

        // when
        ResultActions response = mockMvc.perform(get("/cm/get/t/page")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(teachers.size())));
    }
}
