package com.example.ing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealEstateDTO {
    private String id;
    private String type;
    private String price;
    private String description;
    private String area;
    private Integer rooms;
}
