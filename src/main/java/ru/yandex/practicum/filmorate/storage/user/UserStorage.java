package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationException;

import java.util.List;

public interface UserStorage {

    public abstract User createUser(User user) throws ValidationException;

    public abstract User updateUser(User user) throws ValidationException;

    public abstract List<User> getUsers();

    public abstract User getUser(Integer id);
}

