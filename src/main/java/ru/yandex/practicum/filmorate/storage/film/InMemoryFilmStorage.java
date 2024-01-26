package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidationException;
import java.util.*;

@Repository
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;
    private Integer filmId;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
        filmId = 0;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {

        if (!films.containsKey(film.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + film.getId() + " не найден.");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Integer id) {

        if (!films.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + id + " не найден.");
        }
        return films.get(id);
    }

    private Integer generateId() {
        return ++filmId;
    }
}
