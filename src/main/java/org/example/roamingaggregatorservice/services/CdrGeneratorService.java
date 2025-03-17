package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.repositories.CdrRepository;
import org.example.roamingaggregatorservice.repositories.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CdrGeneratorService {

    private final SubscriberRepository subscriberRepository;
    private final CdrRepository cdrRepository;

    public CdrGeneratorService(SubscriberRepository subscriberRepository, CdrRepository cdrRepository) {
        this.subscriberRepository = subscriberRepository;
        this.cdrRepository = cdrRepository;
    }

    public void generateCdrForOneYear(){
        List<Subscriber> subscribers = subscriberRepository.findAll();

        List<Cdr> generatedCdrs = new ArrayList<>();

        LocalDateTime startDateTime = LocalDateTime.now().minusYears(1);
        LocalDateTime endDateTime = LocalDateTime.now();

        long startMillis = startDateTime.atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli();
        long endMillis = endDateTime.atZone(ZoneId.of("Europe/Moscow")).toInstant().toEpochMilli();

        int totalNumberOfCalls = ThreadLocalRandom.current().nextInt(1000,2001);

        for (int i = 0; i < totalNumberOfCalls; i++) {

            Cdr generatedCdr = new Cdr();


            String callType = (ThreadLocalRandom.current().nextBoolean()) ? "01" : "02";


            int randomCallerIndex = ThreadLocalRandom.current().nextInt(subscribers.size());

            int randomCalledIndex;
            do {
                randomCalledIndex = ThreadLocalRandom.current().nextInt(subscribers.size());
            }while (randomCalledIndex == randomCallerIndex);

            Subscriber caller = subscribers.get(randomCallerIndex);
            Subscriber called = subscribers.get(randomCalledIndex);


            long durationMillis = ThreadLocalRandom.current().nextLong(1,5*60*60*1000);

            long callStartMillis = ThreadLocalRandom.current().nextLong(startMillis,endMillis-durationMillis);
            long callFinishMillis = callStartMillis + durationMillis;

            var callStartDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(callStartMillis),ZoneId.of("Europe/Moscow"));
            var callFinishDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(callFinishMillis),ZoneId.of("Europe/Moscow"));


            generatedCdr.setCallType(callType);
            generatedCdr.setCallerNumber(caller.getPhoneNumber());
            generatedCdr.setCalledNumber(called.getPhoneNumber());
            generatedCdr.setStartDateTime(callStartDateTime);
            generatedCdr.setFinishDateTime(callFinishDateTime);

            generatedCdrs.add(generatedCdr);
        }

        generatedCdrs.sort(Comparator.comparing(Cdr::getStartDateTime));

        cdrRepository.saveAll(generatedCdrs);
    }

}
