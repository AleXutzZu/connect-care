package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.IntegrationTest;
import me.alexutzzu.teledon.controller.dto.CreateDonorRequest;
import me.alexutzzu.teledon.model.Donor;
import me.alexutzzu.teledon.model.dto.DonorDto;
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

class DonorControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUpRepository() {
        donorRepository.deleteAll();
    }

    @Test
    void getAllDonors_noAuthorization_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/donors/")).andExpect(status().isUnauthorized());
    }

    @Test
    void getDonor_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var entity = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        mockMvc.perform(get("/api/donors/" + entity.getId())).andExpect(status().isUnauthorized());
    }

    @Test
    void createDonor_noAuthorization_shouldReturnUnauthorized() throws Exception {
        CreateDonorRequest createDonorRequest = new CreateDonorRequest("John", "Doe", "123 Main St", "1234567890");
        mockMvc.perform(post("/api/donors/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest)))
                .andExpect(status().isUnauthorized());

        assertThat(donorRepository.count()).isEqualTo(0);
    }

    @Test
    void updateDonor_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var entity = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        CreateDonorRequest createDonorRequest = new CreateDonorRequest("John", "Doe", "123 Main St", "1234567890");

        mockMvc.perform(put("/api/donors/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest)))
                .andExpect(status().isUnauthorized());

        var postPutEntity = donorRepository.findById(entity.getId());
        assertThat(postPutEntity).isPresent();

        assertThat(postPutEntity.get()).isEqualTo(entity);
    }

    @Test
    void deleteDonor_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var entity = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        mockMvc.perform(delete("/api/donors/" + entity.getId()))
                .andExpect(status().isUnauthorized());

        var postDeleteEntity = donorRepository.findById(entity.getId());
        assertThat(postDeleteEntity).isPresent();
    }

    @Test
    void getAllDonors_shouldReturnOk() throws Exception {
        donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        mockMvc.perform(get("/api/donors").with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isOk());
    }

    @Test
    void getDonor_shouldReturnOk() throws Exception {
        var entity = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        String response = mockMvc.perform(get("/api/donors/" + entity.getId())
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        var actualDonor = objectMapper.readValue(response, DonorDto.class);

        assertThat(actualDonor.firstName()).isEqualTo(entity.getFirstName());
        assertThat(actualDonor.lastName()).isEqualTo(entity.getLastName());
        assertThat(actualDonor.address()).isEqualTo(entity.getAddress());
        assertThat(actualDonor.phoneNumber()).isEqualTo(entity.getPhoneNumber());
        assertThat(actualDonor.id()).isEqualTo(entity.getId());
    }

    @Test
    void createDonor_shouldReturnCreated() throws Exception {
        CreateDonorRequest createDonorRequest = new CreateDonorRequest("John", "Doe", "123 Main St", "1234567890");

        String response = mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        var actualDonor = objectMapper.readValue(response, DonorDto.class);
        assertThat(actualDonor.firstName()).isEqualTo(createDonorRequest.firstName());
        assertThat(actualDonor.lastName()).isEqualTo(createDonorRequest.lastName());
        assertThat(actualDonor.address()).isEqualTo(createDonorRequest.address());
        assertThat(actualDonor.phoneNumber()).isEqualTo(createDonorRequest.phoneNumber());

        var donorInDatabase = donorRepository.findById(actualDonor.id());
        assertThat(donorInDatabase).isPresent();
        assertThat(donorInDatabase.get().getFirstName()).isEqualTo(actualDonor.firstName());
        assertThat(donorInDatabase.get().getLastName()).isEqualTo(actualDonor.lastName());
        assertThat(donorInDatabase.get().getAddress()).isEqualTo(actualDonor.address());
        assertThat(donorInDatabase.get().getPhoneNumber()).isEqualTo(actualDonor.phoneNumber());
    }

    @Test
    void createDonor_DuplicatePhoneNumber_shouldReturnConflict() throws Exception {
        CreateDonorRequest createDonorRequest = new CreateDonorRequest("John", "Doe", "123 Main St", "1234567890");

        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isConflict());

        assertThat(donorRepository.count()).isEqualTo(1);
    }

    @Test
    void updateDonor_shouldReturnOk() throws Exception {
        var entity = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        CreateDonorRequest createDonorRequest = new CreateDonorRequest("Jane", "Doe", "456 Main St", "0987654321");

        String result = mockMvc.perform(put("/api/donors/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        DonorDto actualDonor = objectMapper.readValue(result, DonorDto.class);

        var databaseEntity = donorRepository.findById(entity.getId());

        assertThat(databaseEntity).isPresent();
        assertThat(actualDonor.firstName()).isEqualTo(createDonorRequest.firstName());
        assertThat(actualDonor.lastName()).isEqualTo(createDonorRequest.lastName());
        assertThat(actualDonor.address()).isEqualTo(createDonorRequest.address());
        assertThat(actualDonor.phoneNumber()).isEqualTo(createDonorRequest.phoneNumber());
        assertThat(databaseEntity.get().getFirstName()).isEqualTo(actualDonor.firstName());
        assertThat(databaseEntity.get().getLastName()).isEqualTo(actualDonor.lastName());
        assertThat(databaseEntity.get().getAddress()).isEqualTo(actualDonor.address());
        assertThat(databaseEntity.get().getPhoneNumber()).isEqualTo(actualDonor.phoneNumber());
    }

    @Test
    void updateDonor_idempotencyTest() throws Exception {
        var entity = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        CreateDonorRequest createDonorRequest = new CreateDonorRequest("Jane", "Doe", "456 Main St", "0987654321");

        String result = mockMvc.perform(put("/api/donors/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        DonorDto firstUpdateDonor = objectMapper.readValue(result, DonorDto.class);

        result = mockMvc.perform(put("/api/donors/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        DonorDto secondUpdateDonor = objectMapper.readValue(result, DonorDto.class);

        assertThat(donorRepository.count()).isEqualTo(1L);

        assertThat(firstUpdateDonor).isEqualTo(secondUpdateDonor);
        var databaseEntity = donorRepository.findById(entity.getId());

        assertThat(databaseEntity).isPresent();
        assertThat(firstUpdateDonor.firstName()).isEqualTo(createDonorRequest.firstName());
        assertThat(databaseEntity.get().getFirstName()).isEqualTo(firstUpdateDonor.firstName());
    }

    @Test
    void updateDonor_invalidPhoneNumber_ShouldReturnConflict() throws Exception {
        var entity = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));
        donorRepository.save(Donor.ofDetails("Jane", "Doe", "456 Main St", "0987654321"));

        CreateDonorRequest createDonorRequest = new CreateDonorRequest("John", "Doe", "123 Main St", "0987654321");

        mockMvc.perform(put("/api/donors/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isConflict());

        var actualEntity = donorRepository.findById(entity.getId());

        assertThat(donorRepository.count()).isEqualTo(2);
        assertThat(actualEntity).isPresent();

        assertThat(actualEntity.get()).isEqualTo(entity);
    }

    @Test
    void deleteDonor_shouldReturnNoContent() throws Exception {
        var entity = donorRepository.save(Donor.ofDetails("John", "Doe", "123 Main St", "1234567890"));

        mockMvc.perform(delete("/api/donors/" + entity.getId())
                        .with(jwt().jwt(builder -> builder.claim("scope", "USER"))))
                .andExpect(status().isNoContent());

        assertThat(donorRepository.count()).isEqualTo(0);
    }
}
