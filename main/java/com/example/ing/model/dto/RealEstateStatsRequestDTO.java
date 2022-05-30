package com.example.ing.model.dto;

import com.example.ing.model.enumeration.RealEstateSize;
import com.example.ing.model.enumeration.RealEstateType;
import com.example.ing.model.enumeration.RegionId;
import com.example.ing.model.util.DateRange;
import lombok.*;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstateStatsRequestDTO {
    private RegionId regionId;
    private Integer rooms;
    private RealEstateSize size;
    private Set<RealEstateType> types;
    private DateRange dateRange;
}
