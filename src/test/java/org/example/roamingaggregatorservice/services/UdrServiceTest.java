package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.dto.UdrDTO;
import org.example.roamingaggregatorservice.entities.Cdr;
import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса UdrService.
 * <p>
 * Данный класс содержит юнит-тесты для проверки функциональности сервиса UdrService.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class UdrServiceTest {

    @Mock
    private CdrService cdrService;

    @Mock
    private SubscriberService subscriberService;

    @InjectMocks
    private UdrService udrService;

    private final String msisdn = "79123456789";
    private final String yearAndMonth = "2023-05";
    private List<Cdr> incomingCalls;
    private List<Cdr> outgoingCalls;
    private List<Subscriber> subscribers;

    @BeforeEach
    void setUp() {
        Cdr incomingCall1 = new Cdr();
        incomingCall1.setCalledNumber(msisdn);
        incomingCall1.setCallerNumber("79876543210");
        incomingCall1.setStartDateTime(LocalDateTime.of(2023, 5, 10, 12, 0, 0));
        incomingCall1.setFinishDateTime(LocalDateTime.of(2023, 5, 10, 12, 15, 30));

        Cdr incomingCall2 = new Cdr();
        incomingCall2.setCalledNumber(msisdn);
        incomingCall2.setCallerNumber("79876543211");
        incomingCall2.setStartDateTime(LocalDateTime.of(2023, 5, 15, 14, 30, 0));
        incomingCall2.setFinishDateTime(LocalDateTime.of(2023, 5, 15, 14, 45, 0));

        incomingCalls = Arrays.asList(incomingCall1, incomingCall2);

        Cdr outgoingCall1 = new Cdr();
        outgoingCall1.setCallerNumber(msisdn);
        outgoingCall1.setCalledNumber("79876543210");
        outgoingCall1.setStartDateTime(LocalDateTime.of(2023, 5, 12, 10, 0, 0));
        outgoingCall1.setFinishDateTime(LocalDateTime.of(2023, 5, 12, 10, 10, 30));

        outgoingCalls = Collections.singletonList(outgoingCall1);

        Subscriber subscriber1 = new Subscriber();
        subscriber1.setId(1L);
        subscriber1.setMsisdn(msisdn);

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setId(2L);
        subscriber2.setMsisdn("79876543210");

        subscribers = Arrays.asList(subscriber1, subscriber2);
    }

    /**
     * Тест генерации UDR для абонента за указанный месяц.
     * <p>
     * Проверяет, что метод правильно вычисляет общее время входящих и исходящих звонков
     * для конкретного абонента за указанный месяц.
     * </p>
     */
    @Test
    void generateUdrForSubscriberForMonth_ShouldReturnCorrectData() {
        // Given
        doNothing().when(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        when(cdrService.findAllByCalledNumberAndStartDateTimeLike(msisdn, 2023, 5)).thenReturn(incomingCalls);
        when(cdrService.findAllByCallerNumberAndStartDateTimeLike(msisdn, 2023, 5)).thenReturn(outgoingCalls);

        // When
        UdrDTO result = udrService.generateUdrForSubscriberForMonth(msisdn, yearAndMonth);

        // Then
        assertNotNull(result);
        assertEquals(msisdn, result.msisdn());
        assertEquals("00:30:30", result.incomingCall().totalTime()); // 15:30 + 15:00 = 30:30
        assertEquals("00:10:30", result.outcomingCall().totalTime()); // 10:30

        verify(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        verify(cdrService).findAllByCalledNumberAndStartDateTimeLike(msisdn, 2023, 5);
        verify(cdrService).findAllByCallerNumberAndStartDateTimeLike(msisdn, 2023, 5);
    }

    /**
     * Тест генерации UDR для несуществующего абонента.
     * <p>
     * Проверяет, что метод выбрасывает исключение NoSuchSubscriberException
     * при попытке сгенерировать UDR для абонента, которого нет в системе.
     * </p>
     */
    @Test
    void generateUdrForSubscriberForMonth_WithNonExistentSubscriber_ShouldThrowException() {
        // Given
        doThrow(new NoSuchSubscriberException(msisdn))
                .when(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);

        // When + Then
        assertThrows(NoSuchSubscriberException.class, () -> {
            udrService.generateUdrForSubscriberForMonth(msisdn, yearAndMonth);
        });

        verify(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        verify(cdrService, never()).findAllByCalledNumberAndStartDateTimeLike(anyString(), anyInt(), anyInt());
        verify(cdrService, never()).findAllByCallerNumberAndStartDateTimeLike(anyString(), anyInt(), anyInt());
    }

    /**
     * Тест генерации UDR для абонента за все время.
     * <p>
     * Проверяет, что метод правильно вычисляет общее время входящих и исходящих звонков
     * для конкретного абонента за весь период.
     * </p>
     */
    @Test
    void generateUdrForSubscriberForAllTime_ShouldReturnCorrectData() {
        // Given
        doNothing().when(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        when(cdrService.findAllByCalledNumber(msisdn)).thenReturn(incomingCalls);
        when(cdrService.findAllByCallerNumber(msisdn)).thenReturn(outgoingCalls);

        // When
        UdrDTO result = udrService.generateUdrForSubscriberForAllTime(msisdn);

        // Then
        assertNotNull(result);
        assertEquals(msisdn, result.msisdn());
        assertEquals("00:30:30", result.incomingCall().totalTime());
        assertEquals("00:10:30", result.outcomingCall().totalTime());

        verify(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        verify(cdrService).findAllByCalledNumber(msisdn);
        verify(cdrService).findAllByCallerNumber(msisdn);
    }

    /**
     * Тест генерации UDR для всех абонентов за указанный месяц.
     * <p>
     * Проверяет, что метод правильно формирует список UDR для всех абонентов
     * в системе за указанный месяц.
     * </p>
     */
    @Test
    void generateUdrForAllSubscribersForMonth_ShouldReturnAllSubscribers() {
        // Given
        when(subscriberService.findAll()).thenReturn(subscribers);

        doNothing().when(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(anyString());
        
        for (Subscriber subscriber : subscribers) {
            String subscriberMsisdn = subscriber.getMsisdn();
            when(cdrService.findAllByCalledNumberAndStartDateTimeLike(eq(subscriberMsisdn), eq(2023), eq(5)))
                    .thenReturn(subscriberMsisdn.equals(msisdn) ? incomingCalls : Collections.emptyList());
            when(cdrService.findAllByCallerNumberAndStartDateTimeLike(eq(subscriberMsisdn), eq(2023), eq(5)))
                    .thenReturn(subscriberMsisdn.equals(msisdn) ? outgoingCalls : Collections.emptyList());
        }

        // When
        List<UdrDTO> results = udrService.generateUdrForAllSubscribersForMonth(yearAndMonth);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        

        UdrDTO firstUdr = results.stream()
                .filter(udr -> udr.msisdn().equals(msisdn))
                .findFirst()
                .orElseThrow();
        assertEquals("00:30:30", firstUdr.incomingCall().totalTime());
        assertEquals("00:10:30", firstUdr.outcomingCall().totalTime());

        UdrDTO secondUdr = results.stream()
                .filter(udr -> udr.msisdn().equals("79876543210"))
                .findFirst()
                .orElseThrow();
        assertEquals("00:00:00", secondUdr.incomingCall().totalTime());
        assertEquals("00:00:00", secondUdr.outcomingCall().totalTime());

        verify(subscriberService).findAll();
        verify(subscriberService, times(subscribers.size())).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(anyString());
    }

    /**
     * Тест генерации UDR для абонента без звонков.
     * <p>
     * Проверяет, что метод правильно формирует UDR с нулевой продолжительностью
     * для абонента, у которого не было звонков за указанный период.
     * </p>
     */
    @Test
    void generateUdrForSubscriberForMonth_WithNoCalls_ShouldReturnZeroDuration() {
        // Given
        doNothing().when(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        when(cdrService.findAllByCalledNumberAndStartDateTimeLike(msisdn, 2023, 5)).thenReturn(Collections.emptyList());
        when(cdrService.findAllByCallerNumberAndStartDateTimeLike(msisdn, 2023, 5)).thenReturn(Collections.emptyList());

        // When
        UdrDTO result = udrService.generateUdrForSubscriberForMonth(msisdn, yearAndMonth);

        // Then
        assertNotNull(result);
        assertEquals(msisdn, result.msisdn());
        assertEquals("00:00:00", result.incomingCall().totalTime());
        assertEquals("00:00:00", result.outcomingCall().totalTime());
    }
}
