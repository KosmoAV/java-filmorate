package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer id, Integer friendId) {

        existsUser(id, friendId);
        userStorage.getUser(id).getFriends().add(friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {

        existsUser(id, friendId);
        userStorage.getUser(id).getFriends().remove(friendId);
    }

    public List<User> getFriends(Integer id) {
        existsUser(id);
        return userStorage.getUser(id).getFriends().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        existsUser(id, otherId);
        return userStorage.getUser(id).getFriends().stream()
                .filter(userStorage.getUser(otherId).getFriends()::contains)
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    private void existsUser(Integer...usersId) {

        for (Integer userId : usersId) {
            if (userStorage.getUser(userId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + userId + " не найден.");
            }
        }
    }
}
