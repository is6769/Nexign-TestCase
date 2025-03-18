package org.example.roamingaggregatorservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Информация о звонке")
public record CallDataDTO(
        @Schema(description = "Общее время звонка в формате ЧЧ:ММ:СС", example = "00:05:30")
        String totalTime
) {

}
