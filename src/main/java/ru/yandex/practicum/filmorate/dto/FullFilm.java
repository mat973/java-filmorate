package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;

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
    private List<Director> directors;
}
