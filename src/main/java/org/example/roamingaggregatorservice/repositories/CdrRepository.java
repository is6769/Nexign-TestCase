package org.example.roamingaggregatorservice.repositories;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CdrRepository extends JpaRepository<Cdr,Long> {

    @Override
    <S extends Cdr> List<S> saveAll(Iterable<S> entities);

    @Query("select c from Cdr c where c.callerNumber=:msidn or c.calledNumber=:msidn")
    List<Cdr> findCdrByCallerNumberOrCalledNumber(String msisdn);

    List<Cdr> findAllByCalledNumber(String calledNumber);

    List<Cdr> findAllByCallerNumber(String callerNumber);
}
