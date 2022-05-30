package com.example.ing.service;

import com.example.ing.dao.RealEstateRepo;
import com.example.ing.model.dto.RealEstateResponseDTO;
import com.example.ing.model.dto.RealEstateStatsRequestDTO;
import com.example.ing.model.dto.RealEstateStatsResponseDTO;
import com.example.ing.model.enumeration.RealEstateSize;
import com.example.ing.model.enumeration.RealEstateType;
import com.example.ing.model.enumeration.RegionId;
import com.example.ing.model.util.DateRange;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class RealEstateServiceImplTest {
    @InjectMocks
    RealEstateServiceImpl sut;
    @Mock
    private RealEstateRepo mockRealEstateRepo;
    @Mock
    private JavaMailSender mockJavaMailSender;
    @Mock
    private RestTemplate mockRestTemplate;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private RealEstateResponseDTO getRealEstateResponseDTO() throws IOException {
        File jsonFile = new ClassPathResource("RealEstates.json").getFile();
        return new ObjectMapper().readValue(jsonFile, RealEstateResponseDTO.class);
    }

    private final RealEstateResponseDTO emptyRealEstateRespones = RealEstateResponseDTO
            .builder()
            .totalPages(87)
            .data(new ArrayList<>())
            .build();

    @Test
    void testHarvestRealEstateData() throws IOException {
        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(1))).thenReturn(ResponseEntity.ok().body(getRealEstateResponseDTO()));
        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(87))).thenReturn(ResponseEntity.ok().body(emptyRealEstateRespones));
        sut.getRealEstateSalesData();
    }

    @Test
    void testMissingData() {
        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(1))).thenReturn(ResponseEntity.ok().body(null));
        sut.getRealEstateSalesData();
    }

    @Test
    void testExceptionWhileSavingRealEstate() throws IOException {
        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(1))).thenReturn(ResponseEntity.ok().body(getRealEstateResponseDTO()));
        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(87))).thenReturn(ResponseEntity.ok().body(emptyRealEstateRespones));
        when(mockRealEstateRepo.save(any())).thenThrow(new IllegalArgumentException());
        sut.getRealEstateSalesData();
    }

    @Test
    void testExceptionWhileGettingRealEstates() {
        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(1))).thenReturn(ResponseEntity.badRequest().body(null));
        sut.getRealEstateSalesData();
    }

    @Test
    void testExceptionWhileCastingInvalidArea() throws IOException {
        File jsonFile = new ClassPathResource("RealEstateInvalidArea.json").getFile();
        RealEstateResponseDTO realEstateResponseDTO = new ObjectMapper().readValue(jsonFile, RealEstateResponseDTO.class);

        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(1))).thenReturn(ResponseEntity.ok().body(realEstateResponseDTO));
        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(87))).thenReturn(ResponseEntity.ok().body(emptyRealEstateRespones));
        sut.getRealEstateSalesData();
    }

    @Test
    void testExceptionWhileCastingInvalidSize() throws IOException {
        File jsonFile = new ClassPathResource("RealEstateInvalidSize.json").getFile();
        RealEstateResponseDTO realEstateResponseDTO = new ObjectMapper().readValue(jsonFile, RealEstateResponseDTO.class);

        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(1))).thenReturn(ResponseEntity.ok().body(realEstateResponseDTO));
        when(mockRestTemplate.getForEntity(any(), any(), any(), eq(87))).thenReturn(ResponseEntity.ok().body(emptyRealEstateRespones));
        sut.getRealEstateSalesData();
    }

    @Test
    void testAveragePrice() {
        RealEstateStatsRequestDTO request = RealEstateStatsRequestDTO
                .builder()
                .dateRange(new DateRange("20220101", "20220102"))
                .types(new HashSet<>(Arrays.asList(RealEstateType.flat, RealEstateType.detached_house)))
                .rooms(5)
                .size(RealEstateSize.L)
                .regionId(RegionId.DLN_POZA_WROC)
                .build();

        when(mockRealEstateRepo.findRealEstateAveragePrice(any())).thenReturn(5.00);

        RealEstateStatsResponseDTO response = sut.getRealEstatesAveragePrice(request);

        assertEquals(response.getAvgValue(), "5.0");
    }
}
