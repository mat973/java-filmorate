package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private Integer mpa;
    private List<Integer> genres;
    private String directors;
}