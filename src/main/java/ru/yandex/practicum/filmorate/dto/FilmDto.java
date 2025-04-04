package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Data
@Builder
public class FilmDto {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Описание не может быть больше 200 символов.")
    private String description;
    @NotBlank(message = "Дата выхода не можеть быть пустой.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Дата должна быть в формате yyyy-MM-dd.")
    private String releaseDate;
    @NotNull(message = "Продолжительность не можеть быть пустой.")
    @Positive
    private Long duration;

    private MpaRating mpa;

    private List<Genre> genres;


    @Override
    public String toString() {
        return "FilmDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", duration=" + duration +
                ", mpa='" + mpa + '\'' +
                ", genres=" + genres +
                '}';
    }
}
