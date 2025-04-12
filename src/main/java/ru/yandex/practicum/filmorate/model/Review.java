package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Review {
    Long reviewId;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Long useful;
}