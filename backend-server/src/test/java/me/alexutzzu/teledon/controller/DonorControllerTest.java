package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.config.RsaKeyProperties;
import me.alexutzzu.teledon.controller.dto.CreateDonorRequest;
import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.dto.DonorDto;
import me.alexutzzu.teledon.model.dto.DonorWithoutDonations;
import me.alexutzzu.teledon.service.DonorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

@WebMvcTest(DonorController.class)
class DonorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DonorService donorService;

    @MockitoBean
    private RsaKeyProperties rsaKeys;

    @Test
    void getAllDonors_shouldReturnOk() throws Exception {
        List<DonorWithoutDonations> donors = Collections.singletonList(new DonorWithoutDonations(1L, "John", "Doe", "123 Main St", "1234567890", LocalDateTime.now()));
        Page<DonorWithoutDonations> donorsPage = new PageImpl<>(donors);
        when(donorService.getAllDonors(anyInt(), anyInt(), any())).thenReturn(donorsPage);

        mockMvc.perform(get("/api/donors"))
                .andExpect(status().isOk());

        verify(donorService).getAllDonors(anyInt(), anyInt(), any());
    }

    @Test
    void getDonor_shouldReturnOk() throws Exception {
        DonorDto donor = new DonorDto(1L, "John", "Doe", "123 Main St", "1234567890", Collections.emptyList(), LocalDateTime.now());
        when(donorService.getDonor(1L)).thenReturn(donor);

        String response = mockMvc.perform(get("/api/donors/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DonorDto actualDonor = objectMapper.readValue(response, DonorDto.class);
        assertThat(actualDonor).isEqualTo(donor);
        verify(donorService).getDonor(1L);
    }

    @Test
    void getDonor_invalidId_shouldReturnNotFound() throws Exception {
        when(donorService.getDonor(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/donors/1"))
                .andExpect(status().isNotFound());

        verify(donorService).getDonor(anyLong());
    }

    @Test
    void createDonor_shouldReturnCreated() throws Exception {
        DonorDto expected = new DonorDto(1L, "John", "Doe", "123 Main St", "1234567890", Collections.emptyList(), LocalDateTime.now());
        when(donorService.createDonor(expected.firstName(), expected.lastName(), expected.address(), expected.phoneNumber())).thenReturn(expected);

        CreateDonorRequest createDonorRequest = new CreateDonorRequest("John", "Doe", "123 Main St", "1234567890");

        String response = mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        DonorDto actualDonor = objectMapper.readValue(response, DonorDto.class);
        assertThat(actualDonor).isEqualTo(expected);
        verify(donorService).createDonor(expected.firstName(), expected.lastName(), expected.address(), expected.phoneNumber());
    }

    @Test
    void createDonor_invalidBody_shouldReturnBadRequest() throws Exception {
        CreateDonorRequest createDonorRequest = new CreateDonorRequest("", "Doe", "123 Main St", "1234567890");
        mockMvc.perform(post("/api/donors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDonor_shouldReturnOk() throws Exception {
        DonorDto original = new DonorDto(1L, "John", "Doe", "123 Main St", "1234567890", Collections.emptyList(), LocalDateTime.now());
        DonorDto newDonor = new DonorDto(1L, "Jane", "Doe", "456 Main St", "0987654321", Collections.emptyList(), original.createdOn());

        when(donorService.updateDonor(original.id(), newDonor.firstName(), newDonor.lastName(), newDonor.address(), newDonor.phoneNumber())).thenReturn(newDonor);

        CreateDonorRequest createDonorRequest = new CreateDonorRequest("Jane", "Doe", "456 Main St", "0987654321");

        String response = mockMvc.perform(put("/api/donors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        DonorDto actualDonor = objectMapper.readValue(response, DonorDto.class);
        assertThat(actualDonor).isEqualTo(newDonor);
        assertThat(actualDonor).isNotEqualTo(original);
        verify(donorService).updateDonor(newDonor.id(), newDonor.firstName(), newDonor.lastName(), newDonor.address(), newDonor.phoneNumber());
    }

    @Test
    void updateDonor_invalidId_ShouldReturnNotFound() throws Exception {
        when(donorService.updateDonor(anyLong(), anyString(), anyString(), anyString(), anyString())).thenThrow(NotFoundException.class);

        CreateDonorRequest createDonorRequest = new CreateDonorRequest("John", "Doe", "123 Main St", "1234567890");

        mockMvc.perform(put("/api/donors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDonorRequest)))
                .andExpect(status().isNotFound());

        verify(donorService).updateDonor(anyLong(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void deleteDonor_ShouldReturnNoContent() throws Exception {
        doNothing().when(donorService).deleteDonor(1L);

        mockMvc.perform(delete("/api/donors/1"))
                .andExpect(status().isNoContent());

        verify(donorService).deleteDonor(1L);
    }

    @Test
    void deleteDonor_invalidId_shouldReturnNotFound() throws Exception {
        doThrow(NotFoundException.class).when(donorService).deleteDonor(anyLong());

        mockMvc.perform(delete("/api/donors/1"))
                .andExpect(status().isNotFound());

        verify(donorService).deleteDonor(anyLong());
    }
}
