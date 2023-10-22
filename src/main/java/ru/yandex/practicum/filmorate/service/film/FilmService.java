package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer id, Integer userId) {
        Film film = filmStorage.getFilm(id);

        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + " не найден.");
        }

        if (userStorage.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден.");
        }

        film.getLikes().add(userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        Film film = filmStorage.getFilm(id);

        if (film == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + " не найден.");
        }

        if (userStorage.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден.");
        }

        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .collect(Collectors.toMap(Film::getId, film -> film.getLikes().size()))
                .entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .map(filmStorage::getFilm)
                .limit(count)
                .collect(Collectors.toList());
    }
}
