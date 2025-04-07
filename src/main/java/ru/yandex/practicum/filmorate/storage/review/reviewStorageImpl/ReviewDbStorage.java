package ru.yandex.practicum.filmorate.storage.review.reviewStorageImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.PreparedStatement;

@RequiredArgsConstructor
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbc;
    private final ReviewRowMapper rowMapper;


    private final String CREATE_REVIEW_QUERY =
            "INSERT INTO review (content, positive, user_id, film_id) VALUES (?, ?, ?, ?)";
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



}
