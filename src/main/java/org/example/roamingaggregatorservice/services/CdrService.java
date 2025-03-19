package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.repositories.CdrRepository;
import org.example.roamingaggregatorservice.repositories.SubscriberRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CdrService {

    private final SubscriberService subscriberService;
    private final CdrRepository cdrRepository;

    public CdrService(SubscriberService subscriberService, CdrRepository cdrRepository) {
        this.subscriberService = subscriberService;
        this.cdrRepository = cdrRepository;
    }

    public void generateCdrForOneYear(){
        List<Subscriber> subscribers = subscriberService.findAll();

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

            long callStartMillis = ThreadLocalRandom.current().nextLong(startMillis,endMillis-durationMillis);//endMillis-durationMillis to make [l;r) maybe redo
            long callFinishMillis = callStartMillis + durationMillis;

            var callStartDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(callStartMillis),ZoneId.of("Europe/Moscow"));
            var callFinishDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(callFinishMillis),ZoneId.of("Europe/Moscow"));

            //TODO choose to add or not the inverse side of call(i mean if someone called someone, that means that someone was called by someone)

            generatedCdr.setCallType(callType);
            generatedCdr.setCallerNumber(caller.getMsisdn());
            generatedCdr.setCalledNumber(called.getMsisdn());
            generatedCdr.setStartDateTime(callStartDateTime);
            generatedCdr.setFinishDateTime(callFinishDateTime);

            generatedCdrs.add(generatedCdr);
        }

        generatedCdrs.sort(Comparator.comparing(Cdr::getStartDateTime));

        cdrRepository.saveAll(generatedCdrs);
    }

    public List<Cdr> findAllByCalledNumber(String msisdn) {
        return cdrRepository.findAllByCalledNumber(msisdn);
    }

    public List<Cdr> findAllByCallerNumber(String msisdn) {
        return cdrRepository.findAllByCallerNumber(msisdn);
    }

    public List<Cdr> findAllByCalledNumberAndStartDateTimeLike(String msisdn, int year, int month) {
        return cdrRepository.findAllByCalledNumberAndStartDateTime(msisdn, year, month);
    }

    public List<Cdr> findAllByCallerNumberAndStartDateTimeLike(String msisdn, int year, int month) {
        return cdrRepository.findAllByCallerNumberAndStartDateTime(msisdn, year, month);
    }

    public void generateCdrReport(String msisdn, LocalDate startDate, LocalDate endDate, UUID requestUUID) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Cdr> cdrs = cdrRepository.findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc(msisdn, startDateTime, endDateTime);

        String fileName = msisdn + "_%s.txt".formatted(requestUUID.toString());
        Path reportsPath = Paths.get(System.getProperty("user.dir"), "reports");
        if (!Files.exists(reportsPath)) {
            try {
                Files.createDirectories(reportsPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path reportFilePath = reportsPath.resolve(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(reportFilePath, StandardCharsets.UTF_8)){
            for (Cdr cdr: cdrs){
                writer.write(String.format("%s,%s,%s,%s,%s",
                        cdr.getCallType(),
                        cdr.getCallerNumber(),
                        cdr.getCalledNumber(),
                        cdr.getStartDateTime(),
                        cdr.getFinishDateTime()
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(fileName);
    }
}
