package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilmDto {
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    String releaseDate;
    @NotNull
    Long duration;
}
