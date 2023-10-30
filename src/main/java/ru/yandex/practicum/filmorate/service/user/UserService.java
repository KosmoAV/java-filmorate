package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) throws ValidationException {
        validateUser(user);
        userStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) throws ValidationException {
        if (user.getId() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный id пользователя");
        }
        validateUser(user);
        userStorage.updateUser(user);
        return user;
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(Integer id) {

        if (id == null || id < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id пользователя");
        }
        return userStorage.getUser(id);
    }

    public void addFriend(Integer id, Integer friendId) {

        if (id == null || id < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id пользователя");
        }

        if (friendId == null || friendId < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id друга");
        }

        userStorage.getUser(id).getFriends().add(friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {

        if (id == null || id < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id пользователя");
        }

        if (friendId == null || friendId < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id друга");
        }

        userStorage.getUser(id).getFriends().remove(friendId);
    }

    public List<User> getFriends(Integer id) {

        if (id == null || id < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id пользователя");
        }

        return userStorage.getUser(id).getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {

        if (id == null || id < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id первого пользователя");
        }

        if (otherId == null || otherId < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id второго пользователя");
        }

        return userStorage.getUser(id).getFriends().stream()
                .filter(userStorage.getUser(otherId).getFriends()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) throws ValidationException {

        if (user.getId() != null && user.getId() < 1) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный id пользователя");
        }

        if (user.getEmail() == null || user.getLogin() == null || user.getBirthday() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Поля пользователя не инициализированы");
        }

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный e-mail");
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный логин");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректная дата рождения");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
