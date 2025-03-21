package org.example.roamingaggregatorservice.repositories;

import org.example.roamingaggregatorservice.entities.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для репозитория SubscriberRepository.
 * <p>
 * Данный класс содержит тесты для проверки функциональности репозитория SubscriberRepository.
 * </p>
 */
@DataJpaTest
public class SubscriberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscriberRepository subscriberRepository;
    
    @BeforeEach
    void setUp() {
        // Clear database before each test to avoid unique constraint violations
        subscriberRepository.deleteAll();
        entityManager.flush();
    }

    /**
     * Тест поиска абонента по MSISDN с положительным результатом.
     * <p>
     * Проверяет, что метод findSubscriberByMsisdn корректно находит
     * абонента по существующему номеру.
     * </p>
     */
    @Test
    public void findSubscriberByMsisdn_WithExistingMsisdn_ShouldReturnSubscriber() {
        // Given - use UUID to ensure unique MSISDNs across test runs
        String msisdn = "7900" + UUID.randomUUID().toString().substring(0, 8);
        
        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn(msisdn);
        entityManager.persist(subscriber);
        entityManager.flush();

        // When
        Optional<Subscriber> found = subscriberRepository.findSubscriberByMsisdn(msisdn);

        // Then
        assertTrue(found.isPresent());
        assertEquals(msisdn, found.get().getMsisdn());
    }

    /**
     * Тест поиска абонента по MSISDN с отрицательным результатом.
     * <p>
     * Проверяет, что метод findSubscriberByMsisdn возвращает пустой Optional
     * при поиске по несуществующему номеру.
     * </p>
     */
    @Test
    public void findSubscriberByMsisdn_WithNonExistentMsisdn_ShouldReturnEmpty() {
        // Given - use UUID to ensure unique MSISDNs across test runs
        String existingMsisdn = "7901" + UUID.randomUUID().toString().substring(0, 8);
        String nonExistentMsisdn = "7902" + UUID.randomUUID().toString().substring(0, 8);
        
        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn(existingMsisdn);
        entityManager.persist(subscriber);
        entityManager.flush();

        // When
        Optional<Subscriber> found = subscriberRepository.findSubscriberByMsisdn(nonExistentMsisdn);

        // Then
        assertFalse(found.isPresent());
    }

    /**
     * Тест получения всех абонентов.
     * <p>
     * Проверяет, что метод findAll корректно возвращает все сохраненные
     * в базе данных записи абонентов.
     * </p>
     */
    @Test
    public void findAll_WithMultipleSubscribers_ShouldReturnAllSubscribers() {
        // Given - use UUID to ensure unique MSISDNs across test runs
        Subscriber subscriber1 = new Subscriber();
        subscriber1.setMsisdn("7903" + UUID.randomUUID().toString().substring(0, 8));
        entityManager.persist(subscriber1);

        Subscriber subscriber2 = new Subscriber();
        subscriber2.setMsisdn("7904" + UUID.randomUUID().toString().substring(0, 8));
        entityManager.persist(subscriber2);

        Subscriber subscriber3 = new Subscriber();
        subscriber3.setMsisdn("7905" + UUID.randomUUID().toString().substring(0, 8));
        entityManager.persist(subscriber3);
        
        entityManager.flush();

        // When
        Iterable<Subscriber> allSubscribers = subscriberRepository.findAll();

        // Then
        int count = 0;
        for (Subscriber ignored : allSubscribers) {
            count++;
        }
        assertEquals(3, count);
    }

    /**
     * Тест сохранения нового абонента.
     * <p>
     * Проверяет, что метод save корректно сохраняет нового абонента
     * в базу данных и присваивает ему идентификатор.
     * </p>
     */
    @Test
    public void save_WithNewSubscriber_ShouldSaveAndAssignId() {
        // Given - use UUID to ensure unique MSISDNs across test runs
        String msisdn = "7906" + UUID.randomUUID().toString().substring(0, 8);
        Subscriber subscriber = new Subscriber();
        subscriber.setMsisdn(msisdn);

        // When
        Subscriber saved = subscriberRepository.save(subscriber);

        // Then
        assertNotNull(saved.getId());
        assertEquals(msisdn, saved.getMsisdn());
        
        // Verify it was actually saved to the database
        Subscriber foundInDb = entityManager.find(Subscriber.class, saved.getId());
        assertNotNull(foundInDb);
        assertEquals(msisdn, foundInDb.getMsisdn());
    }
}
