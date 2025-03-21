package org.example.roamingaggregatorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для представления записи данных пользователя (UDR).
 * Содержит информацию о номере абонента и данные о входящих и исходящих звонках.
 *
 * @param msisdn Номер мобильного телефона абонента
 * @param incomingCall Данные о входящих звонках
 * @param outcomingCall Данные о исходящих звонках
 */
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