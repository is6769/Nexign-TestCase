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
