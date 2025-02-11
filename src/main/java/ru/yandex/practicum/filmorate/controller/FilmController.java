package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ExceptionDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exeption.DateIsToOldException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final LocalDate checkDate = LocalDate.of(1895, 12, 28);
    private final Map<Long, Film> filmMap = new HashMap<>();
    private final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Long currentId = 1L;

    @PostMapping()
    public ResponseEntity<?> createFilm(@Valid @RequestBody FilmDto filmDto, BindingResult bindingResult) {
        log.debug("Начало обработки запроса на создание фильма: {}", filmDto);

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(" "));
            log.warn("Ошибка валидации при создании фильма: {}", errorMessage);
            return new ResponseEntity<>(new ExceptionDto(errorMessage), HttpStatus.BAD_REQUEST);
        }

        Film film;
        try {
            filmDto.setId(currentId++);
            film = mapToFilm(filmDto);
            log.info("Фильм успешно создан: {}", film);
        } catch (DateTimeParseException e) {
            log.error("Ошибка парсинга даты релиза: {}", e.getMessage());
            return new ResponseEntity<>(new ExceptionDto("Такой даты не существует."), HttpStatus.BAD_REQUEST);
        } catch (DateIsToOldException e) {
            log.error("Ошибка при создании фильма: {}", e.getMessage());
            return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        filmMap.put(film.getId(), film);
        log.trace("Фильм добавлен в хранилище. Текущее количество фильмов: {}", filmMap.size());
        return ResponseEntity.ok(mapToFilDto(film));
    }

    @PutMapping()
    public ResponseEntity<?> updateFilm(@Valid @RequestBody FilmDto filmDto, BindingResult bindingResult) {
        log.debug("Начало обработки запроса на обновление фильма: {}", filmDto);

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(" "));
            log.warn("Ошибка валидации при обновлении фильма: {}", errorMessage);
            return new ResponseEntity<>(new ExceptionDto(errorMessage), HttpStatus.BAD_REQUEST);
        }

        Long id = filmDto.getId();
        if (id == null || !filmMap.containsKey(id)) {
            String errorMessage = "Фильм " + (id == null ? "должен иметь id." : "с id " + id + " не существует.");
            log.warn("Ошибка при обновлении фильма: {}", errorMessage);
            return new ResponseEntity<>(new ExceptionDto(errorMessage), HttpStatus.NOT_FOUND);
        }

        Film film;
        try {
            film = mapToFilm(filmDto);
            log.info("Фильм успешно обновлён: {}", film);
        } catch (DateTimeParseException e) {
            log.error("Ошибка парсинга даты релиза: {}", e.getMessage());
            return new ResponseEntity<>(new ExceptionDto("Такой даты не существует."), HttpStatus.BAD_REQUEST);
        } catch (DateIsToOldException e) {
            log.error("Ошибка при обновлении фильма: {}", e.getMessage());
            return new ResponseEntity<>(new ExceptionDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(mapToFilDto(film));
    }

    @GetMapping()
    public ResponseEntity<?> getAllFilms() {
        log.debug("Запрос на получение списка всех фильмов");
        log.trace("Текущее количество фильмов: {}", filmMap.size());
        return ResponseEntity.ok(filmMap.values().stream().map(this::mapToFilDto).toList());
    }

    private Film mapToFilm(FilmDto filmDto) {
        log.debug("Начало преобразования FilmDto в Film: {}", filmDto);

        LocalDate date;
        try {
            date = LocalDate.parse(filmDto.getReleaseDate(), formater);
            log.info("Дата релиза успешно распаршена: {}", date);
        } catch (DateTimeParseException e) {
            log.error("Ошибка парсинга даты релиза: {}", e.getMessage());
            throw e;
        }

        if (date.isBefore(checkDate)) {
            log.error("Дата релиза раньше допустимой: {}", date);
            throw new DateIsToOldException("Дата не должна быть раньше " + checkDate.format(formater));
        }

        Duration duration = Duration.ofMinutes(filmDto.getDuration());
        log.trace("Длительность фильма преобразована в Duration: {}", duration);

        Film film = Film.builder()
                .id(filmDto.getId())
                .description(filmDto.getDescription())
                .duration(duration)
                .name(filmDto.getName())
                .releaseDate(date)
                .build();

        log.trace("Фильм успешно преобразован: {}", film);
        return film;
    }

    private FilmDto mapToFilDto(Film film) {
        log.trace("Начало преобразования Film в FilmDto: {}", film);

        FilmDto filmDto = FilmDto.builder()
                .id(film.getId())
                .description(film.getDescription())
                .name(film.getName())
                .releaseDate(film.getReleaseDate().format(formater))
                .duration(film.getDuration().getSeconds() / 60)
                .build();

        log.trace("FilmDto успешно создан: {}", filmDto);
        return filmDto;
    }
}