package org.example.roamingaggregatorservice.repositories;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CdrRepository extends JpaRepository<Cdr,Long> {

    @Override
    <S extends Cdr> List<S> saveAll(Iterable<S> entities);

    List<Cdr> findAllByCalledNumber(String calledNumber);

    List<Cdr> findAllByCallerNumber(String callerNumber);

    @Query("SELECT e FROM Cdr e WHERE YEAR(e.startDateTime) = :year AND MONTH(e.startDateTime) = :month AND e.calledNumber= :calledNumber")
    List<Cdr> findAllByCalledNumberAndStartDateTime(String calledNumber, int year, int month);

    @Query("SELECT e FROM Cdr e WHERE YEAR(e.startDateTime) = :year AND MONTH(e.startDateTime) = :month AND e.callerNumber= :callerNumber")
    List<Cdr> findAllByCallerNumberAndStartDateTime(String callerNumber, int year, int month);

    @Query("select c from Cdr c where c.calledNumber= :msisdn OR c.callerNumber= :msisdn AND c.startDateTime BETWEEN :startDate AND :endDate ORDER BY c.startDateTime ASC")
    List<Cdr> findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc(String msisdn, LocalDateTime startDate, LocalDateTime endDate);
}
