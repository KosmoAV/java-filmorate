package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.Genre.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private static final Logger log = LoggerFactory.getLogger(GenreController.class);

    private GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Получен запрос на получение всех жанров");
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable Integer id) {
        log.info("Получен запрос на получение жанра с id = " + id);
        return genreService.getGenre(id);
    }
}
