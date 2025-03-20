package org.example.roamingaggregatorservice.controllers;

import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.example.roamingaggregatorservice.exceptions.StartDateIsAfterEndDateException;
import org.example.roamingaggregatorservice.services.CdrService;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CdrRestController.class)
public class CdrRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CdrService cdrService;

    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;

    @Test
    public void generateCDR_ShouldReturnSuccessMessage() throws Exception {
        // Given
        doNothing().when(cdrService).generateCdrForOneYear();

        // When
        mockMvc.perform(post("/v1/cdr")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Успешно сгенерированы cdr-записи."));

        // Then
        verify(cdrService, times(1)).generateCdrForOneYear();
    }

    @Test
    public void generateCdrReport_WithValidParameters_ShouldReturnSuccessMessage() throws Exception {
        // Given
        String msisdn = "79123456789";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31);
        
        doNothing().when(cdrService).generateCdrReport(eq(msisdn), eq(startDate), eq(endDate), any(UUID.class));

        // When
        mockMvc.perform(post("/v1/cdr/report")
                        .param("msisdn", msisdn)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Успешно сформирован cdr-отчет")));

        // Then
        verify(cdrService, times(1)).generateCdrReport(
                eq(msisdn), 
                eq(startDate), 
                eq(endDate), 
                any(UUID.class));
    }

    @Test
    public void generateCdrReport_WithInvalidDateRange_ShouldReturnBadRequest() throws Exception {
        // Given
        String msisdn = "79123456789";
        LocalDate startDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31); // End date before start date
        
        doThrow(new StartDateIsAfterEndDateException(startDate, endDate))
                .when(cdrService).generateCdrReport(eq(msisdn), eq(startDate), eq(endDate), any(UUID.class));

        // When + Then
        mockMvc.perform(post("/v1/cdr/report")
                        .param("msisdn", msisdn)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("BAD_REQUEST")));
    }

    @Test
    public void generateCdrReport_WithNonExistentSubscriber_ShouldReturnBadRequest() throws Exception {
        // Given
        String msisdn = "79999999999"; // Non-existent subscriber
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31);
        
        doThrow(new NoSuchSubscriberException(msisdn))
                .when(cdrService).generateCdrReport(eq(msisdn), eq(startDate), eq(endDate), any(UUID.class));

        // When + Then
        mockMvc.perform(post("/v1/cdr/report")
                        .param("msisdn", msisdn)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Абонент с номером")))
                .andExpect(content().string(containsString("не найден")));
    }

    @Test
    public void generateCdrReport_ShouldGenerateValidUUID() throws Exception {
        // Given
        String msisdn = "79123456789";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31);
        
        doNothing().when(cdrService).generateCdrReport(
                eq(msisdn), 
                eq(startDate), 
                eq(endDate), 
                uuidCaptor.capture());

        // When
        mockMvc.perform(post("/v1/cdr/report")
                        .param("msisdn", msisdn)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Then
        UUID capturedUUID = uuidCaptor.getValue();
        verify(cdrService, times(1)).generateCdrReport(
                eq(msisdn), 
                eq(startDate), 
                eq(endDate), 
                eq(capturedUUID));

        UUID parsedUUID = UUID.fromString(capturedUUID.toString());
        assertEquals(capturedUUID, parsedUUID);
    }
}
