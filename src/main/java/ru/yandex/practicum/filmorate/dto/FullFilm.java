package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FullFilm {
    private Long id;
    private String name;
    private String description;
    private String releaseDate;
    private Long duration;
    private Mpa mpa;
    private List<Genre> genres;
}
