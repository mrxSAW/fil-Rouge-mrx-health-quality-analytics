package org.example.healthcarequalite.controller;

import org.example.healthcarequalite.config.SecurityConfig;
import org.example.healthcarequalite.exception.GlobalExceptionHandler;
import org.example.healthcarequalite.exception.ResourceNotFoundException;
import org.example.healthcarequalite.security.CustomAccessDeniedHandler;
import org.example.healthcarequalite.security.JwtAuthenticationEntryPoint;
import org.example.healthcarequalite.security.JwtAuthenticationFilter;
import org.example.healthcarequalite.security.JwtService;
import org.example.healthcarequalite.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class, GlobalExceptionHandler.class})
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void anonymousUserShouldReceive401() throws Exception {

        mockMvc.perform(get("/api/departments")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        verifyNoInteractions(departmentService);
    }

    @Test
    void invalidTokenShouldReceive401() throws Exception {

        when(jwtService.isValid("invalid-token")).thenReturn(false);

        mockMvc.perform(get("/api/departments").header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(departmentService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldReadPaginatedDepartments() throws Exception {

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));

        when(departmentService.findAll(any(Pageable.class))).thenReturn(Page.empty(pageable));

        mockMvc.perform(get("/api/departments").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));

        verify(departmentService).findAll(pageable);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void staffShouldReadDepartments() throws Exception {

        when(departmentService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/departments")).andExpect(status().isOk());

        verify(departmentService).findAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void staffShouldNotDeleteDepartment() throws Exception {

        mockMvc.perform(delete("/api/departments/1")).andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Accès refusé"));

        verifyNoInteractions(departmentService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldDeleteDepartment() throws Exception {

        mockMvc.perform(delete("/api/departments/1")).andExpect(status().isNoContent());

        verify(departmentService).delete(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void negativePageShouldReceive400() throws Exception {

        mockMvc.perform(get("/api/departments").param("page", "-1"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists());

        verifyNoInteractions(departmentService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void excessivePageSizeShouldReceive400() throws Exception {

        mockMvc.perform(get("/api/departments").param("size", "101"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(departmentService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void missingDepartmentShouldReceive404() throws Exception {

        when(departmentService.findById(999L)).thenThrow(
                        new ResourceNotFoundException( "Département introuvable : 999" )
                );

        mockMvc.perform(get("/api/departments/999")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Département introuvable : 999"));
    }





}