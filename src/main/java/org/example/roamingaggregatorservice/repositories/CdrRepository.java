package org.example.roamingaggregatorservice.repositories;

import org.example.roamingaggregatorservice.entities.Cdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с записями данных вызовов (CDR).
 * <p>
 * Обеспечивает доступ к базе данных для выполнения операций 
 * создания, чтения, обновления и удаления (CRUD) записей CDR.
 * Предоставляет методы для поиска записей по различным критериям,
 * таким как номер вызывающего, номер вызываемого, период времени и т.д.
 * </p>
 * 
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
public interface CdrRepository extends JpaRepository<Cdr,Long> {

    /**
     * Сохранение списка записей CDR.
     *
     * @param entities Список записей CDR для сохранения
     * @param <S> Тип, производный от сущности Cdr
     * @return Список сохраненных записей CDR
     */
    @Override
    <S extends Cdr> List<S> saveAll(Iterable<S> entities);

    /**
     * Поиск всех записей CDR, в которых указанный номер является вызываемым.
     *
     * @param calledNumber Номер вызываемого абонента
     * @return Список записей CDR, где абонент был вызываемым
     */
    List<Cdr> findAllByCalledNumber(String calledNumber);

    /**
     * Поиск всех записей CDR, в которых указанный номер является вызывающим.
     *
     * @param callerNumber Номер вызывающего абонента
     * @return Список записей CDR, где абонент был вызывающим
     */
    List<Cdr> findAllByCallerNumber(String callerNumber);

    /**
     * Поиск всех записей CDR для указанного вызываемого номера в определенный месяц и год.
     * <p>
     * Метод использует JPQL запрос для фильтрации записей по году и месяцу.
     * </p>
     *
     * @param calledNumber Номер вызываемого абонента
     * @param year Год для фильтрации
     * @param month Месяц для фильтрации (1-12)
     * @return Список записей CDR, соответствующих критериям
     */
    @Query("SELECT e FROM Cdr e WHERE YEAR(e.startDateTime) = :year AND MONTH(e.startDateTime) = :month AND e.calledNumber= :calledNumber")
    List<Cdr> findAllByCalledNumberAndStartDateTime(String calledNumber, int year, int month);

    /**
     * Поиск всех записей CDR для указанного вызывающего номера в определенный месяц и год.
     * <p>
     * Метод использует JPQL запрос для фильтрации записей по году и месяцу.
     * </p>
     *
     * @param callerNumber Номер вызывающего абонента
     * @param year Год для фильтрации
     * @param month Месяц для фильтрации (1-12)
     * @return Список записей CDR, соответствующих критериям
     */
    @Query("SELECT e FROM Cdr e WHERE YEAR(e.startDateTime) = :year AND MONTH(e.startDateTime) = :month AND e.callerNumber= :callerNumber")
    List<Cdr> findAllByCallerNumberAndStartDateTime(String callerNumber, int year, int month);

    /**
     * Поиск всех записей CDR, где абонент был либо вызывающим, либо вызываемым, в указанный период времени.
     * <p>
     * Метод возвращает записи, отсортированные по времени начала вызова.
     * </p>
     *
     * @param msisdn Номер абонента
     * @param startDate Начало периода времени
     * @param endDate Конец периода времени
     * @return Отсортированный список записей CDR, соответствующих критериям
     */
    @Query("select c from Cdr c where (c.calledNumber= :msisdn OR c.callerNumber= :msisdn) AND c.startDateTime BETWEEN :startDate AND :endDate ORDER BY c.startDateTime ASC")
    List<Cdr> findAllByCalledNumberOrCalledNumberAndStartDateTimeBetweenOrderByStartDateTimeAsc(String msisdn, LocalDateTime startDate, LocalDateTime endDate);
}
