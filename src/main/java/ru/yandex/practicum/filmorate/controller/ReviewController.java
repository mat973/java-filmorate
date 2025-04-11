package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Review createReview(@Valid @RequestBody ReviewDto reviewDto) {
        log.info("Начался запрос о созании нового отзыва.");
        return reviewService.create(reviewDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review updateReview(@Valid @RequestBody ReviewDto reviewDto) {
        log.info("Обновления отзыва с id {}", reviewDto.getReviewId());
        return reviewService.update(reviewDto, reviewDto.getReviewId());
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long reviewId) {
        log.info("Удаление review с id {}", reviewId);
        reviewService.deleteReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public Review findById(@PathVariable Long reviewId) {
        log.info("Получение review с id {}", reviewId);
        return reviewService.findById(reviewId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Review> getReviews(@RequestParam Optional<Long> filmId, @RequestParam Optional<Long> count) {
        log.info("Вывод комментариев по входным критериям");
        Long fId;
        Long amount;
        if (filmId.isEmpty()) {
            fId = -1L;
        } else {
            fId = filmId.get();
        }

        if (count.isEmpty()) {
            amount = 10L;
        } else {
            amount = count.get();
        }
        return reviewService.getReviews(fId, amount);
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
