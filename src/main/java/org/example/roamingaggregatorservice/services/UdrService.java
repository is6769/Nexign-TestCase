package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.dto.CallDataDTO;
import org.example.roamingaggregatorservice.dto.UdrDTO;
import org.example.roamingaggregatorservice.dto.UdrForOneUserDTO;
import org.example.roamingaggregatorservice.entities.Cdr;
import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class UdrService {

    private final CdrService cdrService;
    private final SubscriberService subscriberService;


    public UdrService(CdrService cdrService, SubscriberService subscriberService) {
        this.cdrService = cdrService;
        this.subscriberService = subscriberService;
    }

    public UdrDTO generateUdrForSubscriberForMonth(String msisdn, String yearAndMonth){

        subscriberService.checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);

        int year = Integer.valueOf(yearAndMonth.split("-")[0]);
        int month = Integer.valueOf(yearAndMonth.split("-")[1]);

        List<Cdr> cdrs = cdrService.findAllByCalledNumberAndStartDateTimeLike(msisdn, year, month);
        String totalTimeOfIncomingCalls =  calculateTotalTimeOfCalls(cdrs);

        cdrs = cdrService.findAllByCallerNumberAndStartDateTimeLike(msisdn, year, month);
        String totalTimeOfOutcomingCalls =  calculateTotalTimeOfCalls(cdrs);

        return new UdrForOneUserDTO(msisdn, new CallDataDTO(totalTimeOfIncomingCalls), new CallDataDTO(totalTimeOfOutcomingCalls));
    }

    public UdrDTO generateUdrForSubscriberForAllTime(String msisdn){

        subscriberService.checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);

        List<Cdr> cdrs = cdrService.findAllByCalledNumber(msisdn);
        String totalTimeOfIncomingCalls =  calculateTotalTimeOfCalls(cdrs);

        cdrs = cdrService.findAllByCallerNumber(msisdn);
        String totalTimeOfOutcomingCalls =  calculateTotalTimeOfCalls(cdrs);

        return new UdrForOneUserDTO(msisdn, new CallDataDTO(totalTimeOfIncomingCalls), new CallDataDTO(totalTimeOfOutcomingCalls));
    }

    public List<UdrDTO> generateUdrForAllSubscribersForMonth(String yearAndMonth){

        List<Subscriber> subscribers = subscriberService.findAll();

        List<UdrDTO> udrDTOList = new ArrayList<>();
        subscribers.forEach( subscriber -> {
            udrDTOList.add(generateUdrForSubscriberForMonth(subscriber.getMsisdn(),yearAndMonth));
        });
        return udrDTOList;
    }

    private String calculateTotalTimeOfCalls(List<Cdr> cdrs) {

        Duration totalDuration = Duration.ofSeconds(0);

        for (Cdr cdr: cdrs){
            Duration duration = Duration.between(cdr.getStartDateTime(),cdr.getFinishDateTime());
            totalDuration = totalDuration.plus(duration);
        }

        return String.format("%02d:%02d:%02d", totalDuration.toHours(), totalDuration.toMinutesPart(), totalDuration.toSecondsPart());
    }


}
