package org.example.roamingaggregatorservice.repositories;

import org.example.roamingaggregatorservice.entities.Subscriber;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    @Override
    <S extends Subscriber> List<S> findAll(Example<S> example);

    Optional<Subscriber> findSubscriberByMsisdn(String msisdn);
}
