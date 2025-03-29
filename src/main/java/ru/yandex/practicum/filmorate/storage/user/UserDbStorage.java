package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

public class UserDbStorage extends BaseRepository<User> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(username, email, password, registration_date)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE users SET username = ?, email = ?, password = ? WHERE id = ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, Class<User> entityType) {
        super(jdbc, mapper, entityType);
    }


    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public User save(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                Timestamp.from(user.getRegistrationDate())
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getId()
        );
        return user;
    }
}
