package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.config.RsaKeyProperties;
import me.alexutzzu.teledon.controller.dto.CreateDonorRequest;
import me.alexutzzu.teledon.model.dto.DonorWithoutDonations;
import me.alexutzzu.teledon.service.DonorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DonorController.class)
class DonorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DonorService donorService;

    @MockitoBean
    private RsaKeyProperties rsaKeyProperties;

    @Test
    void getAllDonors_shouldReturnOk() throws Exception {
        List<DonorWithoutDonations> donors = Collections.singletonList(new DonorWithoutDonations(1L, "John", "Doe", "123 Main St", "1234567890"));
        when(donorService.getAllDonors()).thenReturn(donors);

        mockMvc.perform(get("/api/donors"))
                .andExpect(status().isOk());
    }

    @Test
    void createDonor_shouldReturnOk() throws Exception {
        CreateDonorRequest request = new CreateDonorRequest("John", "Doe", "123 Main St", "1234567890");

        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
