package com.bharath.college_management.controller;


import com.bharath.college_management.config.jwt.JwtUtils;
import com.bharath.college_management.entity.Department;
import com.bharath.college_management.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//        (webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
//@Import(TestConfig.class)
//@TestPropertySource(properties = {
//        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
//})
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenDepartmentObject_whenCreateDepartment_thenReturnSavedDepartment() throws Exception {
        // given - precondition or setup
        Department department = new Department("1", "CSE", "Computer Science", true, null, null, null, null);
        given(departmentService.saveDepartment(any(Department.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/cm/d/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(department)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.departmentId", is(department.getDepartmentId())))
                .andExpect(jsonPath("$.departmentName", is(department.getDepartmentName())))
                .andExpect(jsonPath("$.availableOnline", is(department.getAvailableOnline())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenListOfDepartments_whenGetAllDepartments_thenReturnDepartmentsList() throws Exception {
        // given - precondition or setup
        List<Department> listOfDepartments = Arrays.asList(
                new Department("1", "CSE", "Computer Science", true, null, null, null, null),
                new Department("2", "ECE", "Electronics", true, null, null, null, null)
        );
        given(departmentService.getAllDepartments()).willReturn(listOfDepartments);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/cm/get/d/get-all"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(listOfDepartments.size())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenDepartmentId_whenGetDepartmentById_thenReturnDepartmentObject() throws Exception {
        // given - precondition or setup
        String departmentId = "1";
        Department department = new Department(departmentId, "CSE", "Computer Science", true, null, null, null, null);
        given(departmentService.getDepartmentById(departmentId)).willReturn(department);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/cm/d/{departmentId}", departmentId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.departmentId", is(department.getDepartmentId())))
                .andExpect(jsonPath("$.departmentName", is(department.getDepartmentName())))
                .andExpect(jsonPath("$.availableOnline", is(department.getAvailableOnline())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenInvalidDepartmentId_whenGetDepartmentById_thenReturnNotFound() throws Exception {
        // given - precondition or setup
        String departmentId = "1";
        given(departmentService.getDepartmentById(departmentId)).willReturn(null);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/cm/d/{departmentId}", departmentId));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenUpdatedDepartment_whenUpdateDepartment_thenReturnUpdatedDepartmentObject() throws Exception {
        // given - precondition or setup
        String departmentId = "1";
        Department savedDepartment = new Department(departmentId, "CSE", "Computer Science", true, null, null, null, null);
        Department updatedDepartment = new Department(departmentId, "IT", "Information Technology", false, null, null, null, null);

        given(departmentService.getDepartmentById(departmentId)).willReturn(savedDepartment);
        given(departmentService.updateDepartment(anyString(), any(Department.class)))
                .willAnswer((invocation) -> invocation.getArgument(1));

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/cm/d/update/{departmentId}", departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDepartment)));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.departmentId", is(updatedDepartment.getDepartmentId())))
                .andExpect(jsonPath("$.departmentName", is(updatedDepartment.getDepartmentName())))
                .andExpect(jsonPath("$.availableOnline", is(updatedDepartment.getAvailableOnline())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenInvalidDepartmentId_whenUpdateDepartment_thenReturnNotFound() throws Exception {
        // given - precondition or setup
        String departmentId = "1";
        Department updatedDepartment = new Department(departmentId, "IT", "Information Technology", false, null, null, null, null);

        given(departmentService.getDepartmentById(departmentId)).willReturn(null);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(put("/cm/d/update/{departmentId}", departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDepartment)));

        // then - verify the output
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenDepartmentId_whenDeleteDepartment_thenReturnNoContent() throws Exception {
        // given - precondition or setup
        String departmentId = "1";
        willDoNothing().given(departmentService).deleteDepartment(departmentId);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/cm/d/delete/{departmentId}", departmentId));

        // then - verify the output
        response.andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenPageAndSize_whenGetDepartmentsInPages_thenReturnDepartmentsPage() throws Exception {
        // given - precondition or setup
        List<Department> departments = Arrays.asList(
                new Department("1", "CSE", "Computer Science", true, null, null, null, null),
                new Department("2", "ECE", "Electronics", true, null, null, null, null)
        );
        given(departmentService.getDepartmentsInPages(0, 2)).willReturn(departments);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/cm/d/page")
                .param("page", "0")
                .param("size", "2"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(departments.size())));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "USER" })
    public void givenDepName_whenGetDepartmentsSortedByDepName_thenReturnSortedDepartmentsList() throws Exception {
        // given - precondition or setup
        List<Department> departments = Arrays.asList(
                new Department("1", "CSE", "Computer Science", true, null, null, null, null),
                new Department("2", "ECE", "Electronics", true, null, null, null, null)
        );
        given(departmentService.sortBasedOnDepartmentName(anyString())).willReturn(departments);

        // when - action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(get("/cm/get/d/sort/CSE"));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(departments.size())));
    }

}