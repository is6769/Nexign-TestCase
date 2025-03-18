package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.dto.UdrDTO;
import org.springframework.stereotype.Service;

@Service
public class UdrService {

    private final CdrService cdrService;

    public UdrService(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    public UdrDTO generateUdrForSubscriberForMonth(String msisdn, String yearAndMonth){

        return null;
    }

    public UdrDTO generateUdrForSubscriberForAllTime(){
        return null;
    }

    public UdrDTO generateUdrForAllSubscribersForMonth(String yearAndMonth){
        return null;
    }
}
