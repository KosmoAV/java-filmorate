package ru.yandex.practicum.filmorate.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films;

    private Integer filmId;

    public FilmController() {
        films = new HashMap<>();
        filmId = 0;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        log.info("Получен запрос на добавление фильма");
        validateFilm(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        log.info("Получен запрос на обновление фильма");
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на получение всех фильмов");
        return new ArrayList<>(films.values());
    }

    private Integer generateId () {
        return ++filmId;
    }

    public void validateFilm(Film film) throws ValidationException {

        if (film.getName() == null || film.getDescription() == null || film.getReleaseDate() == null ||
            film.getDuration() == null) {

            log.error("Выброшено исключение");
            throw new ValidationException("Поля фильма не инициализированы");
        }

        if (film.getId() != null && !films.containsKey(film.getId())) {
            log.error("Выброшено исключение");
            throw new ValidationException("Некорректный Id фильма");
        }

        if (film.getName().isBlank()) {
            log.error("Выброшено исключение");
            throw new ValidationException("Некорректное название фильма");
        }

        if (film.getDescription().length() > 200) {
            log.error("Выброшено исключение");
            throw new ValidationException("Некорректное описание фильма");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Выброшено исключение");
            throw new ValidationException("Некорректная дата релиза фильма");
        }

        if (!(film.getDuration() > 0)) {
            log.error("Выброшено исключение");
            throw new ValidationException("Некорректная продолжительность фильма");
        }
    }
}
