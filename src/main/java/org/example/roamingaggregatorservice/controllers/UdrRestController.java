package org.example.roamingaggregatorservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.example.roamingaggregatorservice.dto.CallDataDTO;
import org.example.roamingaggregatorservice.dto.ExceptionDTO;
import org.example.roamingaggregatorservice.dto.UdrDTO;
import org.example.roamingaggregatorservice.services.CdrService;
import org.example.roamingaggregatorservice.services.UdrService;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/udr")
@Validated
@Tag(name = "UDR", description = "Операции с записями данных пользователя (User Data Records)")
public class UdrRestController {

    private final UdrService udrService;

    public UdrRestController(UdrService udrService) {
        this.udrService = udrService;
    }

    @GetMapping
    @Operation(
            summary = "Получить UDR для абонента",
            description = "Возвращает записи данных для конкретного абонента за все время или за указанный месяц"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ", 
                    content = @Content(schema = @Schema(implementation = UdrDTO.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации: неправильный формат параметров запроса (например, неверный формат года и месяца)",
                    content = @Content(schema = @Schema(implementation = ExceptionDTO.class)))
    })
    public ResponseEntity<UdrDTO> getUdrForSubscriber(
            @Parameter(description = "Номер мобильного телефона абонента") @RequestParam String msisdn,
            @Parameter(description = "Год и месяц в формате yyyy-mm (например, 2023-05)") @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Предоставленный год и месяц не соответствуют формату yyyy-mm") String yearAndMonth
    ) {
        UdrDTO dto;
        if (Objects.nonNull(yearAndMonth)){
            dto = udrService.generateUdrForSubscriberForMonth(msisdn, yearAndMonth);
        } else {
            dto = udrService.generateUdrForSubscriberForAllTime(msisdn);
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Получить UDR для всех абонентов за месяц",
            description = "Возвращает записи данных для всех абонентов за указанный месяц"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный ответ",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации: неправильный формат года и месяца",
                    content = @Content(schema = @Schema(implementation = ExceptionDTO.class)))
    })
    public ResponseEntity<List<UdrDTO>> getUdrForAllSubscribersForOneMonth(
            @Parameter(description = "Год и месяц в формате yyyy-mm (например, 2023-05)") @RequestParam String yearAndMonth
    ){
        List<UdrDTO> dtos = udrService.generateUdrForAllSubscribersForMonth(yearAndMonth);
        return ResponseEntity.ok(dtos);
    }
}
