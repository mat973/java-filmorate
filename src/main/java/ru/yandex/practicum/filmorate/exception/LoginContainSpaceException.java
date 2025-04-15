package ru.yandex.practicum.filmorate.exception;

public class LoginContainSpaceException extends RuntimeException {
    public LoginContainSpaceException() {
        super("Логин содержит пробелы");
    }
}
