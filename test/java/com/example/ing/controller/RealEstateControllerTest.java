package com.example.ing.controller;

import com.example.ing.model.enumeration.RegionId;
import com.example.ing.service.RealEstateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RealEstateControllerTest {
    @InjectMocks
    RealEstateController sut;

    @Mock
    private RealEstateServiceImpl realEstateService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
    }

    @Test
    void testConrollerSucces() throws Exception {
        mockMvc.perform(get("/api/real-estates-stats/{regionID}", RegionId.DLN_POZA_WROC)
                .param("regionID", "DLN_WROC_C")
                .param("size", "L")
                .param("rooms", "3")
                .param("types", "flat")
                .param("dateSince", "20220101")
                .param("dateUntil", "20220102"))
                .andExpect(status().isOk());
    }

    @Test
    void testConrollerBadRequest() throws Exception {
        mockMvc.perform(get("/api/real-estates-stats/{regionID}", RegionId.DLN_POZA_WROC)
                .param("regionID", "DLN_WROC_C")
                .param("size", "L")
                .param("rooms", "3")
                .param("types", "flat")
                .param("dateSince", "invalid date")
                .param("dateUntil", "20220102"))
                .andExpect(status().isBadRequest());
    }
}
