package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exeption.DateIsToOldException;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    private InMemoryFilmStorage inMemoryFilmStorage;
    @Autowired
    private UserService userService;
    private final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final LocalDate checkDate = LocalDate.of(1895, 12, 28);

    private final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public FilmDto createFilm(FilmDto filmDto) {
        Film film = inMemoryFilmStorage.save(mapToFilm(filmDto));
        return mapToFilDto(film);
    }

    public FilmDto updateFilm(FilmDto filmDto) {
        Long id = filmDto.getId();
        if (id == null || inMemoryFilmStorage.find(id).isEmpty()) {
            String errorMessage = "Фильм " + (id == null ? "должен иметь id." : "с id " + id + " не существует.");
            throw new FilmNotFoundException(errorMessage);
        }
        Film oldFilm = inMemoryFilmStorage.find(filmDto.getId()).get();
        Film newFilm = mapToFilm(filmDto);
        newFilm.setLikes(oldFilm.getLikes());


        return mapToFilDto(inMemoryFilmStorage.update(newFilm));
    }


    public List<Film> getAllFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    public FilmDto getFilmById(Long id) {
        Optional<Film> film = inMemoryFilmStorage.find(id);
        if (film.isEmpty()) {
            throw new FilmNotFoundException("Фильм с id: " + id + " не удалось найти :(");
        }
        return mapToFilDto(film.get());
    }

    public void addLike(Long id, Long userId) {
        if (userId == null || !userService.contain(userId)) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }
        if (id == null || inMemoryFilmStorage.find(id).isEmpty()) {
            throw new FilmNotFoundException("Фильм с id: " + id + " не удалось найти :(");
        }

        inMemoryFilmStorage.find(id).get().getLikes().add(userId);

    }

    public void deleteLike(Long id, Long userId) {
        if (userId == null || !userService.contain(userId)) {
            throw new UserNotFoundException("Пользователя не может быть с пустым id");
        }
        if (id == null || inMemoryFilmStorage.find(id).isEmpty()) {
            throw new FilmNotFoundException("Фильм с id: " + id + " не удалось найти :(");
        }

        inMemoryFilmStorage.find(id).get().getLikes().remove(userId);
    }

    public List<FilmDto> getPopularFilms(Integer count) {
        return inMemoryFilmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film x) -> x.getLikes().size()).reversed())
                .limit(count)
                .map(this::mapToFilDto)
                .collect(Collectors.toList());
    }


    private Film mapToFilm(FilmDto filmDto) {
        LocalDate date = LocalDate.parse(filmDto.getReleaseDate(), formater);
        if (date.isBefore(checkDate)) {
            throw new DateIsToOldException("Дата не должна быть раньше " + checkDate.format(formater));
        }
        Duration duration = Duration.ofMinutes(filmDto.getDuration());

        Film film = Film.builder()
                .id(filmDto.getId())
                .description(filmDto.getDescription())
                .duration(duration)
                .name(filmDto.getName())
                .releaseDate(date)
                .build();
        return film;
    }


    private FilmDto mapToFilDto(Film film) {
        FilmDto filmDto = FilmDto.builder()
                .id(film.getId())
                .description(film.getDescription())
                .name(film.getName())
                .releaseDate(film.getReleaseDate().format(formater))
                .duration(film.getDuration().getSeconds() / 60)
                .likes(film.getLikes())
                .build();

        return filmDto;
    }
}
