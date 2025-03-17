package org.example.roamingaggregatorservice.controllers;

import org.example.roamingaggregatorservice.services.CdrGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cdr")
public class CdrRestController {

    private final CdrGeneratorService cdrGeneratorService;

    public CdrRestController(CdrGeneratorService cdrGeneratorService) {
        this.cdrGeneratorService = cdrGeneratorService;
    }

    @PostMapping
    public ResponseEntity<String> generateCDR(){
        cdrGeneratorService.generateCdrForOneYear();
        return ResponseEntity.ok("Successfully generated cdr.");
    }
}
