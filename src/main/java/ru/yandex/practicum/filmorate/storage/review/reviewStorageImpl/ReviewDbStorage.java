package ru.yandex.practicum.filmorate.storage.review.reviewStorageImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.PreparedStatement;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper rowMapper;


    private static final String CREATE_REVIEW_QUERY =
            "INSERT INTO review (content, positive, user_id, film_id) VALUES (?, ?, ?, ?)";
    private final static String EXIST_BY_ID_QUERY =
            "SELECT COUNT(*) > 0 FROM review WHERE film_id = ?";
    private static final String UPDATE_QUERY = "UPDATE review SET CONTENT = ?, positive = ? where  review_id = ?";
    private static final String GET_BY_ID = """
            SELECT
                r.review_id,
                r.content,
                r.positive,
                r.user_id,
                r.film_id,
                COALESCE(SUM(CASE
                    WHEN rs.isPositive = TRUE THEN 1
                    WHEN rs.isPositive = FALSE THEN -1
                    ELSE 0
                END), 0) AS usefull
            FROM
                review r
            LEFT JOIN
                review_score rs ON r.review_id = rs.review_id
            WHERE
                r.review_id = ?
            GROUP BY
                r.review_id, r.content, r.positive, r.user_id, r.film_id
            """;
    private static final String GET_ALL_WITH_LIMIT_QUERY = """
            SELECT r.review_id,
                   r.content,
                   r.positive
                   r.user_id
                   r.film_id
                   COALESCE(SUM(CASE
                        WHEN rs.isPositive = TRUE THEN 1
                        WHEN rs.isPositive = FALSE THEN -1
                        ELSE 0
                        END), 0) AS usefull
                   FROM
                       review r
                   LEFT JOIN
                       review_score rs ON r.review_id = rs.review_id
                   GROUP BY
                       r.review_id, r.content, r.positive, r.user_id, r.film_id;
                   LIMIT ?
            """;

    private static final String GET_ALL_WITH_LIMIT_AND_FILM_ID_QUERY = """
            SELECT r.review_id,
                   r.content,
                   r.positive
                   r.user_id
                   r.film_id
                   COALESCE(SUM(CASE
                        WHEN rs.isPositive = TRUE THEN 1
                        WHEN rs.isPositive = FALSE THEN -1
                        ELSE 0
                        END), 0) AS usefull
                   FROM
                       review r
                   WHERE
                       r.film_id = ?
                   LEFT JOIN
                       review_score rs ON r.review_id = rs.review_id
                   GROUP BY
                       r.review_id, r.content, r.positive, r.user_id, r.film_id;
                   LIMIT ?
            """;

    private static final String ADD_LIKE_QUERY = """
            MERGE INTO review_score (review_id, user_id, isPositive)
            KEY (review_id, user_id)
            VALUES (?, ?, TRUE);
            """;

    private static final String ADD_DISLIKE_QUERY = """
                         MERGE INTO review_score (review_id, user_id, isPositive)
            KEY (review_id, user_id)
            VALUES (?, ?, FALSE)
            """;

    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM review_score WHERE review_id = ? AND user_id = ? AND isPositive = TRUE
            """;

    private static final String DELETE_DISLIKE_QUERY = """
            DELETE FROM review_score WHERE review_id = ? AND user_id = ? AND isPositive = FALSE
            """;

    private static final String DELETE_BY_ID_QUERY = "DELETE FROM review WHERE review_id = ?";

    @Override
    public Review create(Review review) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(CREATE_REVIEW_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        review.setReviewId((Long) keyHolder.getKey());
        return review;
    }

    @Override
    public Boolean existById(Long reviewId) {
        return jdbc.queryForObject(EXIST_BY_ID_QUERY, Boolean.class, reviewId);
    }

    @Override
    public Review update(Review review) {
        int updated = jdbc.update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return find(review.getReviewId());
    }

    @Override
    public Review find(Long reviewId) {
        return jdbc.queryForObject(GET_BY_ID, rowMapper, reviewId);
    }

    @Override
    public void delete(Long reviewId) {
        jdbc.update(DELETE_BY_ID_QUERY, reviewId);
    }

    @Override
    public List<Review> getReviews(Long count) {
        return jdbc.query(GET_ALL_WITH_LIMIT_QUERY, rowMapper, count);
    }

    @Override
    public List<Review> getReviews(Long filmId, Long count) {
        return jdbc.query(GET_ALL_WITH_LIMIT_AND_FILM_ID_QUERY, rowMapper, filmId, count);
    }

    @Override
    public void addLikeToReview(Long reviewId, Long userId) {
        jdbc.update(ADD_LIKE_QUERY, reviewId, userId);
    }


    public void addDislikeToReview(Long reviewId, Long userId) {
        jdbc.update(ADD_DISLIKE_QUERY, reviewId, userId);
    }

    @Override
    public void deleteLikeFromReview(Long reviewId, Long userId) {
        jdbc.update(DELETE_LIKE_QUERY, reviewId, userId);
    }

    @Override
    public void deleteDislikeFromReview(Long reviewId, Long userId) {
        jdbc.update(DELETE_DISLIKE_QUERY, reviewId, userId);
    }


}
