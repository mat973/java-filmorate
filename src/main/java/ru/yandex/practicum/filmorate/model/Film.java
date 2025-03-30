package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.storage.film.FilmStorageImpl.Rating;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode
public class Film {
    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private Rating rating;

}