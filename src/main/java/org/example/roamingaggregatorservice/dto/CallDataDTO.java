package org.example.roamingaggregatorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для представления информации о звонке.
 * Содержит данные об общем времени звонка.
 *
 * @param totalTime Общее время звонка в формате ЧЧ:ММ:СС
 */
@Schema(description = "Информация о звонке")
public record CallDataDTO(
        @Schema(description = "Общее время звонка в формате ЧЧ:ММ:СС", example = "00:05:30")
        String totalTime
) {

}
