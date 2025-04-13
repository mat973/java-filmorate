package ru.yandex.practicum.filmorate.service;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exeption.DateNotExistException;
import ru.yandex.practicum.filmorate.exeption.FriendsException;
import ru.yandex.practicum.filmorate.exeption.LoginContainSpaceException;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    public User createUser(UserDto userDto) {
        return userStorage.save(mapToUser(userDto));
    }


    public User upDateUser(@Valid UserDto userDto) {
        Long id = userDto.getId();
        if (id == null || !userStorage.existById(id)) {
            String errorMessage = "Пользователь " + (id == null ? "должен иметь id." : "с id " + id + " не существует.");
            log.warn("Ошибка при обновлении пользователя: {}", errorMessage);
            throw new UserNotFoundException(errorMessage);
        }
        User oldUser = userStorage.find(userDto.getId()).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + id + " не найден."
        ));
        User newUser = mapToUser(userDto);
        return userStorage.update(newUser);
    }


    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return userStorage.find(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id:" + id + "не удалось найти :("));
    }

    public void addFriendById(Long userId, Long friendId) {
        if (userId == null || !userStorage.existById(userId)) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }
        User user = userStorage.find(userId).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + userId + " не найден."
        ));

        if (friendId == null || !userStorage.existById(friendId)) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }
        User friend = userStorage.find(friendId).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + friendId + " не найден."
        ));
        if (friend.equals(user)) {
            throw new FriendsException("Вы не можете добавить сами себя в друзья");
        }
        userStorage.addFriendById(userId, friendId);
    }

    public void deleteFriendById(Long userId, Long friendId) {
        if (userId == null || !userStorage.existById(userId)) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }
        User user = userStorage.find(userId).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + userId + " не найден."
        ));

        if (friendId == null || !userStorage.existById(friendId)) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }
        User friend = userStorage.find(friendId).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + userId + " не найден."
        ));
        userStorage.deleteFriendById(userId, friendId);
    }

    public List<User> getAllFriends(Long userId) {
        if (userId == null || userStorage.find(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }
        return userStorage.getAllFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        if (userId == null || userStorage.find(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }


        if (otherUserId == null || userStorage.find(otherUserId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }


        Set<User> userSetFriends = new HashSet<>(userStorage.getAllFriends(userId));
        Set<User> otherUserSetFriends = new HashSet<>(userStorage.getAllFriends(otherUserId));

        return userSetFriends.stream()
                .filter(otherUserSetFriends::contains).distinct().collect(Collectors.toList());
    }


    public boolean contain(Long id) {
        return userStorage.existById(id);
    }

    private User mapToUser(UserDto userDto) {
        if (containSpace(userDto.getLogin())) {
            log.warn("Логин содержит пробелы: {}", userDto.getLogin());
            throw new LoginContainSpaceException();
        }

        log.info("Парсинг даты рождения: {}", userDto.getBirthday());
        LocalDate birthday = LocalDate.parse(userDto.getBirthday(), formater);

        if (birthday.isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть позже текущей даты: {}", birthday);
            throw new DateNotExistException("Дата рождения не может быть позже текущей даты.");
        }

        if (userDto.getName() == null) {
            log.info("Имя пользователя не указано, используется логин: {}", userDto.getLogin());
            userDto.setName(userDto.getLogin());
        }

        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                .name(userDto.getName())
                .birthday(birthday)
                .build();
    }

    private boolean containSpace(String s) {
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                log.debug("Найден пробел в строке: {}", s);
                return true;
            }
        }
        return false;
    }

    public void createEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        eventStorage.saveEvent(userId, eventType, operation, entityId);
    }

    public List<Event> getUserEvents(Long userId) {
        if (userId == null || !userStorage.existById(userId)) {
            throw new UserNotFoundException("Пользователя с id: " + userId + " не удалось найти :(");
        }
        return eventStorage.getUserEvents(userId);

    }
}
