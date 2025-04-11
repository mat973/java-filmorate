package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FullFilm;
import ru.yandex.practicum.filmorate.dto.Genre;
import ru.yandex.practicum.filmorate.dto.Mpa;
import ru.yandex.practicum.filmorate.exeption.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final GenreService genreService;
    private final MpaService mpaService;
    private final DirectorService directorService;

    private final LocalDate checkDate = LocalDate.of(1895, 12, 28);
    private static final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        Film newFilm = mapToFilm(filmDto);


        return mapToFilDto(filmStorage.update(newFilm));
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public FilmDto getFilmById(Long filmID) {
        Film film = filmStorage.find(filmID)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с filmID:" + filmID + "не удалось найти :("));
        return mapToFilDto(film);
    }

    public void addLike(Long filmId, Long userId) {
        if (userId == null || !userService.contain(userId)) {
            throw new UserNotFoundException("Пользователя с filmId: " + userId + " не удалось найти :(");
        }
        if (filmId == null || !filmStorage.existById(filmId)) {
            throw new FilmNotFoundException("Фильм с filmId: " + filmId + " не удалось найти :(");
        }

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (userId == null || !userService.contain(userId)) {
            throw new UserNotFoundException("Пользователя не может быть с пустым filmId");
        }
        if (filmId == null || !filmStorage.existById(filmId)) {
            throw new FilmNotFoundException("Фильм с filmId: " + filmId + " не удалось найти :(");
        }

        filmStorage.dislike(filmId, userId);
    }

    public FullFilm getFilmWithGenre(Long filmId) {
        FilmDto filmDto = mapToFilDto(filmStorage.find(filmId)
                .orElseThrow(() -> new FilmNotFoundException("Фильма с таким filmId не существует")));

        Mpa mpa = mpaService.getMpaById((long) filmDto.getMpa().getId());

        List<Genre> genres = Collections.emptyList();
        if (filmDto.getGenres() != null && !filmDto.getGenres().isEmpty()) {
            Set<Long> genreIds = filmDto.getGenres().stream()
                    .map(genre -> (long) genre.getId())
                    .collect(Collectors.toSet());

            List<Genre> genreList = genreService.getGenresByIds(genreIds);

            Map<Integer, Genre> genreMap = genreList.stream()
                    .collect(Collectors.toMap(Genre::getId, genre -> genre));

            genres = filmDto.getGenres().stream()
                    .map(x -> genreMap.get(x.getId()))
                    .filter(Objects::nonNull)
                    .toList();

        }


        List<Director> directors = Collections.emptyList();
        if (filmDto.getDirectors() != null && !filmDto.getDirectors().isEmpty()) {
            directors = directorService.getDirectorsByFilmId(filmId);
        }

        return FullFilm.builder()
                .id(filmDto.getId())
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .duration(filmDto.getDuration())
                .mpa(mpa)
                .releaseDate(filmDto.getReleaseDate())
                .genres(genres)
                .directors(directors)
                .build();
    }

    public boolean contain(Long filmId) {
        return filmStorage.existById(filmId);
    }

    public List<FilmDto> getRecommendation(Long userId) {
        if (userId == null || !userService.contain(userId)) {
            throw new UserNotFoundException("Пользователя не может быть с пустым filmId");
        }
        return filmStorage.getRecommendations(userId).stream()
                .map(FilmService::mapToFilDto)
                .collect(Collectors.toList());
    }

    private Film mapToFilm(FilmDto filmDto) {
        LocalDate date = LocalDate.parse(filmDto.getReleaseDate(), formater);
        if (date.isBefore(checkDate)) {
            throw new DateIsToOldException("Дата не должна быть раньше " + checkDate.format(formater));
        }
        Duration duration = Duration.ofMinutes(filmDto.getDuration());

        if (filmDto.getMpa() == null) {
            throw new MpaNotExistException("Рейтинг не может быть путсым");
        }
        List<Integer> genres;
        if (filmDto.getGenres() == null || filmDto.getGenres().isEmpty()) {
            genres = null;
        } else {
            genres = filmDto.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()).stream().toList();
        }
        List<Long> directors;
        if (filmDto.getDirectors() == null || filmDto.getDirectors().isEmpty()) {
            directors = null;
        } else {
            directors = filmDto.getDirectors().stream().map(Director::getId).collect(Collectors.toSet()).stream().toList();
        }

        return Film.builder()
                .id(filmDto.getId())
                .description(filmDto.getDescription())
                .duration(duration)
                .title(filmDto.getName())
                .releaseDate(date)
                .mpa(filmDto.getMpa().getId())
                .genres(genres)
                .directors(directors)
                .build();
    }

    private static FilmDto mapToFilDto(Film film) {
        List<Genre> genres;
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            genres = Collections.emptyList();
        } else {
            genres = film.getGenres().stream().map(Genre::new).collect(Collectors.toList());
        }
        List<Director> directors;
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            directors = Collections.emptyList();
        } else {
            directors = film.getDirectors().stream().map(Director::new).collect(Collectors.toList());
        }

        return FilmDto.builder()
                .id(film.getId())
                .description(film.getDescription())
                .name(film.getTitle())
                .releaseDate(film.getReleaseDate().format(formater))
                .duration(film.getDuration().getSeconds() / 60)
                .mpa(new Mpa(film.getMpa()))
                .genres(genres)
                .directors(directors)
                .build();
    }


    public List<FilmDto> getFilmsByDirectorId(Long directorId, String sortBy) {
        if (!directorService.existDirector(directorId)) {
            throw new DirectorNotExistException("Директор с id " + directorId + " не найден");
        }
        if (sortBy.equals("year")) {
            return filmStorage.getDirectorFilmSortByYear(directorId).stream()
                    .map(FilmService::mapToFilDto)
                    .collect(Collectors.toList());
        } else if (sortBy.equals("likes")) {
            return filmStorage.getDirectorFilmSortByLikes(directorId).stream()
                    .map(FilmService::mapToFilDto)
                    .collect(Collectors.toList());
        } else {
            throw new SortByNotCorrectException("Выберите сортировку или по году или по количеству лайков year,likes()");
        }
    }

    public List<FilmDto> getPopularFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year).stream()
                .map(FilmService::mapToFilDto)
                .collect(Collectors.toList());
    }
}




