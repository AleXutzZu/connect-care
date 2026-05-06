package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.config.RsaKeyProperties;
import me.alexutzzu.teledon.model.dto.CharityWithRaisedSum;
import me.alexutzzu.teledon.service.CharityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharityController.class)
class CharityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CharityService charityService;

    @MockitoBean
    private RsaKeyProperties rsaKeys;

    @Test
    void getAllCharities_shouldReturnOk() throws Exception {
        List<CharityWithRaisedSum> charities = Collections.singletonList(new CharityWithRaisedSum(1L, "Test Charity", 100.0));
        when(charityService.getAllCharities()).thenReturn(charities);

        mockMvc.perform(get("/api/charities"))
                .andExpect(status().isOk());
    }
}
