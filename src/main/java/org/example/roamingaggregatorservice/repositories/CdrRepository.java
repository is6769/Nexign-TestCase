package org.example.roamingaggregatorservice.repositories;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CdrRepository extends JpaRepository<Cdr,Long> {

    @Override
    <S extends Cdr> List<S> saveAll(Iterable<S> entities);
}
