package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Review {
    Long reviewId;
    @NotNull(message = "не должен быть пустым")
    String content;
    @NotNull(message = "не должен быть пустым")
    Boolean isPositive;
    @NotNull(message = "не должен быть пустым")
    Long userId;
    @NotNull(message = "не должен быть пустым")
    Long filmId;
    Long useful;
}