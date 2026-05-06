package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.IntegrationTest;
import me.alexutzzu.teledon.controller.dto.CreateDonorRequest;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.persistence.DonorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class DonorControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DonorRepository donorRepository;

    @Test
    void getAllDonors_shouldReturnOk() throws Exception {
        donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        mockMvc.perform(get("/api/donors")
                        .with(jwt().jwt(builder -> builder.claim("scope", "ROLE_ADMIN"))))
                .andExpect(status().isOk());
    }

    @Test
    void createDonor_shouldReturnOk() throws Exception {
        CreateDonorRequest request = new CreateDonorRequest("John", "Doe", "123 Main St", "1234567890");

        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(builder -> builder.claim("scope", "ROLE_ADMIN"))))
                .andExpect(status().isCreated());
    }
}
