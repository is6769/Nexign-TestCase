package org.example.roamingaggregatorservice.exceptions;

import java.time.LocalDate;

public class StartDateIsAfterEndDateException extends RuntimeException {

    public StartDateIsAfterEndDateException() {
        super("Дата начала периода должна быть раньше даты окончания.");
    }

    public StartDateIsAfterEndDateException(LocalDate startDate, LocalDate endDate) {
        super("Дата начала периода: %s должна быть раньше даты окончания: %s".formatted(startDate,endDate));
    }
}
