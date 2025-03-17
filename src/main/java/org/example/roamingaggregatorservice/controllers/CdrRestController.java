package org.example.roamingaggregatorservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cdr")
public class CdrRestController {

    @PostMapping
    public ResponseEntity<String> generateCDR(){
        return ResponseEntity.ok("Successfully generated cdr.");
    }
}
