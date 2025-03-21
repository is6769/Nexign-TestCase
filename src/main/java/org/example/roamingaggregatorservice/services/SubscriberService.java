package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.example.roamingaggregatorservice.repositories.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы с абонентами.
 * Предоставляет методы для поиска и проверки существования абонентов.
 */
@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    /**
     * Получает список всех абонентов.
     *
     * @return Список всех абонентов
     */
    public List<Subscriber> findAll(){
        return subscriberRepository.findAll();
    }

    /**
     * Проверяет существование абонента с указанным номером телефона.
     * Выбрасывает исключение, если абонент не найден.
     *
     * @param msisdn Номер телефона абонента
     * @throws NoSuchSubscriberException если абонент с указанным номером не найден
     */
    public void checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(String msisdn){
        subscriberRepository.findSubscriberByMsisdn(msisdn).orElseThrow(NoSuchSubscriberException::new);
    }
}
