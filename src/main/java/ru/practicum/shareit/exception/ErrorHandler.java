package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleValidateException(
            final ValidationException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(Map.of("message",
                e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNotFoundException(
            final NotFoundException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(Map.of("message",
                e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleExistsElementException(
            final ExistsElementException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Map.of("message", e.getMessage()),
                HttpStatus.CONFLICT);
    }
}
