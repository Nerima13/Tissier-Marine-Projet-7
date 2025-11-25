package com.nnk.springboot.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Spring Security configuration.
 * <p>
 * Verifies that:
 * <ul>
 *   <li>Anonymous users are redirected to the login page for protected URLs</li>
 *   <li>Users with role USER cannot access /user/** (ADMIN only)</li>
 *   <li>Users with role ADMIN can access /user/**</li>
 *   <li>Authenticated users can access general protected URLs</li>
 *   <li>The login page is publicly accessible</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Anonymous user trying to access a protected page (/bidList/list)
     * should be redirected to the custom login page (/login).
     */
    @Test
    void whenAnonymousAccessesProtectedUrl_thenRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isFound())                     // 302
                .andExpect(redirectedUrlPattern("**/login"));      // http://localhost/login
    }

    /**
     * Authenticated user with role USER trying to access an ADMIN-only URL (/user/list)
     * should receive HTTP 403 Forbidden.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void whenUserRoleAccessesAdminUrl_thenForbidden() throws Exception {
        mockMvc.perform(get("/user/list"))
                .andExpect(status().isForbidden());
    }

    /**
     * Authenticated user with role ADMIN accessing /user/list
     * should be allowed and receive the "user/list" view.
     */
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void whenAdminAccessesAdminUrl_thenOkAndCorrectView() throws Exception {
        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"));
    }

    /**
     * Authenticated user with role USER accessing a general protected URL (/bidList/list)
     * should be allowed and receive the "bidList/list" view.
     */
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void whenUserAccessesGeneralProtectedUrl_thenOkAndCorrectView() throws Exception {
        mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("bidList/list"));
    }

    /**
     * Anonymous user should be able to access the login page (/login),
     * which is configured as a public URL.
     */
    @Test
    void whenAnonymousAccessesLogin_thenOkAndCorrectView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}