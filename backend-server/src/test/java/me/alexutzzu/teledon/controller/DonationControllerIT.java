package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.IntegrationTest;
import me.alexutzzu.teledon.controller.dto.CreateDonationRequest;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class DonationControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CharityRepository charityRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Test
    void createDonation_shouldReturnOk() throws Exception {
        Charity charity = charityRepository.save(Charity.ofName("Test Charity"));
        Donor donor = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        CreateDonationRequest request = new CreateDonationRequest(charity.getId(), donor.getId(), 100.0);

        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(builder -> builder.claim("scope", "ROLE_ADMIN"))))
                .andExpect(status().isCreated());
    }
}
