package org.example.roamingaggregatorservice.dto;

import java.time.LocalDateTime;

public record ExceptionDTO(
        LocalDateTime timestamp,
        int status,
        String message
) {
}
