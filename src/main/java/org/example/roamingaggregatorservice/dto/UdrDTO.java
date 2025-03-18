package org.example.roamingaggregatorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Объект записи данных пользователя (User Data Record)")
public record UdrDTO(
        @Schema(description = "Номер мобильного телефона абонента", example = "79123456789")
        String msisdn,
        
        @Schema(description = "Данные о входящем звонке")
        CallDataDTO incomingCall,
        
        @Schema(description = "Данные о исходящем звонке")
        CallDataDTO outcomingCall
) {
}