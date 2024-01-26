package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationException;

import java.util.List;

public interface UserStorage {

    public abstract User createUser(User user) throws ValidationException;

    public abstract User updateUser(User user) throws ValidationException;

    public abstract List<User> getUsers();

    public abstract List<User> getUsers(Integer...ids);

    public abstract User getUser(Integer id);

    public abstract void addFriend(Integer id, Integer otherId);

    public abstract void deleteFriend(Integer id, Integer otherId);

    public abstract List<User> getCommonFriends(Integer id, Integer otherId);
}

