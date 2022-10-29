package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String error) {
        super(error);
    }
}
