package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmDto {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Описание не может быть больше 200 символов.")
    private String description;
    @NotBlank(message = "Дата выхода не можеть быть пустой.")
    String releaseDate;
    @NotNull(message = "Продолжительность не можеть быть пустой.")
    @Positive
    Long duration;
}
