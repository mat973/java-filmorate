package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Boolean existById(Long reviewId);

    Review update(Review review);

    Review find(Long reviewId);

    void delete(Long reviewId);

    List<Review> getReviews(Long count);

    List<Review> getReviews(Long filmId, Long count);

    void addLikeToReview(Long reviewId, Long userId);

    void addDislikeToReview(Long reviewId, Long userId);

    void deleteLikeFromReview(Long reviewId, Long userId);

    void deleteDislikeFromReview(Long reviewId, Long userId);
}
