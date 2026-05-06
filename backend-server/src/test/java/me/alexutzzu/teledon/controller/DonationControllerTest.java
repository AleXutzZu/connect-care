package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.config.RsaKeyProperties;
import me.alexutzzu.teledon.controller.dto.CreateDonationRequest;
import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.dto.DonationDto;
import me.alexutzzu.teledon.service.DonationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DonationController.class)
class DonationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DonationService donationService;

    @MockitoBean
    private RsaKeyProperties rsaKeys;

    @Test
    void getAllDonations_shouldReturnOk() throws Exception {
        List<DonationDto> donations = Collections.singletonList(new DonationDto(1L, 100.0, 1L, "John", "Doe", 1L, "Test Charity", LocalDateTime.now()));
        when(donationService.getAllDonations()).thenReturn(donations);

        mockMvc.perform(get("/api/donations"))
                .andExpect(status().isOk());

        verify(donationService).getAllDonations();
    }

    @Test
    void getDonation_shouldReturnOk() throws Exception {
        DonationDto donation = new DonationDto(1L, 100.0, 1L, "John", "Doe", 1L, "Test Charity", LocalDateTime.now());
        when(donationService.getDonation(1L)).thenReturn(donation);

        String response = mockMvc.perform(get("/api/donations/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DonationDto actualDonation = objectMapper.readValue(response, DonationDto.class);
        assertThat(actualDonation).isEqualTo(donation);
        verify(donationService).getDonation(1L);
    }

    @Test
    void getDonation_invalidId_shouldReturnNotFound() throws Exception {
        when(donationService.getDonation(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/donations/1"))
                .andExpect(status().isNotFound());

        verify(donationService).getDonation(anyLong());
    }

    @Test
    void createDonation_shouldReturnCreated() throws Exception {
        DonationDto expected = new DonationDto(1L, 100.0, 1L, "John", "Doe", 1L, "Test Charity", LocalDateTime.now());
        when(donationService.createDonation(any(), any(), any())).thenReturn(expected);

        CreateDonationRequest createDonationRequest = new CreateDonationRequest(1L, 1L, 100.0);

        String response = mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonationRequest))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        DonationDto actualDonation = objectMapper.readValue(response, DonationDto.class);
        assertThat(actualDonation).isEqualTo(expected);
        verify(donationService).createDonation(1L, 1L, 100.0);
    }

    @Test
    void createDonation_invalidBody_shouldReturnBadRequest() throws Exception {
        CreateDonationRequest createDonationRequest = new CreateDonationRequest(1L, 1L, -100.0);
        mockMvc.perform(post("/api/donations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonationRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDonation_shouldReturnOk() throws Exception {
        LocalDateTime creationTime = LocalDateTime.now();
        DonationDto original = new DonationDto(1L, 100.0, 1L, "John", "Doe", 1L, "Test Charity", creationTime);
        DonationDto newDonation = new DonationDto(1L, 200.0, 1L, "John", "Doe", 1L, "Test Charity", creationTime);

        when(donationService.updateDonation(anyLong(), any(), any(), any())).thenReturn(newDonation);

        CreateDonationRequest createDonationRequest = new CreateDonationRequest(1L, 1L, 200.0);

        String response = mockMvc.perform(put("/api/donations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonationRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        DonationDto actualDonation = objectMapper.readValue(response, DonationDto.class);
        assertThat(actualDonation).isEqualTo(newDonation);
        assertThat(actualDonation).isNotEqualTo(original);
        verify(donationService).updateDonation(1L, 1L, 1L, 200.0);
    }

    @Test
    void updateDonation_invalidId_ShouldReturnNotFound() throws Exception {
        when(donationService.updateDonation(anyLong(), any(), any(), any())).thenThrow(NotFoundException.class);

        CreateDonationRequest createDonationRequest = new CreateDonationRequest(1L, 1L, 100.0);

        mockMvc.perform(put("/api/donations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonationRequest)))
                .andExpect(status().isNotFound());

        verify(donationService).updateDonation(anyLong(), any(), any(), any());
    }

    @Test
    void deleteDonation_ShouldReturnNoContent() throws Exception {
        doNothing().when(donationService).deleteDonation(1L);

        mockMvc.perform(delete("/api/donations/1"))
                .andExpect(status().isNoContent());

        verify(donationService).deleteDonation(1L);
    }

    @Test
    void deleteDonation_invalidId_shouldReturnNotFound() throws Exception {
        doThrow(NotFoundException.class).when(donationService).deleteDonation(anyLong());

        mockMvc.perform(delete("/api/donations/1"))
                .andExpect(status().isNotFound());

        verify(donationService).deleteDonation(anyLong());
    }
}
