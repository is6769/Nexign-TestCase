package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.example.roamingaggregatorservice.exceptions.StartDateIsAfterEndDateException;
import org.example.roamingaggregatorservice.repositories.CdrRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса CdrService.
 * <p>
 * Данный класс содержит юнит-тесты для проверки функциональности сервиса CdrService.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class CdrServiceTest {

    @Mock
    private SubscriberService subscriberService;

    @Mock
    private CdrRepository cdrRepository;

    @InjectMocks
    private CdrService cdrService;

    @Captor
    private ArgumentCaptor<List<Cdr>> cdrListCaptor;

    @TempDir
    Path tempDir;

    private String msisdn;
    private List<Subscriber> subscribers;
    private List<Cdr> cdrs;

    @BeforeEach
    void setUp() {
        msisdn = "79123456789";
        
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setId(1L);
        subscriber1.setMsisdn(msisdn);

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setId(2L);
        subscriber2.setMsisdn("79876543210");

        subscribers = Arrays.asList(subscriber1, subscriber2);

        Cdr cdr1 = new Cdr();
        cdr1.setId(1L);
        cdr1.setCallType("01");
        cdr1.setCallerNumber("79876543210");
        cdr1.setCalledNumber(msisdn);
        cdr1.setStartDateTime(LocalDateTime.of(2023, 5, 10, 12, 0, 0));
        cdr1.setFinishDateTime(LocalDateTime.of(2023, 5, 10, 12, 15, 30));

        Cdr cdr2 = new Cdr();
        cdr2.setId(2L);
        cdr2.setCallType("02");
        cdr2.setCallerNumber(msisdn);
        cdr2.setCalledNumber("79876543210");
        cdr2.setStartDateTime(LocalDateTime.of(2023, 5, 12, 10, 0, 0));
        cdr2.setFinishDateTime(LocalDateTime.of(2023, 5, 12, 10, 10, 30));

        cdrs = Arrays.asList(cdr1, cdr2);
        

        System.setProperty("user.dir", tempDir.toString());
    }

    /**
     * Тест генерации CDR за один год.
     * <p>
     * Проверяет, что метод правильно генерирует CDR записи для всех абонентов
     * за период одного года и сохраняет их в репозиторий.
     * </p>
     */
    @Test
    void generateCdrForOneYear_ShouldGenerateAndSaveCdrs() {
        // Given
        when(subscriberService.findAll()).thenReturn(subscribers);
        when(cdrRepository.saveAll(anyList())).thenReturn(null);

        // When
        cdrService.generateCdrForOneYear();

        // Then
        verify(subscriberService).findAll();
        verify(cdrRepository).saveAll(cdrListCaptor.capture());
        

        List<Cdr> savedCdrs = cdrListCaptor.getValue();
        assertNotNull(savedCdrs);

        assertTrue(savedCdrs.size() >= 1000 && savedCdrs.size() <= 2000);

        LocalDateTime previousStartTime = null;
        for (Cdr cdr : savedCdrs) {
            if (previousStartTime != null) {
                assertTrue(cdr.getStartDateTime().compareTo(previousStartTime) >= 0);
            }
            previousStartTime = cdr.getStartDateTime();

            assertNotNull(cdr.getCallType());
            assertNotNull(cdr.getCallerNumber());
            assertNotNull(cdr.getCalledNumber());
            assertNotNull(cdr.getStartDateTime());
            assertNotNull(cdr.getFinishDateTime());
            assertTrue(cdr.getStartDateTime().isBefore(cdr.getFinishDateTime()));
        }
    }

    /**
     * Тест поиска всех CDR по номеру вызываемого абонента.
     * <p>
     * Проверяет, что метод корректно запрашивает и возвращает список CDR,
     * где указанный номер является вызываемым.
     * </p>
     */
    @Test
    void findAllByCalledNumber_ShouldReturnCorrectCdrs() {
        // Given
        when(cdrRepository.findAllByCalledNumber(msisdn)).thenReturn(cdrs.subList(0, 1));

        // When
        List<Cdr> result = cdrService.findAllByCalledNumber(msisdn);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(msisdn, result.get(0).getCalledNumber());
        
        verify(cdrRepository).findAllByCalledNumber(msisdn);
    }

    /**
     * Тест поиска всех CDR по номеру вызывающего абонента.
     * <p>
     * Проверяет, что метод корректно запрашивает и возвращает список CDR,
     * где указанный номер является вызывающим.
     * </p>
     */
    @Test
    void findAllByCallerNumber_ShouldReturnCorrectCdrs() {
        // Given
        when(cdrRepository.findAllByCallerNumber(msisdn)).thenReturn(cdrs.subList(1, 2));

        // When
        List<Cdr> result = cdrService.findAllByCallerNumber(msisdn);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(msisdn, result.get(0).getCallerNumber());
        
        verify(cdrRepository).findAllByCallerNumber(msisdn);
    }

    /**
     * Тест поиска всех CDR по номеру вызываемого абонента за указанный месяц и год.
     * <p>
     * Проверяет, что метод корректно фильтрует CDR записи по дате и
     * возвращает только те, которые соответствуют указанному месяцу и году.
     * </p>
     */
    @Test
    void findAllByCalledNumberAndStartDateTimeLike_ShouldReturnCorrectCdrs() {
        // Given
        when(cdrRepository.findAllByCalledNumberAndStartDateTime(msisdn, 2023, 5)).thenReturn(cdrs.subList(0, 1));

        // When
        List<Cdr> result = cdrService.findAllByCalledNumberAndStartDateTimeLike(msisdn, 2023, 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(msisdn, result.get(0).getCalledNumber());
        assertEquals(5, result.get(0).getStartDateTime().getMonthValue());
        assertEquals(2023, result.get(0).getStartDateTime().getYear());
        
        verify(cdrRepository).findAllByCalledNumberAndStartDateTime(msisdn, 2023, 5);
    }

    /**
     * Тест поиска всех CDR по номеру вызывающего абонента за указанный месяц и год.
     * <p>
     * Проверяет, что метод корректно фильтрует CDR записи по дате и
     * возвращает только те, которые соответствуют указанному месяцу и году.
     * </p>
     */
    @Test
    void findAllByCallerNumberAndStartDateTimeLike_ShouldReturnCorrectCdrs() {
        // Given
        when(cdrRepository.findAllByCallerNumberAndStartDateTime(msisdn, 2023, 5)).thenReturn(cdrs.subList(1, 2));

        // When
        List<Cdr> result = cdrService.findAllByCallerNumberAndStartDateTimeLike(msisdn, 2023, 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(msisdn, result.get(0).getCallerNumber());
        assertEquals(5, result.get(0).getStartDateTime().getMonthValue());
        assertEquals(2023, result.get(0).getStartDateTime().getYear());
        
        verify(cdrRepository).findAllByCallerNumberAndStartDateTime(msisdn, 2023, 5);
    }

    /**
     * Тест генерации отчета CDR с корректными параметрами.
     * <p>
     * Проверяет, что метод правильно создает файл отчета с
     * информацией о вызовах для указанного абонента за заданный период.
     * </p>
     */
    @Test
    void generateCdrReport_WithValidParameters_ShouldCreateReport() throws IOException {
        // Given
        LocalDate startDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 5, 31);
        UUID requestUUID = UUID.randomUUID();
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        doNothing().when(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        when(cdrRepository.findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                msisdn, startDateTime, endDateTime)).thenReturn(cdrs);

        // When
        cdrService.generateCdrReport(msisdn, startDate, endDate, requestUUID);

        // Then
        verify(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        verify(cdrRepository).findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                msisdn, startDateTime, endDateTime);
        

        Path reportsDir = tempDir.resolve("reports");
        Path reportFile = reportsDir.resolve(msisdn + "_" + requestUUID.toString() + ".txt");
        assertTrue(Files.exists(reportFile));

        List<String> fileLines = Files.readAllLines(reportFile);
        assertEquals(cdrs.size()*2, fileLines.size());

        String expectedLine1 = String.format("%s,%s,%s,%s,%s",
                cdrs.get(0).getCallType(),
                cdrs.get(0).getCallerNumber(),
                cdrs.get(0).getCalledNumber(),
                cdrs.get(0).getStartDateTime(),
                cdrs.get(0).getFinishDateTime());
        assertEquals(expectedLine1, fileLines.get(0));
    }

    /**
     * Тест генерации отчета CDR для несуществующего абонента.
     * <p>
     * Проверяет, что метод выбрасывает исключение NoSuchSubscriberException
     * при попытке сгенерировать отчет для несуществующего абонента.
     * </p>
     */
    @Test
    void generateCdrReport_WithNonExistentSubscriber_ShouldThrowException() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 5, 31);
        UUID requestUUID = UUID.randomUUID();
        
        doThrow(new NoSuchSubscriberException(msisdn))
                .when(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);

        // When + Then
        assertThrows(NoSuchSubscriberException.class, () -> {
            cdrService.generateCdrReport(msisdn, startDate, endDate, requestUUID);
        });
        
        verify(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        verify(cdrRepository, never()).findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                anyString(), any(), any());
    }

    /**
     * Тест генерации отчета CDR с неверным диапазоном дат.
     * <p>
     * Проверяет, что метод выбрасывает исключение StartDateIsAfterEndDateException,
     * если начальная дата позже конечной.
     * </p>
     */
    @Test
    void generateCdrReport_WithInvalidDateRange_ShouldThrowException() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 5, 31);
        LocalDate endDate = LocalDate.of(2023, 5, 1); // End date before start date
        UUID requestUUID = UUID.randomUUID();
        
        doNothing().when(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);

        // When
        assertThrows(StartDateIsAfterEndDateException.class, () -> {
            cdrService.generateCdrReport(msisdn, startDate, endDate, requestUUID);
        });

        // Then
        verify(subscriberService).checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(msisdn);
        verify(cdrRepository, never()).findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                anyString(), any(), any());
    }
}
