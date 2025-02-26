package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> userMap = new HashMap<>();
    private Long currentId = 1L;

    @Override
    public User save(User user) {
        user.setId(currentId++);
        user.setFriends(new HashSet<>());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> find(Long id) {
        if (!userMap.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(userMap.get(id));
    }


    public List<User> getAllUsers() {
        return userMap.values().stream().toList();
    }

    public boolean containUser(Long id) {
        return userMap.containsKey(id);
    }
}
