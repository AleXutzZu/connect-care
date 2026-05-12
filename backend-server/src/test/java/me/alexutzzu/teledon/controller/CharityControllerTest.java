package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.config.RsaKeyProperties;
import me.alexutzzu.teledon.controller.dto.CreateCharityRequest;
import me.alexutzzu.teledon.exception.NotFoundException;
import me.alexutzzu.teledon.model.dto.CharityDto;
import me.alexutzzu.teledon.model.dto.CharityWithRaisedSum;
import me.alexutzzu.teledon.service.CharityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharityController.class)
class CharityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CharityService charityService;

    @MockitoBean
    private RsaKeyProperties rsaKeys;

    @Test
    void getAllCharities_shouldReturnOk() throws Exception {
        List<CharityWithRaisedSum> charities = Collections.singletonList(new CharityWithRaisedSum(1L, "Test Charity", "user", 1000.0, "cause", 100.0));
        Page<CharityWithRaisedSum> charityPage = new PageImpl<>(charities);
        when(charityService.getAllCharities(anyInt(), anyInt())).thenReturn(charityPage);

        mockMvc.perform(get("/api/charities").with(jwt()))
                .andExpect(status().isOk());

        verify(charityService).getAllCharities(anyInt(), anyInt());
    }

    @Test
    void getCharity_shouldReturnOk() throws Exception {
        CharityDto charity = new CharityDto(1L, "Test Charity", "user", 1000.0, "cause", Collections.emptyList());
        when(charityService.getCharity(1L)).thenReturn(charity);

        String response = mockMvc.perform(get("/api/charities/1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CharityDto actualCharity = objectMapper.readValue(response, CharityDto.class);
        assertThat(actualCharity).isEqualTo(charity);
        verify(charityService).getCharity(1L);
    }

    @Test
    void getCharity_invalidId_shouldReturnNotFound() throws Exception {
        when(charityService.getCharity(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/charities/1"))
                .andExpect(status().isNotFound());

        verify(charityService).getCharity(anyLong());
    }

    @Test
    void createCharity_shouldReturnCreated() throws Exception {
        CharityDto expected = new CharityDto(1L, "Test Charity", "user", 1000.0, "cause", Collections.emptyList());
        when(charityService.createCharity(any(CreateCharityRequest.class), anyString())).thenReturn(expected);

        CreateCharityRequest createCharityRequest = new CreateCharityRequest(expected.name(), expected.target(), expected.cause());

        Authentication mockAuth = new TestingAuthenticationToken("user", "password");

        String response = mockMvc.perform(post("/api/charities")
                        .principal(mockAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();

        CharityDto actualCharity = objectMapper.readValue(response, CharityDto.class);
        assertThat(actualCharity).isEqualTo(expected);
        verify(charityService).createCharity(any(CreateCharityRequest.class), anyString());
    }

    @Test
    void createCharity_invalidBody_shouldReturnBadRequest() throws Exception {
        CreateCharityRequest createCharityRequest = new CreateCharityRequest("", -1.0, "");
        mockMvc.perform(post("/api/charities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCharity_shouldReturnOk() throws Exception {
        CharityDto original = new CharityDto(1L, "Test Charity", "user", 1000.0, "cause", Collections.emptyList());
        CharityDto newCharity = new CharityDto(1L, "New Name", "user", 2000.0, "new cause", Collections.emptyList());

        when(charityService.updateCharity(anyLong(), any(CreateCharityRequest.class))).thenReturn(newCharity);

        CreateCharityRequest createCharityRequest = new CreateCharityRequest(newCharity.name(), newCharity.target(), newCharity.cause());

        String response = mockMvc.perform(put("/api/charities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        CharityDto actualCharity = objectMapper.readValue(response, CharityDto.class);
        assertThat(actualCharity).isEqualTo(newCharity);
        assertThat(actualCharity).isNotEqualTo(original);
        verify(charityService).updateCharity(anyLong(), any(CreateCharityRequest.class));
    }

    @Test
    void updateCharity_invalidId_ShouldReturnNotFound() throws Exception {
        when(charityService.updateCharity(anyLong(), any(CreateCharityRequest.class))).thenThrow(NotFoundException.class);

        CreateCharityRequest createCharityRequest = new CreateCharityRequest("Test", 1.0, "cause");

        mockMvc.perform(put("/api/charities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCharityRequest)))
                .andExpect(status().isNotFound());

        verify(charityService).updateCharity(anyLong(), any(CreateCharityRequest.class));
    }

    @Test
    void deleteCharity_ShouldReturnNoContent() throws Exception {
        doNothing().when(charityService).deleteCharity(1L);

        mockMvc.perform(delete("/api/charities/1"))
                .andExpect(status().isNoContent());

        verify(charityService).deleteCharity(1L);
    }

    @Test
    void deleteCharity_invalidId_shouldReturnNotFound() throws Exception {
        doThrow(NotFoundException.class).when(charityService).deleteCharity(anyLong());

        mockMvc.perform(delete("/api/charities/1"))
                .andExpect(status().isNotFound());

        verify(charityService).deleteCharity(anyLong());
    }
}
