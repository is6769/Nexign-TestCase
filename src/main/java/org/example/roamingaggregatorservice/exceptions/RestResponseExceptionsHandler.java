package org.example.roamingaggregatorservice.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.example.roamingaggregatorservice.dto.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseExceptionsHandler {

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<ExceptionDTO> handleConflict(ConstraintViolationException ex) {
        ExceptionDTO dto = new ExceptionDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }
}
