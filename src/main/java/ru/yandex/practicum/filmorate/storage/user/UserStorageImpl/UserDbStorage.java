package ru.yandex.practicum.filmorate.storage.user.UserStorageImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAddFriendException;
import ru.yandex.practicum.filmorate.exception.UserDeleteFriendException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component("user-bd")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> mapper;

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birth_day) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birth_day = ? WHERE user_id = ?";
    private static final String EXIST_BY_ID_QUERY = "SELECT COUNT(*) > 0 FROM users WHERE user_id = ?";

    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id, confirmed) VALUES (?, ?, FALSE)";
    private static final String CONFIRM_FRIEND_QUERY = "UPDATE friends SET confirmed = TRUE WHERE user_id = ? AND friend_id = ? AND confirmed = FALSE";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ? AND confirmed = FALSE";
    private static final String UPDATE_FRIEND_STATUS_QUERY = """
            UPDATE friends
            SET
                 confirmed = FALSE,
                 user_id = friend_id,
                 friend_id = user_id WHERE user_id = ? AND friend_id = ? AND confirmed = TRUE""";
    private static final String GET_FRIENDS_QUERY = """
            SELECT u.*
            FROM users u
            JOIN friends f ON u.user_id = f.friend_id OR u.user_id = f.user_id
            WHERE (f.user_id = ? OR (f.friend_id = ? AND confirmed = TRUE))
            AND u.user_id != ?
            """;
    private static final String CHECK_FRIEND_QUERY = "SELECT confirmed FROM friends WHERE (user_id = ? AND friend_id = ?)";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE user_id = ?";

    public List<User> getAllUsers() {
        return jdbc.query(FIND_ALL_QUERY, mapper);
    }

    public Optional<User> find(Long userId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, userId));
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean existById(Long userId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(EXIST_BY_ID_QUERY, Boolean.class, userId));
    }

    public User save(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    public User update(User user) {
        jdbc.update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public void addFriendById(Long userId, Long friendId) {
        Optional<Boolean> isFriend = isFriend(userId, friendId);  // Проверяем, есть ли дружба или запрос

        if (isFriend.isEmpty()) {
            jdbc.update(ADD_FRIEND_QUERY, userId, friendId);
        } else if (isFriend.get().equals(Boolean.FALSE)) {
            Optional<Boolean> isFriendBack = isFriend(friendId, userId);
            if (isFriendBack.isEmpty() || isFriendBack.get().equals(Boolean.FALSE)) {
                throw new UserAddFriendException("Вы уже отправили запрос другу id = " + friendId +
                        ", дождитесь подтверждения с его стороны");
            } else {
                jdbc.update(CONFIRM_FRIEND_QUERY, friendId, userId);
            }
        } else {
            throw new UserAddFriendException("Вы уже являетесь этим пользователем друзьями");
        }
    }

    @Override
    public void deleteFriendById(Long userId, Long friendId) {
        Optional<Boolean> confirmedByUser = isFriend(userId, friendId);
        Optional<Boolean> confirmedByFriend = isFriend(friendId, userId);
        if (confirmedByUser.isEmpty() && confirmedByFriend.isEmpty()) {
            throw new UserDeleteFriendException("Вы не являетесь другом с этим пользователем и" +
                    " не отправляли запросы на дружбу");
        } else if (confirmedByUser.isPresent()) {
            if (confirmedByUser.get().equals(Boolean.FALSE)) {
                jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
            } else {
                jdbc.update(UPDATE_FRIEND_STATUS_QUERY, userId, friendId);
            }
        } else {
            if (confirmedByFriend.get().equals(Boolean.FALSE)) {
                throw new UserDeleteFriendException("Вы не являетесь другом с этим пользователем и" +
                        " не отправляли запросы на дружбу");
            } else {
                jdbc.update(UPDATE_FRIEND_STATUS_QUERY, friendId, userId);
            }
        }
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        return jdbc.query(GET_FRIENDS_QUERY, mapper, userId, userId, userId);
    }

    private Optional<Boolean> isFriend(Long userId, Long friendId) {
        try {
            return Optional.of(jdbc.queryForObject(CHECK_FRIEND_QUERY, Boolean.class, userId, friendId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        if (existById(userId)) {
            jdbc.update(DELETE_USER_QUERY, userId);
            log.info("Пользователь с id = {} удален", userId);
        } else {
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }
}