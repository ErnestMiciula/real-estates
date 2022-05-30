package com.example.ing.service;

import com.example.ing.model.dto.RealEstateStatsRequestDTO;
import com.example.ing.model.dto.RealEstateStatsResponseDTO;

import javax.mail.MessagingException;
import java.io.IOException;

public interface RealEstateService {

    /**
     * The method periodically requests information about real estate sales.
     * The response comes from third party services and must be save in the database.
     * <p>
     * If there is an error/exception, then request must be send again, up to N times.
     * If there is still an error/exception then email must be send to the admin.
    * */
    void getRealEstateSalesData() throws MessagingException, IOException;

    /**
     * Produce average price for real estates (based on given criteria)
     * */
    RealEstateStatsResponseDTO getRealEstatesAveragePrice(RealEstateStatsRequestDTO request);

}
