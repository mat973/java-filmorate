package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Review create(ReviewDto reviewDto) {

        return reviewStorage.create(mapReviewDtoToReview(reviewDto));
    }

    public Review update(@Valid ReviewDto reviewDto, Long reviewId) {
        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыва с таким id " + reviewId + " не существует");
        }
        if (!userService.contain(reviewDto.getUserId())) {
            throw new UserNotFoundException("Пользоваетль с таким id " + reviewDto.getUserId() + " не найден!");
        }
        if (!filmService.contain(reviewDto.getFilmId())) {
            throw new FilmNotFoundException("Фильм с такм id " + reviewDto.getFilmId() + " не сущесвует.");
        }

        Review review = mapReviewDtoToReview(reviewDto);
        review.setReviewId(reviewId);
        return reviewStorage.update(review);
    }

    public long deleteReview(Long reviewId) {
        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыва с таким id " + reviewId + " не существует");
        }
        Long userId = reviewStorage.getUserIdFromReview(reviewId);
        reviewStorage.delete(reviewId);
        return userId;
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

    public Review findById(Long reviewId) {
        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыва с таким id " + reviewId + " не существует");
        }
        return reviewStorage.find(reviewId);
    }

    public List<Review> getReviews(Long fId, Long amount) {
        if (fId != -1) {
            return reviewStorage.getReviews(amount);
        } else {
            return reviewStorage.getReviews(fId, amount);
        }

    }

    public void addLikeToReview(Long reviewId, Long userId) {
        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыва с таким id " + reviewId + " не существует");
        }
        if (!userService.contain(userId)) {
            throw new UserNotFoundException("Пользователь с таким id " + userId + " не найден!");
        }

        reviewStorage.addLikeToReview(reviewId, userId);

    }

    public void addDislikeToReview(Long reviewId, Long userId) {
        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыва с таким id " + reviewId + " не существует");
        }
        if (!userService.contain(userId)) {
            throw new UserNotFoundException("Пользователь с таким id " + userId + " не найден!");
        }

        reviewStorage.addDislikeToReview(reviewId, userId);
    }

    public void deleteLikeFromReview(Long reviewId, Long userId) {
        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыва с таким id " + reviewId + " не существует");
        }
        if (!userService.contain(userId)) {
            throw new UserNotFoundException("Пользователь с таким id " + userId + " не найден!");
        }

        reviewStorage.deleteLikeFromReview(reviewId, userId);
    }

    public void deleteDisLikeFromReview(Long reviewId, Long userId) {
        if (!reviewStorage.existById(reviewId)) {
            throw new ReviewNotFoundException("Отзыва с таким id " + reviewId + " не существует");
        }
        if (!userService.contain(userId)) {
            throw new UserNotFoundException("Пользователь с таким id " + userId + " не найден!");
        }

        reviewStorage.deleteDislikeFromReview(reviewId, userId);
    }

}
