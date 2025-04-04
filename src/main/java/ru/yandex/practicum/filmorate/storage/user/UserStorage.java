package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User save(User user);

    User update(User user);

    Optional<User> find(Long userId);

    List<User> getAllUsers();

    Boolean existById(Long userId);

    void addFriendById(Long userId, Long friendId);

    void deleteFriendById(Long userId, Long friendId);

    List<User> getAllFriends(Long userId);
}
