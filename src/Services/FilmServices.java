package Services;

import Config.Database;
import Domain.Film;
import Model.*;
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
                throw new ValidationException("Film dengan ID " + request.id + " sudah ada");
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
        if (request.title == null || request.title.isEmpty() || request.genre == null || request.genre.isEmpty() || request.duration < 0 || request.id < 0) {
            throw new ValidationException("Film id ,Title , Genre and Duration are required");
        }
    }

    public UpdateFilmResponse update(UpdateFilmRequest request) throws ValidationException, SQLException {
        validateUpdateFilmRequest(request);

        try {
            Database.beginTransaction();

            Film film = this.filmRepository.findById(request.id);
            if (film == null) {
                throw new ValidationException("Film tidak ditemukan");
            }

            Film updateFilm = new Film();
            updateFilm.title = request.title;
            updateFilm.genre = request.genre;
            updateFilm.duration = request.duration;
            updateFilm.id = request.id;

            film = this.filmRepository.update(updateFilm);

            Database.commitTransaction();

            UpdateFilmResponse response = new UpdateFilmResponse();
            response.film = film;
            return response;
        } catch (SQLException err) {
            Database.rollbackTransaction();
            throw err;
        }

    }

    private void validateUpdateFilmRequest(UpdateFilmRequest request) throws ValidationException {
        if (request.title == null || request.title.isEmpty() || request.genre == null || request.genre.isEmpty() || request.duration < 0 || request.id < 0) {
            throw new ValidationException("Film id,Title , Genre and Duration are required");
        }
    }

    public void delete(DeleteFilmRequest request) throws ValidationException, SQLException {
        if(request.filmId < 0 ){
            throw new ValidationException("film id tidak boleh kosong");
        }

        try{
            Database.beginTransaction();

            if (this.filmRepository.findById(request.filmId) == null) {
                throw new ValidationException("Film tidak ditemukan");
            }

            this.filmRepository.deleteById(request.filmId);

            Database.commitTransaction();
        }catch (SQLException err){
            Database.rollbackTransaction();
            throw err;
        }
    }

}
