package org.example.roamingaggregatorservice.controllers;

import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.repositories.SubscriberRepository;
import org.example.roamingaggregatorservice.services.CdrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для REST-контроллера CdrRestController..
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class CdrRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CdrService cdrService;

    @Autowired
    private SubscriberRepository subscriberRepository;

    private final String TEST_MSISDN = "79123456789";
    private final String SECOND_TEST_MSISDN = "79234567890";
    private final String INVALID_MSISDN = "79000000000";
    private final Pattern UUID_PATTERN = Pattern.compile("UUID: ([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})");
    
    @BeforeEach
    void setUp() {
        subscriberRepository.deleteAll();

        Subscriber testSubscriber = new Subscriber();
        testSubscriber.setMsisdn(TEST_MSISDN);
        subscriberRepository.save(testSubscriber);
        
        Subscriber secondTestSubscriber = new Subscriber();
        secondTestSubscriber.setMsisdn(SECOND_TEST_MSISDN);
        subscriberRepository.save(secondTestSubscriber);

        Path reportsDir = Paths.get(System.getProperty("user.dir"), "reports");
        if (!Files.exists(reportsDir)) {
            try {
                Files.createDirectories(reportsDir);
            } catch (Exception e) {
                System.err.println("Не удалось создать директорию для отчетов: " + e.getMessage());
            }
        }
    }

    /**
     * Тест успешной генерации CDR записей.
     * <p>
     * Проверяет, что контроллер правильно обрабатывает запрос на генерацию CDR
     * и возвращает соответствующее сообщение об успехе.
     * </p>
     */
    @Test
    void generateCDR_ShouldReturnSuccessMessage() throws Exception {
        // Given + When + Then
        mockMvc.perform(post("/v1/cdr")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Успешно сгенерированы cdr-записи."));
    }

    /**
     * Тест успешной генерации отчета CDR.
     * <p>
     * Проверяет, что контроллер правильно обрабатывает запрос на генерацию отчета CDR
     * с правильными параметрами и возвращает сообщение об успехе с UUID отчета.
     * </п>
     */
    @Test
    void generateCdrReport_WithValidParameters_ShouldReturnSuccessMessage() throws Exception {
        // Gven
        cdrService.generateCdrForOneYear();
        

        String msisdn = TEST_MSISDN;
        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();

        // When

        MvcResult result = mockMvc.perform(post("/v1/cdr/report")
                .param("msisdn", msisdn)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Успешно сформирован cdr-отчет. UUID:")))
                .andReturn();
                
        // Then
        String responseContent = result.getResponse().getContentAsString();
        Matcher matcher = UUID_PATTERN.matcher(responseContent);
        assertTrue(matcher.find(), "UUID должен присутствовать в ответе");
        
        String uuidStr = matcher.group(1);

        Path reportFilePath = Paths.get(System.getProperty("user.dir"), "reports", msisdn + "_" + uuidStr + ".txt");
        assertTrue(Files.exists(reportFilePath), "Файл отчета должен быть создан");
    }

    /**
     * Тест генерации отчета CDR с неверным диапазоном дат.
     * <p>
     * Проверяет, что контроллер корректно обрабатывает случай, когда 
     * начальная дата больше конечной даты, и возвращает соответствующую ошибку.
     * </п>
     */
    @Test
    void generateCdrReport_WithInvalidDateRange_ShouldReturnBadRequest() throws Exception {
        // Given
        String msisdn = TEST_MSISDN;
        LocalDate startDate = LocalDate.now().plusMonths(1);
        LocalDate endDate = LocalDate.now().minusMonths(1);
        
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

    /**
     * Тест генерации отчета CDR для несуществующего абонента.
     * <p>
     * Проверяет, что контроллер корректно обрабатывает случай, когда 
     * запрашивается отчет для несуществующего абонента, и возвращает соответствующую ошибку.
     * </п>
     */
    @Test
    void generateCdrReport_WithNonExistentSubscriber_ShouldReturnBadRequest() throws Exception {
        // Given
        String msisdn = INVALID_MSISDN;  // Этот MSISDN не был добавлен в базу
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31);
        
        // When + Then
        mockMvc.perform(post("/v1/cdr/report")
                .param("msisdn", msisdn)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Тест генерации отчета CDR без обязательных параметров.
     * <p>
     * Проверяет, что контроллер корректно обрабатывает запрос без
     * необходимых параметров и возвращает ошибку Bad Request.
     * </п>
     */
    @Test
    void generateCdrReport_WithMissingParameters_ShouldReturnBadRequest() throws Exception {
        // Given + When + Then
        mockMvc.perform(post("/v1/cdr/report")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Тест генерации отчета CDR с невалидным форматом MSISDN.
     * <p>
     * Проверяет, что контроллер корректно обрабатывает запрос с
     * некорректным форматом MSISDN и возвращает ошибку Bad Request.
     * </п>
     */
    @Test
    void generateCdrReport_WithInvalidMsisdnFormat_ShouldReturnBadRequest() throws Exception {
        // Given
        String invalidMsisdnFormat = "123";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31);

        // When + Then

        mockMvc.perform(post("/v1/cdr/report")
                .param("msisdn", invalidMsisdnFormat)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Тест генерации отчета CDR с невалидным форматом даты.
     * <p>
     * Проверяет, что контроллер корректно обрабатывает запрос с
     * некорректным форматом даты и возвращает ошибку Bad Request.
     * </п>
     */
    @Test
    void generateCdrReport_WithInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        // Given
        String msisdn = TEST_MSISDN;
        String invalidDateFormat = "01/01/2023";

        // When & Then
        mockMvc.perform(post("/v1/cdr/report")
                .param("msisdn", msisdn)
                .param("startDate", invalidDateFormat)
                .param("endDate", LocalDate.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
