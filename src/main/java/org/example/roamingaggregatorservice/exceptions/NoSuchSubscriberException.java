package org.example.roamingaggregatorservice.exceptions;

public class NoSuchSubscriberException extends RuntimeException {

    public NoSuchSubscriberException(){
        super("Can't find such subscriber. Please check msisdn you provided.");
    }

    public NoSuchSubscriberException(String message) {
        super(message);
    }
}
