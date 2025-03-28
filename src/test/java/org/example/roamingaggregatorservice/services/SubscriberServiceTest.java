package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.example.roamingaggregatorservice.repositories.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тесты для сервиса SubscriberService.
 * <p>
 * Данный класс содержит юнит-тесты для проверки функциональности сервиса SubscriberService.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
public class SubscriberServiceTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private SubscriberService subscriberService;

    private List<Subscriber> subscribers;
    private String existingMsisdn;
    private String nonExistentMsisdn;

    @BeforeEach
    void setUp() {
        existingMsisdn = "79123456789";
        nonExistentMsisdn = "79999999999";
        
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setId(1L);
        subscriber1.setMsisdn(existingMsisdn);

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setId(2L);
        subscriber2.setMsisdn("79876543210");

        subscribers = Arrays.asList(subscriber1, subscriber2);
    }

    /**
     * Тест получения всех абонентов.
     * <p>
     * Проверяет, что метод findAll корректно возвращает список всех абонентов.
     * </p>
     */
    @Test
    void findAll_ShouldReturnAllSubscribers() {
        // Given
        when(subscriberRepository.findAll()).thenReturn(subscribers);

        // When
        List<Subscriber> result = subscriberService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(existingMsisdn, result.get(0).getMsisdn());
        assertEquals("79876543210", result.get(1).getMsisdn());
        
        verify(subscriberRepository).findAll();
    }

    

    /**
     * Тест проверки существования абонента.
     * <p>
     * Проверяет, что метод выбрасывает исключение NoSuchSubscriberException
     * при поиске несуществующего абонента.
     * </p>
     */
    @Test
    void checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException_WithNonExistentSubscriber_ShouldThrow() {
        // Given
        when(subscriberRepository.findSubscriberByMsisdn(nonExistentMsisdn))
                .thenReturn(Optional.empty());

        // When + Then
        assertThrows(NoSuchSubscriberException.class, () -> {
            subscriberService.checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(nonExistentMsisdn);
        });
        
        verify(subscriberRepository).findSubscriberByMsisdn(nonExistentMsisdn);
    }
}
