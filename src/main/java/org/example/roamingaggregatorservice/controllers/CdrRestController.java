package org.example.roamingaggregatorservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/v1/cdr")
@Tag(name = "CDR", description = "Операции с записями данных вызовов (Call Data Records)")
public class CdrRestController {

    private final CdrService cdrService;

    public CdrRestController(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    @PostMapping
    @Operation(
            summary = "Сгенерировать CDR",
            description = "Генерирует записи данных вызовов (CDR) за один год"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CDR успешно сгенерированы", 
                    content = @Content(schema = @Schema(type = "string", example = "Successfully generated cdr.")))
    })
    public ResponseEntity<String> generateCDR(){
        cdrService.generateCdrForOneYear();
        return ResponseEntity.ok("Successfully generated cdr.");
    }

    @PostMapping("/report")
    public ResponseEntity<String> generateCdrReport(
            @RequestParam String msisdn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        UUID requestUUID = UUID.randomUUID();

        //CompletableFuture.runAsync(()->cdrService.generateCdrReport(msisdn, startDate, endDate, requestUUID));

        cdrService.generateCdrReport(msisdn, startDate, endDate, requestUUID);

        return ResponseEntity.ok("Successfully started generating cdr-report.UUID: %s".formatted(requestUUID));
    }
}
