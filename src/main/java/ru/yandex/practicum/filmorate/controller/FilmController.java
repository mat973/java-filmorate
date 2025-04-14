package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FullFilm;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final FilmService filmService;
    private final UserService userService;

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public FilmDto createFilm(@Valid @RequestBody FilmDto filmDto) {
        log.debug("Начало обработки запроса на создание фильма: {}", filmDto);
        return filmService.createFilm(filmDto);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public FilmDto updateFilm(@Valid @RequestBody FilmDto filmDto) {
        log.debug("Начало обработки запроса на обновление фильма: {}", filmDto);
        return filmService.updateFilm(filmDto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<FilmDto> getAllFilms() {
        log.debug("Запрос на получение списка всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public FullFilm getFullFilmById(@PathVariable Long filmId) {
        log.debug("Запрос на получение фильма с id: {}", filmId);
        return filmService.getFilmWithGenre(filmId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<FilmDto> getFilmsByNameOrDirector(
            @RequestParam(required = false) String query,
            @RequestParam List<String> by
    ) {
        log.debug("Запрос на поиск фильмов с параметрами: {}, {}", query, by);
        List<FilmDto> films = filmService.getFilmsByNameOrDirector(query, by);
        log.debug("Вернули список фильмов: {}", films);
        return films;
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public List<FilmDto> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Запрос на получение общих фильмов между пользователем с id {} и другом с id {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }


    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Запрос на добавление лайка фильму с filmId {} от пользователя с userId {}", filmId, userId);
        filmService.addLike(filmId, userId);
        userService.createEvent(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Запрос на удаление лйка фильму с filmId {} от пользователя с userId {}", filmId, userId);
        filmService.deleteLike(filmId, userId);
        userService.createEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<FilmDto> getPopularFilms(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        log.info("Запрос на получение популярных фильмов с количеством {}, жанром {} и годом {}", count, genreId, year);
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFimByDirectorId(@PathVariable Long directorId, @RequestParam String sortBy) {
        log.info("Запрос на получение фильмов режиссера с id {} отсортированных по {}", directorId, sortBy);
        return filmService.getFilmsByDirectorId(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFilmById(@PathVariable Long filmId) {
        log.debug("Запрос на удаление фильма с id {}", filmId);
        filmService.deleteFilmById(filmId);
    }
}