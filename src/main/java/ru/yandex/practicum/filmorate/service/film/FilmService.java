package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidationException;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;
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

    public Film addFilm(Film film) throws ValidationException {
        validateFilm(film);
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        if (film.getId() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный id фильма");
        }
        validateFilm(film);
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Integer id) {

        if (id == null || id < 1) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный id фильма");
        }
        return filmStorage.getFilm(id);
    }

    public void addLike(Integer id, Integer userId) {

        if (id == null || id < 1) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный id фильма");
        }

        if (userId == null || userId < 1) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный id пользователя");
        }

        Film film = filmStorage.getFilm(id);

        if (userStorage.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден");
        }

        film.getLikes().add(userId);
    }

    public void deleteLike(Integer id, Integer userId) {

        if (id == null || id < 1) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный id фильма");
        }

        if (userId == null || userId < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный id пользователя");
        }

        Film film = filmStorage.getFilm(id);

        if (userStorage.getUser(userId) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + id + " не найден");
        }

        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {

        if (count == null || count < 1) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректное количество популярных фильмов");
        }

        return filmStorage.getFilms().stream()
                .collect(Collectors.toMap(Film::getId, film -> film.getLikes().size()))
                .entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .map(filmStorage::getFilm)
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) throws ValidationException {

        if (film.getId() != null && film.getId() < 1) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректный id фильма");
        }

        if (film.getName() == null || film.getDescription() == null || film.getReleaseDate() == null ||
                film.getDuration() == null) {

            throw new ValidationException(HttpStatus.BAD_REQUEST, "Поля фильма не инициализированы");
        }

        if (film.getName().isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректное название фильма");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректное описание фильма");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректная дата релиза фильма");
        }

        if (!(film.getDuration() > 0)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Некорректная продолжительность фильма");
        }
    }
}
