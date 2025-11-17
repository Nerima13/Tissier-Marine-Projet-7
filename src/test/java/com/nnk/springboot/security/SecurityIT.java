package com.nnk.springboot.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIT {

    @Autowired
    private MockMvc mockMvc;

    // 1) Anonymous user trying to access a protected page => redirected to /login
    @Test
    void whenAnonymousAccessesProtectedUrl_thenRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isFound())          // 302 explicit
                .andExpect(redirectedUrl("/login"));    // exact redirect target
    }

    // 2) User with role USER trying to access /user/** (ADMIN required) => 403 Forbidden
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void whenUserRoleAccessesAdminUrl_thenForbidden() throws Exception {
        mockMvc.perform(get("/user/list"))
                .andExpect(status().isForbidden());
    }

    // 3) User with role ADMIN accessing /user/** => allowed (HTTP 200 + correct view)
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenAdminAccessesAdminUrl_thenOk() throws Exception {
        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"));
    }

    // 4) Anonymous user can access /login (public URL)
    @Test
    void whenAnonymousAccessesLogin_thenOk() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }
}
