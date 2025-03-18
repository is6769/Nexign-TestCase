package org.example.roamingaggregatorservice.dto;

public record UdrForOneUserDTO(
        String msisdn,
        CallDataDTO incomingCall,
        CallDataDTO outcomingCall
) implements UdrDTO{
}
