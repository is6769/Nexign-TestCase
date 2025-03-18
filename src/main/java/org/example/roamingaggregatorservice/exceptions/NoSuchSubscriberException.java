package org.example.roamingaggregatorservice.exceptions;

public class NoSuchSubscriberException extends RuntimeException {
    
    public NoSuchSubscriberException() {
        super("Абонент с указанным номером не найден");
    }
    
    public NoSuchSubscriberException(String msisdn) {
        super("Абонент с номером " + msisdn + " не найден");
    }
}
