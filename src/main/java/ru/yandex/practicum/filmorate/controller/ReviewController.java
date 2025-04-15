package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Review createReview(@Valid @RequestBody ReviewDto reviewDto) {
        log.info("Начался запрос о созании нового отзыва.");
        Review review = reviewService.create(reviewDto);
        userService.createEvent(review.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId());
        return review;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review updateReview(@Valid @RequestBody ReviewDto reviewDto) {
        log.info("Обновления отзыва с id {}", reviewDto.getReviewId());
        Review review = reviewService.update(reviewDto, reviewDto.getReviewId());
        userService.createEvent(review.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getReviewId());
        return review;
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long reviewId) {
        log.info("Удаление review с id {}", reviewId);
        Long userId = reviewService.deleteReview(reviewId);
        userService.createEvent(userId, EventType.REVIEW, Operation.REMOVE, reviewId);
    }

    @GetMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public Review findById(@PathVariable Long reviewId) {
        log.info("Получение review с id {}", reviewId);
        return reviewService.findById(reviewId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getReviews(
            @RequestParam(name = "filmId", required = false, defaultValue = "-1") Long filmId,
            @RequestParam(name = "count", required = false, defaultValue = "10") Long count
    ) {
        log.info("Вывод комментариев по входным критериям: filmId={}, count={}", filmId, count);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLikeToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Запрос на оставление лайка от пользователя с UserId {} на отзыв с reviewId {}", userId, reviewId);
        reviewService.addLikeToReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislikeToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Запрос на установление дизлайка ползователм с userId {} на по сто с reviewId {}", userId, reviewId);
        reviewService.addDislikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Запрос на удаление лайка пользователя userId {} на пост reviewId {}", userId, reviewId);
        reviewService.deleteLikeFromReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDislikeToReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        log.info("Запрос на удаление дизлайка пользователя userId {} на пост reviewId {}", userId, reviewId);
        reviewService.deleteDisLikeFromReview(reviewId, userId);
    }

}
