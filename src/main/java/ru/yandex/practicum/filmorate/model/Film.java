package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.dto.Genre;
import ru.yandex.practicum.filmorate.dto.Mpa;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode
public class Film {
    private Long id;
    private String title;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private Mpa mpa;
    private List<Genre> genres;
    private List<Director> directors;
}