package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;
    private Integer userId;

    public InMemoryUserStorage() {
        users = new HashMap<>();
        userId = 0;
    }

    @Override
    public User createUser(User user) throws ValidationException {
        validateUser(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + user.getId() + " не найден.");
        }
        validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Integer id) {

        if (!users.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден.");
        }
        return users.get(id);
    }


    private Integer generateId() {
        return ++userId;
    }

    private void validateUser(User user) throws ValidationException {

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
