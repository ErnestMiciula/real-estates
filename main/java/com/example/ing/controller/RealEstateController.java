package com.example.ing.controller;

import com.example.ing.model.dto.RealEstateStatsRequestDTO;
import com.example.ing.model.dto.RealEstateStatsResponseDTO;
import com.example.ing.model.enumeration.RealEstateSize;
import com.example.ing.model.enumeration.RealEstateType;
import com.example.ing.model.enumeration.RegionId;
import com.example.ing.model.util.DateRange;
import com.example.ing.service.RealEstateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class RealEstateController {

    @Autowired
    private RealEstateServiceImpl realEstateService;

    @GetMapping(value="/real-estates-stats/{regionID}")
    public ResponseEntity<?> getRealEstates(
            @PathVariable("regionID") RegionId id,
            @RequestParam("size") RealEstateSize size,
            @RequestParam("rooms") Integer rooms,
            @RequestParam("types") Set<RealEstateType> types,
            @RequestParam("dateSince") String startDate,
            @RequestParam("dateUntil") String endDate
    ) {
        RealEstateStatsResponseDTO result;
        try {
            RealEstateStatsRequestDTO request = RealEstateStatsRequestDTO
                    .builder()
                    .regionId(id)
                    .size(size)
                    .rooms(rooms)
                    .types(types)
                    .dateRange(new DateRange(startDate, endDate))
                    .build();

            result = realEstateService.getRealEstatesAveragePrice(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

}
