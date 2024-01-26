package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationException;

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
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + user.getId() + " не найден.");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> getUsers(Integer...ids) {
        return null;
    }

    @Override
    public void addFriend(Integer id, Integer otherId) {

    }

    @Override
    public void deleteFriend(Integer id, Integer otherId) {

    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) {
        return null;
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
}