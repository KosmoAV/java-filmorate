package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {

        SimpleJdbcInsert simpleJdbcInsertFilms = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> filmParams = Map.of("name", film.getName(),
                "description", film.getDescription(),
                "birthday", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa_id", film.getMpa().getId());

        Number id;

        try {
            id = simpleJdbcInsertFilms.executeAndReturnKey(filmParams);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        insertFilmsGenre(film.getGenres(), id.intValue());

        return getFilm(id.intValue());
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {

        try {
            jdbcTemplate.update("UPDATE films SET name = ?, description = ?, birthday = ?, " +
                                    "duration = ?, mpa_id = ? WHERE id = ?",
                                    film.getName(), film.getDescription(), film.getReleaseDate(),
                                    film.getDuration(), film.getMpa().getId(), film.getId());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        try {
            jdbcTemplate.update("DELETE FROM films_genre WHERE film_id = ?", film.getId());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        insertFilmsGenre(film.getGenres(), film.getId());

        return getFilm(film.getId());
    }

    @Override
    public List<Film> getFilms() {

        String sql = "SELECT frfg.*, g.genre FROM " +

                "(SELECT fr.*, fg.genre_id FROM " +

                "(SELECT f.id, f.name, f.description, f.birthday, f.duration, f.mpa_id, r.mpa " +
                "FROM films AS f LEFT OUTER JOIN ratings AS r " +
                "ON f.mpa_id = r.id) AS fr " +

                "LEFT OUTER JOIN films_genre AS fg " +
                "ON fr.id = fg.film_id) AS frfg " +

                "LEFT OUTER JOIN genres AS g " +
                "ON frfg.genre_id = g.id " +

                "ORDER BY frfg.id;";

        try {
            return jdbcTemplate.queryForObject(sql, filmsRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Film getFilm(Integer id) {

        String sql = "SELECT frfg.*, g.genre FROM " +

                     "(SELECT fr.*, fg.genre_id FROM " +

                     "(SELECT f.id, f.name, f.description, f.birthday, f.duration, f.mpa_id, r.mpa " +
                     "FROM films AS f LEFT OUTER JOIN ratings AS r " +
                     "ON f.mpa_id = r.id) AS fr " +

                     "LEFT OUTER JOIN films_genre AS fg " +
                     "ON fr.id = fg.film_id) AS frfg " +

                     "LEFT OUTER JOIN genres AS g " +
                     "ON frfg.genre_id = g.id " +

                     "WHERE frfg.id = ?;";

        try {
            return jdbcTemplate.queryForObject(sql, filmRowMapper(), id);

        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        try {
            jdbcTemplate.update("INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);", id, userId);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        try {
            jdbcTemplate.update("DELETE FROM films_likes WHERE film_id = ? AND user_id = ?;", id, userId);
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {

        String sql = "SELECT film_mpa.*, COALESCE (count_likes, 0) AS counts FROM " +

                "(SELECT films.*, ratings.mpa FROM films " +
                "LEFT OUTER JOIN ratings " +
                "ON films.mpa_id = ratings.id) AS film_mpa " +

                "LEFT OUTER JOIN " +

                "(SELECT film_id, count(user_id) AS count_likes " +
                "FROM films_likes " +
                "GROUP BY film_id " +
                ") AS likes " +

                "ON film_mpa.id = likes.film_id " +
                "ORDER BY counts DESC " +
                "LIMIT ?;";

        try {
            return jdbcTemplate.query(sql, popularFilmRowMapper(), count);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {

            Film film = new Film(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("birthday").toLocalDate(),
                    rs.getInt("duration"),
                    new MPA(rs.getInt("mpa_id"), rs.getString("mpa"))
            );

            do {

                int genreId = rs.getInt("genre_id");
                if (genreId == 0) {
                    break;
                }

                film.getGenres().add(new Genre(genreId, rs.getString("genre")));

            } while (rs.next());

            return film;
        };
    }

    private RowMapper<List<Film>> filmsRowMapper() {
        return (rs, rowNum) -> {

            List<Film> films = new ArrayList<>();
            boolean done = false;

            do {

                Film film = new Film(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("birthday").toLocalDate(),
                        rs.getInt("duration"),
                        new MPA(rs.getInt("mpa_id"), rs.getString("mpa"))
                );

                do {

                    int genreId = rs.getInt("genre_id");
                    if (genreId == 0) {
                        done = !rs.next();
                        break;
                    }

                    film.getGenres().add(new Genre(genreId, rs.getString("genre")));
                    done = !rs.next();

                } while (!done && (rs.getInt("id") == film.getId()));

                films.add(film);

            } while (!done);

            return films;
        };
    }

    private RowMapper<Film> popularFilmRowMapper() {
        return (rs, rowNum) -> {

            Film film = new Film(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("birthday").toLocalDate(),
                    rs.getInt("duration"),
                    new MPA(rs.getInt("mpa_id"), rs.getString("mpa"))
            );

            return film;
        };
    }

    private void insertFilmsGenre(List<Genre> genres, Integer filmId) {

        if (genres != null && genres.size() > 0) {

            StringBuilder str = new StringBuilder();
            str.append("INSERT INTO films_genre (film_id, genre_id) VALUES");

            List<Integer> genresId =  genres.stream()
                    .map(Genre::getId)
                    .distinct()
                    .collect(Collectors.toList());

            for (int i = 0; i < genresId.size(); i++) {
                str.append(" (?, ?),");
            }
            str.deleteCharAt(str.length() - 1);
            str.append(";");

            List<Integer> pairId = new ArrayList<>();

            for (Integer genreId : genresId) {
                pairId.add(filmId);
                pairId.add(genreId);
            }

            try {
                jdbcTemplate.update(str.toString(), pairId.toArray(new Integer[1]));
            } catch (DataAccessException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }
}
