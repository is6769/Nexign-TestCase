package org.example.roamingaggregatorservice.repositories;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для репозитория CdrRepository.
 * <p>
 * Данный класс содержит тесты для проверки функциональности репозитория CdrRepository.
 * </p>
 */
@DataJpaTest
public class CdrRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CdrRepository cdrRepository;

    // Generate unique MSISDN values for each test run
    private final String callerMsisdn = "7910" + UUID.randomUUID().toString().substring(0, 8);
    private final String calledMsisdn = "7920" + UUID.randomUUID().toString().substring(0, 8);
    
    @BeforeEach
    void setUp() {
        cdrRepository.deleteAll();
        entityManager.flush();
        

        createAndPersistCdr("01", callerMsisdn, calledMsisdn, 
                LocalDateTime.of(2023, 1, 5, 10, 0, 0),
                LocalDateTime.of(2023, 1, 5, 10, 5, 30));
        

        createAndPersistCdr("01", callerMsisdn, calledMsisdn, 
                LocalDateTime.of(2023, 2, 10, 11, 0, 0),
                LocalDateTime.of(2023, 2, 10, 11, 10, 0));
        

        createAndPersistCdr("01", callerMsisdn, calledMsisdn, 
                LocalDateTime.of(2023, 3, 15, 12, 0, 0),
                LocalDateTime.of(2023, 3, 15, 12, 15, 0));
        

        createAndPersistCdr("02", calledMsisdn, callerMsisdn, 
                LocalDateTime.of(2023, 4, 20, 13, 0, 0),
                LocalDateTime.of(2023, 4, 20, 13, 7, 30));
        

        createAndPersistCdr("02", calledMsisdn, callerMsisdn, 
                LocalDateTime.of(2023, 5, 25, 14, 0, 0),
                LocalDateTime.of(2023, 5, 25, 14, 3, 45));
    }
    
    /**
     * Вспомогательный метод для создания и сохранения CDR записи.
     * <p>
     * Создает объект Cdr с указанными параметрами и сохраняет его в базу данных.
     * </p>
     * 
     * @param callType Тип вызова
     * @param callerNumber Номер вызывающего абонента
     * @param calledNumber Номер вызываемого абонента
     * @param startDateTime Время начала вызова
     * @param finishDateTime Время окончания вызова
     */
    private void createAndPersistCdr(String callType, String callerNumber, String calledNumber, 
                                    LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        Cdr cdr = new Cdr();
        cdr.setCallType(callType);
        cdr.setCallerNumber(callerNumber);
        cdr.setCalledNumber(calledNumber);
        cdr.setStartDateTime(startDateTime);
        cdr.setFinishDateTime(finishDateTime);
        entityManager.persist(cdr);
    }

    /**
     * Тест поиска всех CDR по номеру вызываемого абонента.
     * <p>
     * Проверяет, что метод findAllByCalledNumber корректно возвращает
     * все записи, где указанный номер является вызываемым.
     * </p>
     */
    @Test
    public void findAllByCalledNumber_ShouldReturnCorrectCdrs() {
        // When
        List<Cdr> results = cdrRepository.findAllByCalledNumber(calledMsisdn);

        // Then
        assertEquals(3, results.size());
        results.forEach(cdr -> assertEquals(calledMsisdn, cdr.getCalledNumber()));
    }

    /**
     * Тест поиска всех CDR по номеру вызывающего абонента.
     * <p>
     * Проверяет, что метод findAllByCallerNumber корректно возвращает
     * все записи, где указанный номер является вызывающим.
     * </p>
     */
    @Test
    public void findAllByCallerNumber_ShouldReturnCorrectCdrs() {
        // When
        List<Cdr> results = cdrRepository.findAllByCallerNumber(callerMsisdn);

        // Then
        assertEquals(3, results.size());
        results.forEach(cdr -> assertEquals(callerMsisdn, cdr.getCallerNumber()));
    }

    /**
     * Тест поиска всех CDR по номеру вызываемого абонента за указанный месяц и год.
     * <p>
     * Проверяет, что метод findAllByCalledNumberAndStartDateTime корректно
     * фильтрует записи по дате и возвращает только подходящие.
     * </п>
     */
    @Test
    public void findAllByCalledNumberAndStartDateTime_ShouldReturnCorrectCdrs() {
        // When
        List<Cdr> results = cdrRepository.findAllByCalledNumberAndStartDateTime(calledMsisdn, 2023, 2);

        // Then
        assertEquals(1, results.size());
        Cdr februaryCdr = results.get(0);
        assertEquals(calledMsisdn, februaryCdr.getCalledNumber());
        assertEquals(2023, februaryCdr.getStartDateTime().getYear());
        assertEquals(2, februaryCdr.getStartDateTime().getMonthValue());
    }

    /**
     * Тест поиска всех CDR по номеру вызывающего абонента за указанный месяц и год.
     * <p>
     * Проверяет, что метод findAllByCallerNumberAndStartDateTime корректно
     * фильтрует записи по дате и возвращает только подходящие.
     * </п>
     */
    @Test
    public void findAllByCallerNumberAndStartDateTime_ShouldReturnCorrectCdrs() {
        // When
        List<Cdr> results = cdrRepository.findAllByCallerNumberAndStartDateTime(callerMsisdn, 2023, 3);

        // Then
        assertEquals(1, results.size());
        Cdr marchCdr = results.get(0);
        assertEquals(callerMsisdn, marchCdr.getCallerNumber());
        assertEquals(2023, marchCdr.getStartDateTime().getYear());
        assertEquals(3, marchCdr.getStartDateTime().getMonthValue());
    }

    /**
     * Тест поиска всех CDR в указанном диапазоне дат.
     * <p>
     * Проверяет, что метод findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc
     * корректно фильтрует записи по диапазону дат и возвращает отсортированный результат.
     * </п>
     */
    @Test
    public void findAllByCalledNumberOrCalledNumberAndStartDateTimeBetween_ShouldReturnCorrectCdrs() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2023, 2, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 4, 30, 23, 59, 59);

        // When
        List<Cdr> results = cdrRepository.findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc(
                callerMsisdn, startDate, endDate);

        // Then
        assertEquals(3, results.size());
        

        assertEquals(2, results.get(0).getStartDateTime().getMonthValue());
        assertEquals(callerMsisdn, results.get(0).getCallerNumber());
        assertTrue(results.get(0).getStartDateTime().isBefore(results.get(1).getStartDateTime()));
    }

    /**
     * Тест пакетного сохранения CDR записей.
     * <p>
     * Проверяет, что метод saveAll корректно сохраняет несколько CDR записей
     * в базу данных за один вызов.
     * </п>
     */
    @Test
    public void saveAll_ShouldSaveMultipleCdrs() {
        // Given
        String uniqueCaller1 = "7930" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueCalled1 = "7940" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueCaller2 = "7950" + UUID.randomUUID().toString().substring(0, 8);
        String uniqueCalled2 = "7960" + UUID.randomUUID().toString().substring(0, 8);
        
        Cdr cdr1 = new Cdr();
        cdr1.setCallType("01");
        cdr1.setCallerNumber(uniqueCaller1);
        cdr1.setCalledNumber(uniqueCalled1);
        cdr1.setStartDateTime(LocalDateTime.of(2023, 6, 1, 10, 0, 0));
        cdr1.setFinishDateTime(LocalDateTime.of(2023, 6, 1, 10, 10, 0));
        
        Cdr cdr2 = new Cdr();
        cdr2.setCallType("02");
        cdr2.setCallerNumber(uniqueCaller2);
        cdr2.setCalledNumber(uniqueCalled2);
        cdr2.setStartDateTime(LocalDateTime.of(2023, 6, 2, 11, 0, 0));
        cdr2.setFinishDateTime(LocalDateTime.of(2023, 6, 2, 11, 15, 0));
        
        List<Cdr> cdrsToSave = List.of(cdr1, cdr2);

        // When
        List<Cdr> savedCdrs = cdrRepository.saveAll(cdrsToSave);

        // Then
        assertEquals(2, savedCdrs.size());
        savedCdrs.forEach(cdr -> assertNotNull(cdr.getId()));

        List<Cdr> allCdrs = cdrRepository.findAll();
        assertEquals(7, allCdrs.size());
    }
}
