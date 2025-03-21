package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.example.roamingaggregatorservice.exceptions.StartDateIsAfterEndDateException;
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

/**
 * Сервис для работы с записями данных вызовов (CDR - Call Data Record).
 * Предоставляет методы для генерации, поиска и формирования отчетов по CDR.
 */
@Service
public class CdrService {

    private final SubscriberService subscriberService;
    private final CdrRepository cdrRepository;

    public CdrService(SubscriberService subscriberService, CdrRepository cdrRepository) {
        this.subscriberService = subscriberService;
        this.cdrRepository = cdrRepository;
    }

    /**
     * Генерирует случайные записи CDR за последний год.
     * Создает от 1000 до 2000 записей о звонках между абонентами.
     */
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

    /**
     * Находит все записи CDR, где абонент был вызываемой стороной.
     *
     * @param msisdn Номер телефона абонента
     * @return Список CDR, где абонент был вызываемой стороной
     */
    public List<Cdr> findAllByCalledNumber(String msisdn) {
        return cdrRepository.findAllByCalledNumber(msisdn);
    }

    /**
     * Находит все записи CDR, где абонент был вызывающей стороной.
     *
     * @param msisdn Номер телефона абонента
     * @return Список CDR, где абонент был вызывающей стороной
     */
    public List<Cdr> findAllByCallerNumber(String msisdn) {
        return cdrRepository.findAllByCallerNumber(msisdn);
    }

    /**
     * Находит все записи CDR, где абонент был вызываемой стороной за указанный месяц и год.
     *
     * @param msisdn Номер телефона абонента
     * @param year Год
     * @param month Месяц
     * @return Список CDR, где абонент был вызываемой стороной за указанный период
     */
    public List<Cdr> findAllByCalledNumberAndStartDateTimeLike(String msisdn, int year, int month) {
        return cdrRepository.findAllByCalledNumberAndStartDateTime(msisdn, year, month);
    }

    /**
     * Находит все записи CDR, где абонент был вызывающей стороной за указанный месяц и год.
     *
     * @param msisdn Номер телефона абонента
     * @param year Год
     * @param month Месяц
     * @return Список CDR, где абонент был вызывающей стороной за указанный период
     */
    public List<Cdr> findAllByCallerNumberAndStartDateTimeLike(String msisdn, int year, int month) {
        return cdrRepository.findAllByCallerNumberAndStartDateTime(msisdn, year, month);
    }

    /**
     * Генерирует отчет по звонкам абонента за указанный период.
     * Сохраняет отчет в файл в директории "reports".
     *
     * @param msisdn Номер телефона абонента
     * @param startDate Начальная дата периода
     * @param endDate Конечная дата периода
     * @param requestUUID Уникальный идентификатор запроса
     * @throws NoSuchSubscriberException если абонент с указанным номером не найден
     * @throws StartDateIsAfterEndDateException если начальная дата позже конечной даты
     * @throws RuntimeException при ошибках ввода-вывода
     */
    public void generateCdrReport(String msisdn, LocalDate startDate, LocalDate endDate, UUID requestUUID) {

        subscriberService.checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);

        if (startDate.isAfter(endDate)) throw new StartDateIsAfterEndDateException(startDate, endDate);

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
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(fileName);
    }
}
