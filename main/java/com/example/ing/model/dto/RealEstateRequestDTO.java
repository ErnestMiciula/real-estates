package com.example.ing.model.dto;

import com.example.ing.model.enumeration.RegionId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstateRequestDTO {
    private RegionId regionId;
    private Integer pageNo;
}
