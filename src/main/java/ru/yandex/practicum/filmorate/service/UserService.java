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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final InMemoryUserStorage inMemoryUserStorage;

    public User createUser(UserDto userDto) {
        return inMemoryUserStorage.save(mapToUser(userDto));
    }

    public User upDateUser(@Valid UserDto userDto) {
        Long id = userDto.getId();
        if (id == null || !inMemoryUserStorage.existById(id)) {
            String errorMessage = "Пользователь " + (id == null ? "должен иметь id." : "с id " + id + " не существует.");
            log.warn("Ошибка при обновлении пользователя: {}", errorMessage);
            throw new UserNotFoundException(errorMessage);
        }
        User oldUser = inMemoryUserStorage.find(userDto.getId()).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + id + " не найден."
        ));
        User newUser = mapToUser(userDto);
        newUser.setFriends(oldUser.getFriends());
        return inMemoryUserStorage.update(newUser);
    }


    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        return inMemoryUserStorage.find(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с id:" + id + "не удалось найти :("));
    }

    public void addFriendById(Long id, Long friendId) {
        if (id == null || !inMemoryUserStorage.existById(id)) {
            throw new UserNotFoundException("Пользователя с id: " + id + " не удалось найти :(");
        }
        User user = inMemoryUserStorage.find(id).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + id + " не найден."
        ));

        if (friendId == null || !inMemoryUserStorage.existById(friendId)) {
            throw new UserNotFoundException("Пользователя с id: " + id + " не удалось найти :(");
        }
        User friend = inMemoryUserStorage.find(friendId).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + friendId + " не найден."
        ));
        if (friend.equals(user)) {
            throw new FriendsException("Вы не можете добавить сами себя в друзья");
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriendById(Long id, Long friendId) {
        if (id == null || !inMemoryUserStorage.existById(id)) {
            throw new UserNotFoundException("Пользователя с id: " + id + " не удалось найти :(");
        }
        User user = inMemoryUserStorage.find(id).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + id + " не найден."
        ));

        if (friendId == null || !inMemoryUserStorage.existById(friendId)) {
            throw new UserNotFoundException("Пользователя с id: " + id + " не удалось найти :(");
        }
        User friend = inMemoryUserStorage.find(friendId).orElseThrow(() -> new UserNotFoundException(
                "Пользователь с id: " + id + " не найден."
        ));
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getAllFriends(Long id) {
        if (id == null || inMemoryUserStorage.find(id).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id: " + id + " не удалось найти :(");
        }
        User user = inMemoryUserStorage.find(id).get();
        return user.getFriends().stream()
                .map(x -> inMemoryUserStorage.find(x).orElseThrow(() -> new UserNotFoundException("Друг с id: "
                        + x + " не найден")))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        if (id == null || inMemoryUserStorage.find(id).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id: " + id + " не удалось найти :(");
        }
        User user = inMemoryUserStorage.find(id).get();

        if (otherId == null || inMemoryUserStorage.find(otherId).isEmpty()) {
            throw new UserNotFoundException("Пользователя с id: " + id + " не удалось найти :(");
        }
        User otherUser = inMemoryUserStorage.find(otherId).get();

        Set<Long> userSetFriends = user.getFriends();
        Set<Long> otherUserSetFriends = otherUser.getFriends();

        Set<Long> intersection = userSetFriends.stream()
                .filter(otherUserSetFriends::contains)
                .collect(Collectors.toSet());
        return intersection.stream()
                .map(x -> inMemoryUserStorage.find(x).orElseThrow(() -> new UserNotFoundException(
                        "пользователь с id: " + x + " не найде :(")))
                .collect(Collectors.toList());
    }

    public boolean contain(Long id) {
        return inMemoryUserStorage.existById(id);
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


}
