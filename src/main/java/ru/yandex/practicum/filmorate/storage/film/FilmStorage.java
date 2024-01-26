package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidationException;

import java.util.List;

public interface FilmStorage {

    public abstract Film addFilm(Film film) throws ValidationException;

    public abstract Film updateFilm(Film film) throws ValidationException;

    public abstract List<Film> getFilms();

    public abstract Film getFilm(Integer id);

    public abstract void addLike(Integer id, Integer userId);

    public abstract void deleteLike(Integer id, Integer userId);

    public List<Film> getPopularFilms(Integer count);
}
