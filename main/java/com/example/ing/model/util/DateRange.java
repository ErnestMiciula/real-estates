package com.example.ing.model.util;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public final class DateRange {
    private LocalDate start;
    private LocalDate end;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public DateRange(String startDateString, String endDateString) throws IllegalArgumentException{
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDateString, formatter);
            end = LocalDate.parse(endDateString, formatter);
        } catch(DateTimeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException(
                    "Start date must be after end date. Start date: " + start + " . End date: " + end
            );
        }

        if (start == null && end != null) {
            this.start = end;
            this.end = end;
        } else if (start != null && end == null) {
            this.start = start;
            this.end = start;
        } else {
            this.start = start;
            this.end = end;
        }
    }
}
