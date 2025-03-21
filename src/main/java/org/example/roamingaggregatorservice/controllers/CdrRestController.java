package org.example.roamingaggregatorservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.roamingaggregatorservice.dto.ExceptionDTO;
import org.example.roamingaggregatorservice.services.CdrService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * REST-контроллер для работы с записями данных вызовов (CDR - Call Data Records).
 * <p>
 * Контроллер предоставляет API для генерации записей данных вызовов
 * за один год и для формирования отчетов по звонкам для конкретного
 * абонента за указанный период времени.
 * </p>
 * 
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
@RestController
@RequestMapping("/v1/cdr")
@Tag(name = "CDR", description = "Операции с записями данных вызовов (Call Data Records)")
public class CdrRestController {

    private final CdrService cdrService;

    /**
     * Конструктор контроллера CDR.
     *
     * @param cdrService Сервис для работы с CDR данными
     */
    public CdrRestController(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    /**
     * Генерация записей данных вызовов за один год.
     * <p>
     * Метод инициирует процесс генерации случайных записей о звонках
     * для всех абонентов системы за период одного года. Используется
     * для заполнения базы данных тестовыми данными.
     * </p>
     *
     * @return Объект ResponseEntity с информацией об успешной генерации
     */
    @PostMapping
    @Operation(
            summary = "Сгенерировать CDR",
            description = "Генерирует записи данных вызовов (CDR) за один год"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CDR успешно сгенерированы", 
                    content = @Content(schema = @Schema(type = "string", example = "Успешно сгенерированы cdr-записи.")))
    })
    public ResponseEntity<String> generateCDR(){
        cdrService.generateCdrForOneYear();
        return ResponseEntity.ok("Успешно сгенерированы cdr-записи.");
    }

    /**
     * Генерация отчета по записям вызовов для конкретного абонента.
     * <p>
     * Метод инициирует процесс формирования отчета о
     * входящих и исходящих звонках для указанного абонента за
     * определенный период времени. Отчет сохраняется в файловой системе
     * и может быть идентифицирован по возвращаемому UUID.
     * </p>
     *
     * @param msisdn Номер мобильного телефона абонента
     * @param startDate Дата начала периода для формирования отчета
     * @param endDate Дата окончания периода для формирования отчета
     * @return Объект ResponseEntity с информацией об успешном запуске формирования отчета и его UUID
     */
    @PostMapping("/report")
    @Operation(
            summary = "Сгенерировать отчет по CDR",
            description = "Генерирует отчет по записям данных вызовов (CDR) для указанного MSISDN в заданном временном диапазоне"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Процесс генерации отчета успешно запущен",
                    content = @Content(schema = @Schema(type = "string", example = "Успешно сформирован cdr-отчет. UUID: a718ae8f-edf4-4c00-88d5-9d53eea95178"))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации: неправильный формат параметров запроса (например, неверный формат года и месяца)",
                    content = @Content(schema = @Schema(implementation = ExceptionDTO.class)))
    })
    public ResponseEntity<String> generateCdrReport(
            @Parameter(description = "Номер абонента (MSISDN)", required = true, example = "79999999999")
            @RequestParam String msisdn,

            @Parameter(description = "Дата начала периода", required = true, example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Дата окончания периода", required = true, example = "2024-03-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        UUID requestUUID = UUID.randomUUID();

        cdrService.generateCdrReport(msisdn, startDate, endDate, requestUUID);

        return ResponseEntity.ok("Успешно сформирован cdr-отчет. UUID: %s".formatted(requestUUID));
    }
}
