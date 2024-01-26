package ru.yandex.practicum.filmorate.service.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getGenres() {

        return genreStorage.getGenres();
    }

    public Genre getGenre(Integer id) {

        if (id == null || id < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный запрос или id Genre");
        }
        return genreStorage.getGenre(id);
    }
}
