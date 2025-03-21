package org.example.roamingaggregatorservice.exceptions;

import java.time.LocalDate;

/**
 * Исключение, которое выбрасывается, когда дата начала периода оказывается позже даты окончания.
 * <p>
 * Это исключение используется для валидации временных диапазонов в запросах 
 * на получение или генерацию отчетов о звонках.
 * </p>
 * 
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
public class StartDateIsAfterEndDateException extends RuntimeException {

    /**
     * Создает новый экземпляр исключения с сообщением по умолчанию.
     */
    public StartDateIsAfterEndDateException() {
        super("Дата начала периода должна быть раньше даты окончания.");
    }

    /**
     * Создает новый экземпляр исключения с сообщением, содержащим информацию о конкретных датах.
     *
     * @param startDate Дата начала периода, которая оказалась позже
     * @param endDate Дата окончания периода, которая оказалась раньше
     */
    public StartDateIsAfterEndDateException(LocalDate startDate, LocalDate endDate) {
        super("Дата начала периода: %s должна быть раньше даты окончания: %s".formatted(startDate,endDate));
    }
}
