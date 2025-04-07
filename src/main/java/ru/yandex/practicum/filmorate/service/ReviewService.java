package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Review create(ReviewDto reviewDto){
        if (!userService.contain(reviewDto.getUserId())) {
            throw new UserNotFoundException("Пользоваетль с таким id " + reviewDto.getUserId() + " не найден!");
        }
        if (!filmService.contain(reviewDto.getFilmId())){
            throw new FilmNotFoundException("Фильм с такм id " + reviewDto.getFilmId() + " не сущесвует.");
        }
        return reviewStorage.create(mapReviewDtoToReview(reviewDto));
    }

    private Review mapReviewDtoToReview(ReviewDto reviewDto) {
        return Review.builder()
                .content(reviewDto.getContent())
                .userId(reviewDto.getUserId())
                .filmId(reviewDto.getFilmId())
                .isPositive(reviewDto.getIsPositive())
                .useful(0L)
                .build();
    }
}
