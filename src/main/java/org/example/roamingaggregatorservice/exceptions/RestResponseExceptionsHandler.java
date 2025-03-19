package org.example.roamingaggregatorservice.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.example.roamingaggregatorservice.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseExceptionsHandler {

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<ExceptionDTO> handleConflict(ConstraintViolationException ex) {
        ExceptionDTO dto = new ExceptionDTO(
            LocalDateTime.now(), 
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR", 
            ex.getMessage()
        );
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoSuchSubscriberException.class)
    protected ResponseEntity<ExceptionDTO> handleConflict(NoSuchSubscriberException ex) {
        ExceptionDTO dto = new ExceptionDTO(
            LocalDateTime.now(), 
            HttpStatus.BAD_REQUEST.value(),
            "BAD_REQUEST",
            ex.getMessage()
        );
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ExceptionDTO> handleConflict(MethodArgumentTypeMismatchException ex) {
        ExceptionDTO dto = new ExceptionDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage()
        );
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = StartDateIsAfterEndDateException.class)
    protected ResponseEntity<ExceptionDTO> handleConflict(StartDateIsAfterEndDateException ex) {
        ExceptionDTO dto = new ExceptionDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ex.getMessage()
        );
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }
}
