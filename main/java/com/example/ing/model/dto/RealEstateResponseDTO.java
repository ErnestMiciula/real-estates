package com.example.ing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstateResponseDTO {
    private Integer totalPages;
    private List<RealEstateDTO> data;
}
