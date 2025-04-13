package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Event> rowMapper;

    private static final String INSERT_EVENT_QUERY = "INSERT INTO user_event(user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)";
    private static final String GET_USER_EVENT_QUERY = "SELECT * FROM user_event WHERE user_id = ?";

    public void saveEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        jdbc.update(INSERT_EVENT_QUERY, userId, eventType.getTitle(), operation.getTitle(), entityId);
    }

    public List<Event> getUserEvents(Long userId) {
        return jdbc.query(GET_USER_EVENT_QUERY, rowMapper, userId);
    }
}