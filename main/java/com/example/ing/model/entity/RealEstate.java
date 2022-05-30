package com.example.ing.model.entity;

import com.example.ing.model.enumeration.RealEstateSize;
import com.example.ing.model.enumeration.RealEstateType;
import com.example.ing.model.enumeration.RegionId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "real_estate")
public class RealEstate {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "region_id")
    private RegionId regionId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "price")
    private Double price;

    @Column(name = "type")
    private RealEstateType type;

    @Column(name = "size")
    private RealEstateSize size;

    @Column(name = "rooms")
    private Integer rooms;
}
