package org.example.roamingaggregatorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Информация об ошибке")
public record ExceptionDTO(
        @Schema(description = "Время возникновения ошибки", example = "2023-10-15T14:30:15")
        LocalDateTime timestamp,
        
        @Schema(description = "HTTP статус код", example = "400")
        int status,
        
        @Schema(description = "Тип ошибки", example = "VALIDATION_ERROR", 
               allowableValues = {"VALIDATION_ERROR", "NOT_FOUND", "SERVER_ERROR", "BAD_REQUEST"})
        String errorType,
        
        @Schema(description = "Сообщение об ошибке", example = "Неверный формат номера телефона")
        String message
) {

}
