package kr.megaptera.backendsurvivalweek10.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WelcomeController.class)
class WelcomeControllerTest extends ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET / - with correct Access Token")
    void homeWithAccessToken() throws Exception {
        mockMvc.perform(get("/")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET / - with incorrect Access Token")
    void homeWithIncorrectAccessToken() throws Exception {
        mockMvc.perform(get("/")
                        .header("Authorization", "Bearer XXX"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET / - without Access Token")
    void homeWithoutAccessToken() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /admin - when the current user is ROLE_ADMIN")
    void adminWithRoleAdmin() throws Exception {
        mockMvc.perform(get("/admin")
                        .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /admin - when the current user is ROLE_USER")
    void adminWithRoleUser() throws Exception {
        mockMvc.perform(get("/admin")
                        .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /user - when the current user is ROLE_USER or ROLE_ADMIN")
    void userWithRoleUserOrAdmin() throws Exception {
        for (var accessToken : List.of(userAccessToken, adminAccessToken)) {
            mockMvc.perform(get("/user")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        }
    }
}