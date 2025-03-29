package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.dto.ExceptionDto;
import ru.yandex.practicum.filmorate.exeption.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleUserNotFoundException(final UserNotFoundException e) {
        return new ExceptionDto(e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleFilmNotFoundException(final FilmNotFoundException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleDateIsToOldException(final DateIsToOldException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleDateNotExistException(final DateNotExistException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleLoginContainSpaceException(final LoginContainSpaceException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleLoginContainSpaceException(final MethodArgumentNotValidException e) {
        String result = e.getBindingResult().getAllErrors().stream()
                .filter(ex -> ex instanceof FieldError)
                .map(ex -> (FieldError) ex)
                .map(ex -> "В поле " + ex.getField() + " возникла ошибка: " + ex.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ExceptionDto(result);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleFriendsException(final FriendsException e) {
        return new ExceptionDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleInternalServerException(final InternalServerException e){
        return new ExceptionDto(e.getMessage());
    }
}
