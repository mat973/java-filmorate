package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.anatation.NotNegative;

@Data
public class ReviewDto {
    Long reviewId;
    @NotNull
    @NotBlank(message = "Отзыв не может быть пустым.")
    String content;
    @NotNull(message = "Тип коментария не может быть пустым.")
    Boolean isPositive;
    @NotNull(message = "Индификатор пользователя не может быть путсым.")
    @NotNegative(message = "Индификатор пользователя должен быть положительным числом.")
    Long userId;
    @NotNull(message = "Индификатор фильма не может быть пустым.")
    @NotNegative(message = "Индификатор фильма должен быть положительным чилсом.")
    Long filmId;
}
