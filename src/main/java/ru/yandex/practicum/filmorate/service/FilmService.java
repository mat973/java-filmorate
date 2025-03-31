package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exeption.DateIsToOldException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.IncorrectOfRatingException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl.Rating;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service

public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    private final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final LocalDate checkDate = LocalDate.of(1895, 12, 28);
    private final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public FilmService(@Qualifier("film-bd")FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public FilmDto createFilm(FilmDto filmDto) {
        Film film = filmStorage.save(mapToFilm(filmDto));
        return mapToFilDto(film);
    }

    public FilmDto updateFilm(FilmDto filmDto) {
        Long id = filmDto.getId();
        if (id == null || !filmStorage.existById(id)) {
            String errorMessage = "Фильм " + (id == null ? "должен иметь id." : "с id " + id + " не существует.");
            throw new FilmNotFoundException(errorMessage);
        }
        Film oldFilm = filmStorage.find(filmDto.getId()).orElseThrow(() -> new FilmNotFoundException(
                "Фильм с id: " + id + " не найден."
        ));
        Film newFilm = mapToFilm(filmDto);

        return mapToFilDto(filmStorage.update(newFilm));
    }


    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public FilmDto getFilmById(Long id) {
        Film film = filmStorage.find(id)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id:" + id + "не удалось найти :("));
        return mapToFilDto(film);
    }

    public void addLike(Long id, Long userId) {
        return;
        //TODO
//        if (userId == null || !userService.contain(userId)) {
//            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
//        }
//        if (id == null || !filmStorage.existById(id)) {
//            throw new FilmNotFoundException("Фильм с id: " + id + " не удалось найти :(");
//        }
//
//        filmStorage.find(id)
//                .orElseThrow(() -> new FilmNotFoundException("Фильм с id: " + id + " не найден."))
//                .getLikes().add(userId);
    }

    public void deleteLike(Long id, Long userId) {
        return;
        //todo
//        if (userId == null || !userService.contain(userId)) {
//            throw new UserNotFoundException("Пользователя не может быть с пустым id");
//        }
//        if (id == null || !filmStorage.existById(id)) {
//            throw new FilmNotFoundException("Фильм с id: " + id + " не удалось найти :(");
//        }
//
//        filmStorage.find(id)
//                .orElseThrow(() -> new FilmNotFoundException("Фильм с id: " + id + " не найден."))
//                .getLikes().remove(userId);
    }

    public List<FilmDto> getPopularFilms(Integer count) {
        return null;
        //TODO
//        return filmStorage.getAllFilms().stream()
//                .sorted(Comparator.comparingInt((Film x) -> x.getLikes().size()).reversed())
//                .limit(count)
//                .map(this::mapToFilDto)
//                .collect(Collectors.toList());
    }


    private Film mapToFilm(FilmDto filmDto) {
        LocalDate date = LocalDate.parse(filmDto.getReleaseDate(), formater);
        if (date.isBefore(checkDate)) {
            throw new DateIsToOldException("Дата не должна быть раньше " + checkDate.format(formater));
        }
        Duration duration = Duration.ofMinutes(filmDto.getDuration());

        Rating rating;
        if (filmDto.getMpa() == null){
            rating = Rating.NC_17;
        }else {
            try {
                rating = Rating.valueOf(filmDto.getMpa());
            } catch (IllegalArgumentException e) {
                throw new IncorrectOfRatingException("Введите корректный рейтинг фильма");
            }
        }
        return Film.builder()
                .id(filmDto.getId())
                .description(filmDto.getDescription())
                .duration(duration)
                .title(filmDto.getName())
                .releaseDate(date)
                .mpa(rating)
                .genres(filmDto.getGenres())
                .build();
    }


    private FilmDto mapToFilDto(Film film) {

        return FilmDto.builder()
                .id(film.getId())
                .description(film.getDescription())
                .name(film.getTitle())
                .releaseDate(film.getReleaseDate().format(formater))
                .duration(film.getDuration().getSeconds() / 60)
                .mpa(film.getMpa().getTitle())
                .genres(film.getGenres())
                .build();
    }
}
