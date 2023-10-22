package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;
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
        validateFilm(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {

        if (film.getId() == null || !films.containsKey(film.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id = " + film.getId() + " не найден.");
        }
        validateFilm(film);
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

    private void validateFilm(Film film) throws ValidationException {

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
