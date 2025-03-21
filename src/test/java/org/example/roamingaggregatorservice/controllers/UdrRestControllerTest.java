package org.example.roamingaggregatorservice.controllers;

import org.example.roamingaggregatorservice.dto.CallDataDTO;
import org.example.roamingaggregatorservice.dto.UdrDTO;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.example.roamingaggregatorservice.services.UdrService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для REST-контроллера UdrRestController.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(UdrRestController.class)
public class UdrRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UdrService udrService;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    /**
     * Тест получения UDR для абонента за все время.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос без указания
     * параметра yearAndMonth и возвращает данные за все время.
     * </p>
     */
    @Test
    public void getUdrForSubscriber_WithoutYearAndMonth_ShouldReturnAllTimeData() throws Exception {
        // Given
        String msisdn = "79123456789";
        UdrDTO mockUdrDTO = new UdrDTO(
                msisdn,
                new CallDataDTO("01:30:45"),
                new CallDataDTO("02:15:30")
        );
        
        when(udrService.generateUdrForSubscriberForAllTime(eq(msisdn)))
                .thenReturn(mockUdrDTO);

        // When & Then
        mockMvc.perform(get("/v1/udr")
                        .param("msisdn", msisdn)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is(msisdn)))
                .andExpect(jsonPath("$.incomingCall.totalTime", is("01:30:45")))
                .andExpect(jsonPath("$.outcomingCall.totalTime", is("02:15:30")));

        // Verify service was called with correct parameters
        verify(udrService, times(1)).generateUdrForSubscriberForAllTime(eq(msisdn));
        verify(udrService, times(0)).generateUdrForSubscriberForMonth(any(), any());
    }

    /**
     * Тест получения UDR для абонента за указанный месяц.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос с указанием
     * параметра yearAndMonth и возвращает данные только за этот месяц.
     * </п>
     */
    @Test
    public void getUdrForSubscriber_WithYearAndMonth_ShouldReturnMonthData() throws Exception {
        // Given
        String msisdn = "79123456789";
        String yearAndMonth = "2023-05";
        UdrDTO mockUdrDTO = new UdrDTO(
                msisdn,
                new CallDataDTO("00:45:15"),
                new CallDataDTO("01:10:30")
        );
        
        when(udrService.generateUdrForSubscriberForMonth(eq(msisdn), eq(yearAndMonth)))
                .thenReturn(mockUdrDTO);

        // When 
        mockMvc.perform(get("/v1/udr")
                        .param("msisdn", msisdn)
                        .param("yearAndMonth", yearAndMonth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn", is(msisdn)))
                .andExpect(jsonPath("$.incomingCall.totalTime", is("00:45:15")))
                .andExpect(jsonPath("$.outcomingCall.totalTime", is("01:10:30")));

        // Then
        verify(udrService, times(0)).generateUdrForSubscriberForAllTime(any());
        verify(udrService, times(1)).generateUdrForSubscriberForMonth(eq(msisdn), eq(yearAndMonth));
    }

    /**
     * Тест получения UDR с некорректным форматом месяца и года.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос с неверным
     * форматом параметра yearAndMonth и возвращает ошибку валидации.
     * </п>
     */
    @Test
    public void getUdrForSubscriber_WithInvalidYearAndMonth_ShouldReturnBadRequest() throws Exception {
        // Given
        String msisdn = "79123456789";
        String invalidYearAndMonth = "2023/05"; // Using wrong format

        // When & Then
        mockMvc.perform(get("/v1/udr")
                        .param("msisdn", msisdn)
                        .param("yearAndMonth", invalidYearAndMonth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("VALIDATION_ERROR")));

        // Verify no service methods were called
        verify(udrService, times(0)).generateUdrForSubscriberForAllTime(any());
        verify(udrService, times(0)).generateUdrForSubscriberForMonth(any(), any());
    }

    /**
     * Тест получения UDR для несуществующего абонента.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос для несуществующего
     * абонента и возвращает соответствующую ошибку.
     * </п>
     */
    @Test
    public void getUdrForSubscriber_WithNonExistentSubscriber_ShouldReturnBadRequest() throws Exception {
        // Given
        String msisdn = "79999999999"; // Non-existent subscriber
        
        when(udrService.generateUdrForSubscriberForAllTime(eq(msisdn)))
                .thenThrow(new NoSuchSubscriberException(msisdn));

        // When & Then
        mockMvc.perform(get("/v1/udr")
                        .param("msisdn", msisdn)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", containsString(msisdn)));

        // Verify service was called with correct parameter
        verify(udrService, times(1)).generateUdrForSubscriberForAllTime(eq(msisdn));
    }

    /**
     * Тест получения UDR для всех абонентов за указанный месяц.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос на получение
     * данных для всех абонентов и возвращает список результатов.
     * </п>
     */
    @Test
    public void getUdrForAllSubscribersForOneMonth_WithValidYearAndMonth_ShouldReturnData() throws Exception {
        // Given
        String yearAndMonth = "2023-05";
        List<UdrDTO> mockUdrDTOs = Arrays.asList(
                new UdrDTO("79123456789", new CallDataDTO("01:30:45"), new CallDataDTO("02:15:30")),
                new UdrDTO("79123456790", new CallDataDTO("00:45:15"), new CallDataDTO("01:10:30"))
        );
        
        when(udrService.generateUdrForAllSubscribersForMonth(eq(yearAndMonth)))
                .thenReturn(mockUdrDTOs);

        // When & Then
        mockMvc.perform(get("/v1/udr/all")
                        .param("yearAndMonth", yearAndMonth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].msisdn", is("79123456789")))
                .andExpect(jsonPath("$[1].msisdn", is("79123456790")));

        // Verify service was called with correct parameter
        verify(udrService, times(1)).generateUdrForAllSubscribersForMonth(eq(yearAndMonth));
    }

    /**
     * Тест получения UDR для всех абонентов с пустым результатом.
     * <p>
     * Проверяет, что метод корректно обрабатывает случай, когда
     * результат запроса - пустой список, и возвращает пустой массив.
     * </п>
     */
    @Test
    public void getUdrForAllSubscribersForOneMonth_WithEmptyResult_ShouldReturnEmptyArray() throws Exception {
        // Given
        String yearAndMonth = "2023-05";
        
        when(udrService.generateUdrForAllSubscribersForMonth(eq(yearAndMonth)))
                .thenReturn(new ArrayList<>());

        // When
        mockMvc.perform(get("/v1/udr/all")
                        .param("yearAndMonth", yearAndMonth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // Then
        verify(udrService, times(1)).generateUdrForAllSubscribersForMonth(eq(yearAndMonth));
    }

    /**
     * Тест получения UDR для всех абонентов с некорректным форматом месяца и года.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос с неверным
     * форматом параметра yearAndMonth и возвращает ошибку валидации.
     * </п>
     */
    @Test
    public void getUdrForAllSubscribersForOneMonth_WithInvalidYearAndMonth_ShouldReturnBadRequest() throws Exception {
        // Given
        String invalidYearAndMonth = "05-2023"; // Wrong format

        // When
        mockMvc.perform(get("/v1/udr/all")
                        .param("yearAndMonth", invalidYearAndMonth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorType", is("VALIDATION_ERROR")));

        // Then
        verify(udrService, times(0)).generateUdrForAllSubscribersForMonth(any());
    }

    /**
     * Тест получения UDR для всех абонентов без указания обязательного параметра.
     * <p>
     * Проверяет, что метод корректно обрабатывает запрос без обязательного
     * параметра yearAndMonth и возвращает ошибку 400 Bad Request.
     * </п>
     */
    @Test
    public void getUdrForAllSubscribersForOneMonth_WithMissingYearAndMonth_ShouldReturnBadRequest() throws Exception {
        // When
        mockMvc.perform(get("/v1/udr/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // Then
        verify(udrService, times(0)).generateUdrForAllSubscribersForMonth(any());
    }
}
