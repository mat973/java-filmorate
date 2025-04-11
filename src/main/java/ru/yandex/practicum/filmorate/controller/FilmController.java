package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FullFilm;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    private FilmService filmService;

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public FilmDto createFilm(@Valid @RequestBody FilmDto filmDto) {
        log.debug("Начало обработки запроса на создание фильма: {}", filmDto);
        log.warn(filmDto.toString());
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
    public List<Film> getAllFilms() {
        log.debug("Запрос на получение списка всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    @ResponseStatus(HttpStatus.OK)
    public FullFilm getFullFilmById(@PathVariable Long filmId) {
        log.debug("Запрос на получение фильма с id: {}", filmId);
        return filmService.getFilmWithGenre(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Запрос на добавление лйка фильму с filmId {} от пользоватля с userId {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Запрос на удаление лйка фильму с filmId {} от пользоватля с userId {}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<FilmDto> getPopularFilmsByGenreOfYear(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        log.info("Запрос на получение популярных фильмов с количеством {}, жанром {} и годом {}", count, genreId, year);
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFimByDirectorId(@PathVariable Long directorId, @RequestParam String sortBy) {
        log.info("Запрос на получение фильмов режисера с id {} тсартированных по {}", directorId, sortBy);
        return filmService.getFilmsByDirectorId(directorId, sortBy);
    }
}