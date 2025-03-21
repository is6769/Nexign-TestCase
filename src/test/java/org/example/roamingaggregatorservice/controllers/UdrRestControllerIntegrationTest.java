package org.example.roamingaggregatorservice.controllers;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.repositories.CdrRepository;
import org.example.roamingaggregatorservice.repositories.SubscriberRepository;
import org.example.roamingaggregatorservice.services.CdrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты REST-контроллера UdrRestController
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class UdrRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private CdrRepository cdrRepository;

    @Autowired
    private CdrService cdrService;

    private final String CALLER_MSISDN = "79123456789";
    private final String CALLED_MSISDN = "79876543210";
    private final String NONEXISTENT_MSISDN = "70000000000";
    private final String YEAR_AND_MONTH = "2023-05";
    private final String INVALID_YEAR_AND_MONTH = "05/2023"; // Неверный формат
    
    private Subscriber callerSubscriber;
    private Subscriber calledSubscriber;

    @BeforeEach
    void setUp() {
        cdrRepository.deleteAll();
        subscriberRepository.deleteAll();

        callerSubscriber = new Subscriber();
        callerSubscriber.setMsisdn(CALLER_MSISDN);
        subscriberRepository.save(callerSubscriber);
        
        calledSubscriber = new Subscriber();
        calledSubscriber.setMsisdn(CALLED_MSISDN);
        subscriberRepository.save(calledSubscriber);
        

        List<Cdr> testCdrs = new ArrayList<>();

        testCdrs.add(createCdr("01", CALLED_MSISDN, CALLER_MSISDN,
                LocalDateTime.of(2023, 5, 10, 12, 0, 0),
                LocalDateTime.of(2023, 5, 10, 12, 15, 30)));
        
        testCdrs.add(createCdr("01", CALLED_MSISDN, CALLER_MSISDN,
                LocalDateTime.of(2023, 5, 15, 18, 0, 0),
                LocalDateTime.of(2023, 5, 15, 18, 10, 15)));
        

        testCdrs.add(createCdr("02", CALLER_MSISDN, CALLED_MSISDN,
                LocalDateTime.of(2023, 5, 20, 9, 0, 0),
                LocalDateTime.of(2023, 5, 20, 9, 5, 45)));
        

        testCdrs.add(createCdr("01", CALLED_MSISDN, CALLER_MSISDN,
                LocalDateTime.of(2023, 6, 5, 14, 0, 0),
                LocalDateTime.of(2023, 6, 5, 14, 8, 20)));
        

        testCdrs.add(createCdr("02", CALLER_MSISDN, CALLED_MSISDN,
                LocalDateTime.of(2023, 6, 10, 15, 0, 0),
                LocalDateTime.of(2023, 6, 10, 15, 12, 10)));
        

        cdrRepository.saveAll(testCdrs);
    }
    
    /**
     * Вспомогательный метод для создания CDR записи.
     *
     * @param callType Тип вызова (01 - входящий, 02 - исходящий)
     * @param callerNumber Номер вызывающего абонента
     * @param calledNumber Номер вызываемого абонента
     * @param startDateTime Время начала звонка
     * @param finishDateTime Время окончания звонка
     * @return Созданная CDR запись
     */
    private Cdr createCdr(String callType, String callerNumber, String calledNumber,
                         LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        Cdr cdr = new Cdr();
        cdr.setCallType(callType);
        cdr.setCallerNumber(callerNumber);
        cdr.setCalledNumber(calledNumber);
        cdr.setStartDateTime(startDateTime);
        cdr.setFinishDateTime(finishDateTime);
        return cdr;
    }

    /**
     * Тест получения UDR для абонента за все время.
     * <p>
     * Проверяет, что контроллер правильно обрабатывает запрос без указания
     * параметра yearAndMonth и возвращает данные о вызовах за все время.
     * </p>
     */
    @Test
    void getUdrForSubscriber_WithoutYearAndMonth_ShouldReturnAllTimeData() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/udr")
                .param("msisdn", CALLER_MSISDN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is(CALLER_MSISDN)))
                .andExpect(jsonPath("$.incomingCall.totalTime", is("00:34:05"))) // 15:30 + 10:15 + 8:20
                .andExpect(jsonPath("$.outcomingCall.totalTime", is("00:17:55"))); // 5:45 + 12:10
    }

    /**
     * Тест получения UDR для абонента за конкретный месяц.
     * <p>
     * Проверяет, что контроллер правильно обрабатывает запрос с указанием
     * параметра yearAndMonth и возвращает данные только за этот месяц.
     * </p>
     */
    @Test
    void getUdrForSubscriber_WithYearAndMonth_ShouldReturnMonthData() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/udr")
                .param("msisdn", CALLER_MSISDN)
                .param("yearAndMonth", YEAR_AND_MONTH)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is(CALLER_MSISDN)))
                .andExpect(jsonPath("$.incomingCall.totalTime", is("00:25:45"))) // 15:30 + 10:15
                .andExpect(jsonPath("$.outcomingCall.totalTime", is("00:05:45"))); // 5:45
    }

    /**
     * Тест получения UDR для несуществующего абонента.
     * <p>
     * Проверяет, что контроллер правильно обрабатывает запрос для несуществующего
     * абонента и возвращает соответствующую ошибку.
     * </p>
     */
    @Test
    void getUdrForSubscriber_WithNonExistentMsisdn_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/udr")
                .param("msisdn", NONEXISTENT_MSISDN)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", containsString("не найден")));
    }

    /**
     * Тест получения UDR с неверным форматом месяца и года.
     * <p>
     * Проверяет, что контроллер корректно обрабатывает запрос с неверным
     * форматом параметра yearAndMonth и возвращает ошибку валидации.
     * </p>
     */
    @Test
    void getUdrForSubscriber_WithInvalidYearAndMonth_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/udr")
                .param("msisdn", CALLER_MSISDN)
                .param("yearAndMonth", INVALID_YEAR_AND_MONTH)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", containsString("формату yyyy-mm")));
    }

    /**
     * Тест получения UDR для всех абонентов за указанный месяц.
     * <p>
     * Проверяет, что контроллер правильно обрабатывает запрос на получение
     * данных для всех абонентов и возвращает список результатов.
     * </p>
     */
    @Test
    void getUdrForAllSubscribersForOneMonth_WithValidYearAndMonth_ShouldReturnData() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/udr/all")
                .param("yearAndMonth", YEAR_AND_MONTH)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].msisdn", oneOf(CALLER_MSISDN, CALLED_MSISDN)))
                .andExpect(jsonPath("$[1].msisdn", oneOf(CALLER_MSISDN, CALLED_MSISDN)));
                

        mockMvc.perform(get("/v1/udr/all")
                .param("yearAndMonth", YEAR_AND_MONTH)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.msisdn == '" + CALLER_MSISDN + "')].incomingCall.totalTime").value("00:25:45"))
                .andExpect(jsonPath("$[?(@.msisdn == '" + CALLER_MSISDN + "')].outcomingCall.totalTime").value("00:05:45"));
    }

    /**
     * Тест получения UDR для всех абонентов с неверным форматом месяца и года.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос с неверным
     * форматом параметра yearAndMonth и возвращает ошибку валидации.
     * </p>
     */
    @Test
    void getUdrForAllSubscribersForOneMonth_WithInvalidYearAndMonth_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/udr/all")
                .param("yearAndMonth", INVALID_YEAR_AND_MONTH)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("VALIDATION_ERROR")))
                .andExpect(jsonPath("$.message", containsString("формату yyyy-mm")));
    }

    /**
     * Тест получения UDR для всех абонентов без указания обязательного параметра.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос без обязательного
     * параметра yearAndMonth и возвращает ошибку 400 Bad Request.
     * </p>
     */
    @Test
    void getUdrForAllSubscribersForOneMonth_WithMissingYearAndMonth_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/udr/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Тест получения UDR для абонента без звонков.
     * <p>
     * Проверяет, что метод правильно обрабатывает случай, когда у абонента
     * нет звонков за указанный период, и возвращает нулевую продолжительность.
     * </p>
     */
    @Test
    void getUdrForSubscriber_WithNoCallsInPeriod_ShouldReturnZeroDuration() throws Exception {
        // Given
        String emptyMonth = "2022-01"; // Месяц, в котором нет звонков
        
        // When & Then
        mockMvc.perform(get("/v1/udr")
                .param("msisdn", CALLER_MSISDN)
                .param("yearAndMonth", emptyMonth)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is(CALLER_MSISDN)))
                .andExpect(jsonPath("$.incomingCall.totalTime", is("00:00:00")))
                .andExpect(jsonPath("$.outcomingCall.totalTime", is("00:00:00")));
    }
}
