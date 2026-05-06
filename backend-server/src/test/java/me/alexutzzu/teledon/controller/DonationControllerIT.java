package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.IntegrationTest;
import me.alexutzzu.teledon.controller.dto.CreateDonationRequest;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.Donation;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.model.dto.DonationDto;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.DonationRepository;
import me.alexutzzu.teledon.persistence.DonorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DonationControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private CharityRepository charityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUpRepository() {
        donationRepository.deleteAll();
        donorRepository.deleteAll();
        charityRepository.deleteAll();
    }

    @Test
    void getAllDonations_noAuthorization_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/donations/")).andExpect(status().isUnauthorized());
    }

    @Test
    void getDonation_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var donor = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        var charity = charityRepository.save(Charity.ofName("Test Charity"));
        var entity = donationRepository.save(Donation.ofDetails(charity, donor, 100.0));

        mockMvc.perform(get("/api/donations/" + entity.getId())).andExpect(status().isUnauthorized());
    }

    @Test
    void createDonation_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var donor = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        var charity = charityRepository.save(Charity.ofName("Test Charity"));
        CreateDonationRequest createDonationRequest = new CreateDonationRequest(charity.getId(), donor.getId(), 100.0);
        mockMvc.perform(post("/api/donations/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonationRequest)))
                .andExpect(status().isUnauthorized());

        assertThat(donationRepository.count()).isEqualTo(0);
    }

    @Test
    void deleteDonation_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var donor = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        var charity = charityRepository.save(Charity.ofName("Test Charity"));
        var entity = donationRepository.save(Donation.ofDetails(charity, donor, 100.0));

        mockMvc.perform(delete("/api/donations/" + entity.getId()))
                .andExpect(status().isUnauthorized());

        var postDeleteEntity = donationRepository.findById(entity.getId());
        assertThat(postDeleteEntity).isPresent();
    }

    @Test
    void getAllDonations_shouldReturnOk() throws Exception {
        var donor = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        var charity = charityRepository.save(Charity.ofName("Test Charity"));
        donationRepository.save(Donation.ofDetails(charity, donor, 100.0));

        mockMvc.perform(get("/api/donations").with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isOk());
    }

    @Test
    void getDonation_shouldReturnOk() throws Exception {
        var donor = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        var charity = charityRepository.save(Charity.ofName("Test Charity"));
        var entity = donationRepository.save(Donation.ofDetails(charity, donor, 100.0));

        String response = mockMvc.perform(get("/api/donations/" + entity.getId())
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        var actualDonation = objectMapper.readValue(response, DonationDto.class);

        assertThat(actualDonation.donorId()).isEqualTo(entity.getDonor().getId());
        assertThat(actualDonation.charityId()).isEqualTo(entity.getCharity().getId());
        assertThat(actualDonation.amount()).isEqualTo(entity.getAmount());
        assertThat(actualDonation.id()).isEqualTo(entity.getId());
    }

    @Test
    void createDonation_shouldReturnCreated() throws Exception {
        var donor = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        var charity = charityRepository.save(Charity.ofName("Test Charity"));
        CreateDonationRequest createDonationRequest = new CreateDonationRequest(charity.getId(), donor.getId(), 100.0);

        String response = mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonationRequest))
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        var actualDonation = objectMapper.readValue(response, DonationDto.class);
        assertThat(actualDonation.donorId()).isEqualTo(createDonationRequest.donorId());
        assertThat(actualDonation.charityId()).isEqualTo(createDonationRequest.charityId());
        assertThat(actualDonation.amount()).isEqualTo(createDonationRequest.amount());

        var donationInDatabase = donationRepository.findById(actualDonation.id());
        assertThat(donationInDatabase).isPresent();
        assertThat(donationInDatabase.get().getDonor().getId()).isEqualTo(actualDonation.donorId());
        assertThat(donationInDatabase.get().getCharity().getId()).isEqualTo(actualDonation.charityId());
        assertThat(donationInDatabase.get().getAmount()).isEqualTo(actualDonation.amount());
    }

    @Test
    void deleteDonation_shouldReturnNoContent() throws Exception {
        var donor = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        var charity = charityRepository.save(Charity.ofName("Test Charity"));
        var entity = donationRepository.save(Donation.ofDetails(charity, donor, 100.0));

        mockMvc.perform(delete("/api/donations/" + entity.getId())
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isNoContent());

        assertThat(donationRepository.count()).isEqualTo(0);
    }
}
