package org.example.roamingaggregatorservice.controllers;

import org.example.roamingaggregatorservice.services.CdrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cdr")
public class CdrRestController {

    private final CdrService cdrService;

    public CdrRestController(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    @PostMapping
    public ResponseEntity<String> generateCDR(){
        cdrService.generateCdrForOneYear();
        return ResponseEntity.ok("Successfully generated cdr.");
    }
}
