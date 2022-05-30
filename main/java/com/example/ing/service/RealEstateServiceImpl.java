package com.example.ing.service;

import com.example.ing.dao.RealEstateRepo;
import com.example.ing.model.dto.*;
import com.example.ing.model.entity.RealEstate;
import com.example.ing.model.enumeration.RealEstateSize;
import com.example.ing.model.enumeration.RealEstateType;
import com.example.ing.model.enumeration.RegionId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
@Slf4j
@EnableScheduling
public class RealEstateServiceImpl implements RealEstateService {

    @Autowired
    private RealEstateRepo repo;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.mail.username}") private String sender;
    @Value("${spring.mail.properties.recipient}") private String recipient;
    @Value("${spring.mail.properties.subject}") private String subject;

    private Integer NUMBER_OF_CALLS_AFTER_FAILURE = 3;
    private static final Integer FIRST_PAGE = 1;

    @Override
    public RealEstateStatsResponseDTO getRealEstatesAveragePrice(RealEstateStatsRequestDTO request) {
        log.info("Requesting data...");
        Double averagePrice = repo.findRealEstateAveragePrice(request);
        return new RealEstateStatsResponseDTO(String.valueOf(averagePrice));
    }

    @Override
    @Scheduled(cron = "0 0 21 * * ?")
    public void getRealEstateSalesData() {
        for (RegionId regionId : RegionId.values()) {
            saveRealEstateSales(regionId);
        }
    }

    private void saveRealEstateSales(RegionId regionId) {
        //request initial records (first page)
        String url = "https://third-party-source.com/api/real-estates/"+ regionId +"?page=" + FIRST_PAGE;
        log.info("Requesting data...");
        ResponseEntity <RealEstateResponseDTO> response =
                restTemplate.getForEntity(url, RealEstateResponseDTO.class, regionId, FIRST_PAGE);

        if(response.getStatusCode() == HttpStatus.OK) {
            if (response.getBody() == null || response.getBody().getTotalPages() == null) {
                return;
            }

            int pages = response.getBody().getTotalPages();
           //start iterating from first page, and then backwards till first page (excluded)
            while (pages > 1){
                //trigger if there is no more records on the page
                if (response.getBody() == null || response.getBody().getData().isEmpty()) {
                    break;
                }
                //iterate real estates inside current page
                for (RealEstateDTO realEstate : response.getBody().getData()) {
                    try {
                        log.info("Saving data...");
                        repo.save(RealEstate
                                .builder()
                                .regionId(regionId)
                                .date(LocalDate.now())
                                .price(Double.valueOf(realEstate.getPrice()))
                                .type(RealEstateType.valueOf(realEstate.getType()))
                                .size(getSize(realEstate.getArea()))
                                .rooms(realEstate.getRooms())
                                .build());
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid record, type: " + realEstate.getType().toUpperCase() +
                                " ; area: " + realEstate.getArea() + " ; rooms: " +
                                realEstate.getRooms() + " ; price" + realEstate.getPrice(), e);
                    }
                }

                response = restTemplate.getForEntity(url, RealEstateResponseDTO.class, regionId, pages--);
            }
        } else {
            // If there is any issues with third party API then call this method N tiems recursively
            if (NUMBER_OF_CALLS_AFTER_FAILURE-- > 0) {
                saveRealEstateSales(regionId);
            } else {
                return;
            }

            NUMBER_OF_CALLS_AFTER_FAILURE = 3;

            sendmail("Cannot retrieve data for " + regionId.name());
        }
    }

    private void sendmail(String msg) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(recipient);
            mailMessage.setText(msg);
            mailMessage.setSubject(subject);

            javaMailSender.send(mailMessage);
            log.info("Mail Sent Successfully!");
        } catch (MailException e) {
            log.warn("Error while Sending Mail");
        }
    }

    private RealEstateSize getSize(String area) throws IllegalArgumentException{
        double size;
        try {
            size = Double.parseDouble(area);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid size: " + area);
        }

        RealEstateSize result = null;
        if (size >= 18 && size <= 45) result = RealEstateSize.S;
        else if (size >= 46 && size <= 80) result = RealEstateSize.M;
        else if (size >= 81 && size <= 400) result = RealEstateSize.L;

        if (result == null) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }

        return result;
    }
}
