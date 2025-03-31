package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FullFilm;
import ru.yandex.practicum.filmorate.dto.Mpa;
import ru.yandex.practicum.filmorate.exeption.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl.Rating;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;

    private final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final LocalDate checkDate = LocalDate.of(1895, 12, 28);
    private static final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public FilmService(@Qualifier("film-bd")FilmStorage filmStorage, UserService userService, GenreService genreService, MpaService mpaService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreService = genreService;
        this.mpaService = mpaService;
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
//        Film oldFilm = filmStorage.find(filmDto.getId()).orElseThrow(() -> new FilmNotFoundException(
//                "Фильм с id: " + id + " не найден."
//        ));
        if (filmDto.getGenres() == null){
            filmDto.setGenres(filmStorage.find(filmDto.getId()).get().getGenres().stream().map(Genre::new).collect(Collectors.toList()));
        }
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
        if (userId == null || !userService.contain(userId)) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }
        if (id == null || !filmStorage.existById(id)) {
            throw new FilmNotFoundException("Фильм с id: " + id + " не удалось найти :(");
        }

        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Long id, Long userId) {
        if (userId == null || !userService.contain(userId)) {
            throw new UserNotFoundException("Пользователя не может быть с пустым id");
        }
        if (id == null || !filmStorage.existById(id)) {
            throw new FilmNotFoundException("Фильм с id: " + id + " не удалось найти :(");
        }

        filmStorage.dislike(id,userId);
    }

    public List<FilmDto> getPopularFilms(Integer count) {

        return filmStorage.getPopularFilms(count).stream()
                .map(FilmService::mapToFilDto)
                .collect(Collectors.toList());
    }
    public FullFilm getFilmWithGenre(Long id) {
      FilmDto filmDto = mapToFilDto(filmStorage.find(id)
              .orElseThrow(() ->  new FilmNotFoundException("Фильмиа са такми id не сущевует")));
      Mpa mpa = mpaService.getMpaById((long) filmDto.getMpa().getId());
      List<ru.yandex.practicum.filmorate.dto.Genre> genres = filmDto.getGenres().stream()
              .map(x -> Long.valueOf(x.getId()))
              .map(genreService::getGenreById)
              .toList();


        return FullFilm.builder()
                .id(filmDto.getId())
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .duration(filmDto.getDuration())
                .mpa(mpa)
                .releaseDate(filmDto.getReleaseDate())
                .genres(genres)
                .build();
    }


    private Film mapToFilm(FilmDto filmDto) {
        LocalDate date = LocalDate.parse(filmDto.getReleaseDate(), formater);
        if (date.isBefore(checkDate)) {
            throw new DateIsToOldException("Дата не должна быть раньше " + checkDate.format(formater));
        }
        Duration duration = Duration.ofMinutes(filmDto.getDuration());

        if (filmDto.getMpa() == null){
            throw  new MpaNotExistException("Рейтинг не может быть путсым");
        }
        if (filmDto.getGenres() == null){
            throw new GenreNotExistException("Фильм должен быть какого то жанра");
        }

        return Film.builder()
                .id(filmDto.getId())
                .description(filmDto.getDescription())
                .duration(duration)
                .title(filmDto.getName())
                .releaseDate(date)
                .mpa(filmDto.getMpa().getId())
                .genres(filmDto.getGenres().stream().map(Genre::getId).collect(Collectors.toList()))
                .build();
    }


    private static FilmDto mapToFilDto(Film film) {

        return FilmDto.builder()
                .id(film.getId())
                .description(film.getDescription())
                .name(film.getTitle())
                .releaseDate(film.getReleaseDate().format(formater))
                .duration(film.getDuration().getSeconds() / 60)
                .mpa(new MpaRating(film.getMpa()))
                .genres(film.getGenres().stream().map(Genre::new).collect(Collectors.toList()))
                .build();
    }


}
