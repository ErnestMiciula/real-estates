package com.example.ing.dao;

import com.example.ing.model.dto.RealEstateStatsRequestDTO;
import com.example.ing.model.entity.RealEstate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RealEstateRepo extends JpaRepository<RealEstate, Long> {
   @Query("SELECT AVG(re.price) " +
           "FROM RealEstate re " +
           "WHERE (re.regionId = :#{#realEstate.regionId} OR re.regionId IS NULL) " +
           "AND (re.size = :#{#realEstate.size} OR re.regionId IS NULL) " +
           "AND (re.type = :#{#realEstate.types} OR re.regionId IS NULL) " +
           "AND (re.date BETWEEN :#{#realEstate.dateRange.start} AND :#{#realEstate.dateRange.end})" +
           "AND re.type IN :#{#realEstate.types}")
   Double findRealEstateAveragePrice(@Param("realEstate") RealEstateStatsRequestDTO realEstate);
}
