package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.dto.CallDataDTO;
import org.example.roamingaggregatorservice.dto.UdrDTO;
import org.example.roamingaggregatorservice.entities.Cdr;
import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для генерации и управления записями данных пользователя (UDR - User Data Record).
 * Предоставляет методы для создания UDR на основе CDR (Call Data Record) для абонентов.
 */
@Service
public class UdrService {

    private final CdrService cdrService;
    private final SubscriberService subscriberService;


    public UdrService(CdrService cdrService, SubscriberService subscriberService) {
        this.cdrService = cdrService;
        this.subscriberService = subscriberService;
    }

    /**
     * Генерирует UDR для указанного абонента за конкретный месяц.
     *
     * @param msisdn Номер телефона абонента
     * @param yearAndMonth Год и месяц в формате "YYYY-MM"
     * @return UdrDTO содержащий информацию о входящих и исходящих вызовах
     * @throws NoSuchSubscriberException если абонент с указанным номером не найден
     */
    public UdrDTO generateUdrForSubscriberForMonth(String msisdn, String yearAndMonth){

        subscriberService.checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);

        int year = Integer.valueOf(yearAndMonth.split("-")[0]);
        int month = Integer.valueOf(yearAndMonth.split("-")[1]);

        List<Cdr> cdrs = cdrService.findAllByCalledNumberAndStartDateTimeLike(msisdn, year, month);
        String totalTimeOfIncomingCalls =  calculateTotalTimeOfCalls(cdrs);

        cdrs = cdrService.findAllByCallerNumberAndStartDateTimeLike(msisdn, year, month);
        String totalTimeOfOutcomingCalls =  calculateTotalTimeOfCalls(cdrs);

        return new UdrDTO(msisdn, new CallDataDTO(totalTimeOfIncomingCalls), new CallDataDTO(totalTimeOfOutcomingCalls));
    }

    /**
     * Генерирует UDR для указанного абонента за всё время.
     *
     * @param msisdn Номер телефона абонента
     * @return UdrDTO содержащий информацию о входящих и исходящих вызовах
     * @throws NoSuchSubscriberException если абонент с указанным номером не найден
     */
    public UdrDTO generateUdrForSubscriberForAllTime(String msisdn){

        subscriberService.checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);

        List<Cdr> cdrs = cdrService.findAllByCalledNumber(msisdn);
        String totalTimeOfIncomingCalls =  calculateTotalTimeOfCalls(cdrs);

        cdrs = cdrService.findAllByCallerNumber(msisdn);
        String totalTimeOfOutcomingCalls =  calculateTotalTimeOfCalls(cdrs);

        return new UdrDTO(msisdn, new CallDataDTO(totalTimeOfIncomingCalls), new CallDataDTO(totalTimeOfOutcomingCalls));
    }

    /**
     * Генерирует UDR для всех абонентов за указанный месяц.
     *
     * @param yearAndMonth Год и месяц в формате "YYYY-MM"
     * @return Список UdrDTO для всех абонентов
     */
    public List<UdrDTO> generateUdrForAllSubscribersForMonth(String yearAndMonth){

        List<Subscriber> subscribers = subscriberService.findAll();

        List<UdrDTO> udrDTOList = new ArrayList<>();
        subscribers.forEach( subscriber -> {
            udrDTOList.add(generateUdrForSubscriberForMonth(subscriber.getMsisdn(),yearAndMonth));
        });
        return udrDTOList;
    }

    /**
     * Вычисляет общее время вызовов из списка CDR.
     *
     * @param cdrs Список записей данных вызовов
     * @return Строка с общим временем в формате "HH:MM:SS"
     */
    private String calculateTotalTimeOfCalls(List<Cdr> cdrs) {

        Duration totalDuration = Duration.ofSeconds(0);

        for (Cdr cdr: cdrs){
            Duration duration = Duration.between(cdr.getStartDateTime(),cdr.getFinishDateTime());
            totalDuration = totalDuration.plus(duration);
        }

        return String.format("%02d:%02d:%02d", totalDuration.toHours(), totalDuration.toMinutesPart(), totalDuration.toSecondsPart());
    }


}
