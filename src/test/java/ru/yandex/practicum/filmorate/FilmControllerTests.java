package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Controllers.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTests {

    FilmController filmController;

    @BeforeEach
    public void initTests() {
        filmController = new FilmController();
    }

    @Test
    public void addFilmWithIncorrectName() {
        Film film = new Film(" ", "description", LocalDate.now().minusDays(10), 120);

        try {
            filmController.addFilm(film);
        } catch (ValidationException exception) {
           assertEquals("Некорректное название фильма", exception.getMessage());
        }

        assertEquals(0, filmController.getFilms().size(), "Неверное количество фильмов");
    }

    @Test
    public void addFilmWithCorrectName() {
        Film film = new Film(" first", "description", LocalDate.now().minusDays(10), 120);

        try {
            filmController.addFilm(film);
        } catch (ValidationException exception) {
            assertNull(exception, "Неожиданное исключение");
        }

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

        try {
            filmController.addFilm(film);
        } catch (ValidationException exception) {
            assertEquals("Некорректное описание фильма", exception.getMessage());
        }

        assertEquals(0, filmController.getFilms().size(), "Неверное количество фильмов");
    }

    @Test
    public void addFilmWithCorrectDescription() {
        Film film = new Film("first", "Descrition first", LocalDate.now().minusDays(10), 120);

        try {
            filmController.addFilm(film);
        } catch (ValidationException exception) {
            assertNull(exception, "Неожиданное исключение");
        }

        assertEquals(1, filmController.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmController.getFilms().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addFilmWithIncorrectReleaseDate() {
        LocalDate date = LocalDate.of(1895, 12, 28).minusDays(1);

        Film film = new Film("first", "First",date, 120);

        try {
            filmController.addFilm(film);
        } catch (ValidationException exception) {
            assertEquals("Некорректная дата релиза фильма", exception.getMessage());
        }

        assertEquals(0, filmController.getFilms().size(), "Неверное количество фильмов");
    }

    @Test
    public void addFilmWithCorrectReleaseDate() {
        LocalDate date = LocalDate.of(1895, 12, 28).plusDays(1);

        Film film = new Film("first", "First",date, 120);

        try {
            filmController.addFilm(film);
        } catch (ValidationException exception) {
            assertNull(exception, "Неожиданное исключение");
        }

        assertEquals(1, filmController.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmController.getFilms().get(0).getId(), "Некорректный Id");
    }

    @Test
    public void addFilmWithIncorrectDuration() {

        Film film1 = new Film("first", "First",LocalDate.now(), 0);
        Film film2 = new Film("first", "First",LocalDate.now(), -1);

        try {
            filmController.addFilm(film1);
        } catch (ValidationException exception) {
            assertEquals("Некорректная продолжительность фильма", exception.getMessage());
        }

        try {
            filmController.addFilm(film2);
        } catch (ValidationException exception) {
            assertEquals("Некорректная продолжительность фильма", exception.getMessage());
        }

        assertEquals(0, filmController.getFilms().size(), "Неверное количество фильмов");
    }

    @Test
    public void addFilmWithCorrectDuration() {

        Film film = new Film("first", "First",LocalDate.now(), 1);

        try {
            filmController.addFilm(film);
        } catch (ValidationException exception) {
            assertNull(exception, "Неожиданное исключение");
        }

        assertEquals(1, filmController.getFilms().size(), "Неверное количество фильмов");
        assertEquals(1, filmController.getFilms().get(0).getId(), "Некорректный Id");
    }
}