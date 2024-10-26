package Services;

import Config.Database;
import Domain.Film;
import Model.SaveFilmRequest;
import Model.SaveFilmResponse;
import Repository.FilmRepository;
import Exception.ValidationException;

import java.sql.SQLException;

public class FilmServices {

    private final FilmRepository filmRepository;

    public FilmServices(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public SaveFilmResponse save(SaveFilmRequest request) throws ValidationException, SQLException {
        validateSaveFilm(request);

        try {
            Database.beginTransaction();

            if (this.filmRepository.findById(request.id) != null) {
                throw new ValidationException("Film dengan ID "+ request.id +" sudah ada");
            }

            Film newFilm = new Film();
            newFilm.title = request.title;
            newFilm.genre = request.genre;
            newFilm.duration = request.duration;

            Film res = this.filmRepository.save(newFilm);

            Database.commitTransaction();

            SaveFilmResponse response = new SaveFilmResponse();
            response.film = res;
            return response;
        } catch (SQLException err) {
            Database.rollbackTransaction();
            throw err;
        }
    }

    private void validateSaveFilm(SaveFilmRequest request) throws ValidationException {
        if (request.title == null || request.title.isEmpty() || request.genre == null || request.genre.isEmpty() || request.duration < 0) {
            throw new ValidationException("Film Title , Genre and Duration are required");
        }
    }

}
