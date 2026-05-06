package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.IntegrationTest;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.persistence.CharityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CharityControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CharityRepository charityRepository;

    @Test
    void getAllCharities_shouldReturnOk() throws Exception {
        charityRepository.save(Charity.ofName("Test Charity"));

        mockMvc.perform(get("/api/charities").with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isOk());
    }
}
