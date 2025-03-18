package org.example.roamingaggregatorservice.controllers;

import jakarta.validation.constraints.Pattern;
import org.example.roamingaggregatorservice.dto.CallDataDTO;
import org.example.roamingaggregatorservice.dto.UdrDTO;
import org.example.roamingaggregatorservice.dto.UdrForOneUserDTO;
import org.example.roamingaggregatorservice.services.CdrService;
import org.example.roamingaggregatorservice.services.UdrService;


import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Objects;

@RestController
@RequestMapping("/v1/udr")
@Validated
public class UdrRestController {

    private final UdrService udrService;

    public UdrRestController(UdrService udrService) {
        this.udrService = udrService;
    }

    @GetMapping
    public ResponseEntity<UdrDTO> getUdrForSubscriber(
            @RequestParam String msisdn,
            @RequestParam(required = false) @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Provided year and month don't match format yyyy-mm") String yearAndMonth
    ) {
        if (Objects.nonNull(yearAndMonth)){
            UdrDTO dto = udrService.generateUdrForSubscriberForMonth(msisdn, yearAndMonth);
            ResponseEntity.ok(dto);
        } else {
            UdrDTO dto = udrService.generateUdrForSubscriberForAllTime();
            ResponseEntity.ok(dto);
        }
        return null;
    }

    @GetMapping("/all")
    public ResponseEntity<UdrDTO> getUdrForAllSubscribersForOneMonth(
            @RequestParam String yearAndMonth
    ){
        UdrDTO dto = udrService.generateUdrForAllSubscribersForMonth(yearAndMonth);
        return ResponseEntity.ok(dto);
    }
}
