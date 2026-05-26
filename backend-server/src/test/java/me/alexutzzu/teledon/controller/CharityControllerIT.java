package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.IntegrationTest;
import me.alexutzzu.teledon.controller.dto.CreateCharityRequest;
import me.alexutzzu.teledon.model.Charity;
import me.alexutzzu.teledon.model.User;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.persistence.CharityRepository;
import me.alexutzzu.teledon.persistence.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CharityControllerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CharityRepository charityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder().username("user").password(passwordEncoder.encode("password")).build());
    }

    @AfterEach
    void cleanUpRepository() {
        charityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllCharities_noAuthorization_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/charities/")).andExpect(status().isUnauthorized());
    }

    @Test
    void getCharity_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var entity = charityRepository.save(Charity.of("Test Charity", user, 1000.0));

        mockMvc.perform(get("/api/charities/" + entity.getId())).andExpect(status().isUnauthorized());
    }

    @Test
    void createCharity_noAuthorization_shouldReturnUnauthorized() throws Exception {
        CreateCharityRequest createCharityRequest = new CreateCharityRequest("New Charity", 1000.);
        mockMvc.perform(post("/api/charities/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest)))
                .andExpect(status().isUnauthorized());

        assertThat(charityRepository.count()).isEqualTo(0);
    }

    @Test
    void updateCharity_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var entity = charityRepository.save(Charity.of("Test Charity", user, 1000.0));

        CreateCharityRequest createCharityRequest = new CreateCharityRequest("New Charity Name", 2000.0);

        mockMvc.perform(put("/api/charities/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest)))
                .andExpect(status().isUnauthorized());

        var postPutEntity = charityRepository.findById(entity.getId());
        assertThat(postPutEntity).isPresent();

        assertThat(postPutEntity.get()).isEqualTo(entity);
    }

    @Test
    void deleteCharity_noAuthorization_shouldReturnUnauthorized() throws Exception {
        var entity = charityRepository.save(Charity.of("Test Charity", user, 1000.0));

        mockMvc.perform(delete("/api/charities/" + entity.getId()))
                .andExpect(status().isUnauthorized());

        var postDeleteEntity = charityRepository.findById(entity.getId());
        assertThat(postDeleteEntity).isPresent();
    }

    @Test
    void getAllCharities_shouldReturnOk() throws Exception {
        charityRepository.save(Charity.of("Test Charity", user, 1000.0));

        mockMvc.perform(get("/api/charities").with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isOk());
    }

    @Test
    void getCharity_shouldReturnOk() throws Exception {
        var entity = charityRepository.save(Charity.of("Test Charity", user, 1000.0));

        String response = mockMvc.perform(get("/api/charities/" + entity.getId())
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        var actualCharity = objectMapper.readValue(response, CharityDto.class);

        assertThat(actualCharity.name()).isEqualTo(entity.getName());
        assertThat(actualCharity.id()).isEqualTo(entity.getId());
    }

    @Test
    void createCharity_shouldReturnCreated() throws Exception {
        CreateCharityRequest createCharityRequest = new CreateCharityRequest("Test Charity", 1000.0);

        String response = mockMvc.perform(post("/api/charities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        var actualCharity = objectMapper.readValue(response, CharityDto.class);
        assertThat(actualCharity.name()).isEqualTo(createCharityRequest.name());

        var charityInDatabase = charityRepository.findById(actualCharity.id());
        assertThat(charityInDatabase).isPresent();
        assertThat(charityInDatabase.get().getName()).isEqualTo(actualCharity.name());
    }

    @Test
    void createCharity_DuplicateName_shouldReturnConflict() throws Exception {
        CreateCharityRequest createCharityRequest = new CreateCharityRequest("Test Charity", 1000.0);

        mockMvc.perform(post("/api/charities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/charities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isConflict());

        assertThat(charityRepository.count()).isEqualTo(1);
    }

    @Test
    void updateCharity_shouldReturnOk() throws Exception {
        var entity = charityRepository.save(Charity.of("Test Charity", user, 1000.0));

        CreateCharityRequest createCharityRequest = new CreateCharityRequest("New Charity Name", 2000.0);

        String result = mockMvc.perform(put("/api/charities/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        CharityDto actualCharity = objectMapper.readValue(result, CharityDto.class);

        var databaseEntity = charityRepository.findById(entity.getId());

        assertThat(databaseEntity).isPresent();
        assertThat(actualCharity.name()).isEqualTo(createCharityRequest.name());
        assertThat(databaseEntity.get().getName()).isEqualTo(actualCharity.name());
    }

    @Test
    void updateCharity_idempotencyTest() throws Exception {
        var entity = charityRepository.save(Charity.of("Test Charity", user, 1000.0));

        CreateCharityRequest createCharityRequest = new CreateCharityRequest("New Charity Name", 2000.0);

        String result = mockMvc.perform(put("/api/charities/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        CharityDto firstUpdateCharity = objectMapper.readValue(result, CharityDto.class);

        result = mockMvc.perform(put("/api/charities/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        CharityDto secondUpdateCharity = objectMapper.readValue(result, CharityDto.class);

        assertThat(charityRepository.count()).isEqualTo(1L);

        assertThat(firstUpdateCharity).isEqualTo(secondUpdateCharity);
        var databaseEntity = charityRepository.findById(entity.getId());

        assertThat(databaseEntity).isPresent();
        assertThat(firstUpdateCharity.name()).isEqualTo(createCharityRequest.name());
        assertThat(databaseEntity.get().getName()).isEqualTo(firstUpdateCharity.name());
    }

    @Test
    void updateCharity_invalidName_ShouldReturnConflict() throws Exception {
        var entity = charityRepository.save(Charity.of("Test Charity", user, 1000.0));
        charityRepository.save(Charity.of("New Charity Name", user, 1000.0));

        CreateCharityRequest createCharityRequest = new CreateCharityRequest("New Charity Name", 2000.0);

        mockMvc.perform(put("/api/charities/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isConflict());

        var actualEntity = charityRepository.findById(entity.getId());

        assertThat(charityRepository.count()).isEqualTo(2);
        assertThat(actualEntity).isPresent();

        assertThat(actualEntity.get()).isEqualTo(entity);
    }

    @Test
    void deleteCharity_shouldReturnNoContent() throws Exception {
        var entity = charityRepository.save(Charity.of("Test Charity", user, 1000.0));

        mockMvc.perform(delete("/api/charities/" + entity.getId())
                        .with(jwt().jwt(builder -> builder.subject("user").claim("scope", "USER"))))
                .andExpect(status().isNoContent());

        assertThat(charityRepository.count()).isEqualTo(0);
    }
}
