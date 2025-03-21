package org.example.roamingaggregatorservice.exceptions;

/**
 * Исключение, которое выбрасывается, когда запрашиваемый абонент не найден в системе.
 * <p>
 * Это исключение используется при попытке получения данных о звонках
 * или генерации отчета для несуществующего абонента.
 * </p>
 * 
 * @author Сервис роуминговой агрегации
 * @since 1.0
 */
public class NoSuchSubscriberException extends RuntimeException {
    
    /**
     * Создает новый экземпляр исключения с сообщением по умолчанию.
     */
    public NoSuchSubscriberException() {
        super("Абонент с указанным номером не найден");
    }
    
    /**
     * Создает новый экземпляр исключения с сообщением, содержащим конкретный номер абонента.
     *
     * @param msisdn Номер телефона абонента, который не был найден
     */
    public NoSuchSubscriberException(String msisdn) {
        super("Абонент с номером " + msisdn + " не найден");
    }
}
