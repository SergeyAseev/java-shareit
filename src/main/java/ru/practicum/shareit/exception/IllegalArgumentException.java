package ru.practicum.shareit.exception;

public class IllegalArgumentException extends RuntimeException {

    public IllegalArgumentException(String error) {
        super(error);
    }
}
