package ru.practicum.shareit.exception;

public class ExistsElementException extends RuntimeException {
    public ExistsElementException(String error) {
        super(error);
    }
}
