package org.example.roamingaggregatorservice.services;

import org.example.roamingaggregatorservice.entities.Subscriber;
import org.example.roamingaggregatorservice.exceptions.NoSuchSubscriberException;
import org.example.roamingaggregatorservice.repositories.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public List<Subscriber> findAll(){
        return subscriberRepository.findAll();
    }

    public void checkIfSubscriberExistsOrElseThrowNoSuchSubscriberException(String msisdn){
        subscriberRepository.findSubscriberByMsisdn(msisdn).orElseThrow(NoSuchSubscriberException::new);
    }


}
