package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.dto.CallDataDTO;
import org.example.roamingaggregatorservice.dto.UdrDTO;
import org.example.roamingaggregatorservice.dto.UdrForOneUserDTO;
import org.example.roamingaggregatorservice.entities.Cdr;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UdrService {

    private final CdrService cdrService;

    public UdrService(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    public UdrDTO generateUdrForSubscriberForMonth(String msisdn, String yearAndMonth){
        return null;
    }

    public UdrDTO generateUdrForSubscriberForAllTime(String msisdn){
        List<Cdr> cdrs = cdrService.findAllByCalledNumber(msisdn);
        String totalTimeOfIncomingCalls =  calculateTotalTimeOfCalls(cdrs);

        cdrs = cdrService.findAllByCallerNumber(msisdn);
        String totalTimeOfOutcomingCalls =  calculateTotalTimeOfCalls(cdrs);

        return new UdrForOneUserDTO(msisdn, new CallDataDTO(totalTimeOfIncomingCalls), new CallDataDTO(totalTimeOfOutcomingCalls));
    }

    private String calculateTotalTimeOfCalls(List<Cdr> cdrs) {
        Duration totalDuration = Duration.ofSeconds(0);

        for (Cdr cdr: cdrs){
            Duration duration = Duration.between(cdr.getStartDateTime(),cdr.getFinishDateTime());
            totalDuration = totalDuration.plus(duration);
        }

        return String.format("%02d:%02d:%02d", totalDuration.toHours(), totalDuration.toMinutesPart(), totalDuration.toSecondsPart());
    }

    public UdrDTO generateUdrForAllSubscribersForMonth(String yearAndMonth){
        return null;
    }
}
