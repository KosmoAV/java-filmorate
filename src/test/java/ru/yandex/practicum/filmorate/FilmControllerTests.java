package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidationException;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTests {

    FilmController filmController;

    @BeforeEach
    public void initTests() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    @Test
    public void addFilmWithIncorrectName() {
        Film film = new Film(" ", "description", LocalDate.now().minusDays(10), 120);

        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректное название фильма\"", e.getMessage());

        assertEquals(0, filmController.getFilms().size(), "Неверное количество фильмов");
    }

    @Test
    public void addFilmWithCorrectName() {
        Film film = new Film(" first", "description", LocalDate.now().minusDays(10), 120);

        assertDoesNotThrow(() -> filmController.addFilm(film), "Выброшено исключение");

        assertEquals(1, filmController.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmController.getFilms().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addFilmWithIncorrectDescription() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 20; i++) {
            stringBuilder.append("0123456789");
        }

        stringBuilder.append("!");

        Film film = new Film("first", stringBuilder.toString(), LocalDate.now().minusDays(10), 120);

        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректное описание фильма\"", e.getMessage());

        assertEquals(0, filmController.getFilms().size(), "Неверное количество фильмов");
    }

    @Test
    public void addFilmWithCorrectDescription() {
        Film film = new Film("first", "Descrition first", LocalDate.now().minusDays(10), 120);

        assertDoesNotThrow(() -> filmController.addFilm(film), "Выброшено исключение");

        assertEquals(1, filmController.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmController.getFilms().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addFilmWithIncorrectReleaseDate() {
        LocalDate date = LocalDate.of(1895, 12, 28).minusDays(1);

        Film film = new Film("first", "First",date, 120);

        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректная дата релиза фильма\"", e.getMessage());

        assertEquals(0, filmController.getFilms().size(), "Неверное количество фильмов");
    }

    @Test
    public void addFilmWithCorrectReleaseDate() {
        LocalDate date = LocalDate.of(1895, 12, 28).plusDays(1);

        Film film = new Film("first", "First",date, 120);

        assertDoesNotThrow(() -> filmController.addFilm(film), "Выброшено исключение");

        assertEquals(1, filmController.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmController.getFilms().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addFilmWithIncorrectDuration() {

        Film film1 = new Film("first", "First",LocalDate.now(), 0);
        Film film2 = new Film("first", "First",LocalDate.now(), -1);

        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film1),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректная продолжительность фильма\"", e.getMessage());

        e = assertThrows(ValidationException.class, () -> filmController.addFilm(film2),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректная продолжительность фильма\"", e.getMessage());

        assertEquals(0, filmController.getFilms().size(), "Неверное количество фильмов");
    }

    @Test
    public void addFilmWithCorrectDuration() {

        Film film = new Film("first", "First",LocalDate.now(), 1);

        assertDoesNotThrow(() -> filmController.addFilm(film), "Выброшено исключение");

        assertEquals(1, filmController.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmController.getFilms().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void updateFilmWithIncorrectId() {

        Film film1 = new Film("first", "First",LocalDate.now(), 1);
        assertDoesNotThrow(() -> filmController.addFilm(film1), "Выброшено исключение");

        Film film2 = new Film("Second", "First",LocalDate.now(), 1);

        film2.setId(null);
        ValidationException e = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректный id фильма\"", e.getMessage());

        film2.setId(0);
        e = assertThrows(ValidationException.class, () -> filmController.updateFilm(film2),
                "Исключение ValidationException не выброшено");
        assertEquals("400 BAD_REQUEST \"Некорректный id фильма\"", e.getMessage());

        assertEquals(1, filmController.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmController.getFilms().get(0).getId(), "Некорректный Id");
    }
}