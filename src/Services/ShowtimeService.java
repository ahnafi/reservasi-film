package Services;

import Config.Database;
import Domain.Film;
import Domain.Showtime;
import Domain.Studio;
import Model.CreateShowtimeRequest;
import Model.FindAllShowtimeResponse;
import Model.RemoveShowtimeRequest;
import Repository.FilmRepository;
import Repository.ShowtimeRepository;
import Repository.StudioRepository;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import Exception.ValidationException;


public class ShowtimeService {

    private ShowtimeRepository showtimeRepository;
    private FilmRepository filmRepository;
    private StudioRepository studioRepository;

    private final LocalTime openingTime = LocalTime.of(9, 0);
    private final LocalTime closingTime = LocalTime.of(21, 0);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private int breakTimeMinutes = 15;

    public ShowtimeService(ShowtimeRepository showtimeRepository, FilmRepository filmRepository, StudioRepository studioRepository) {
        this.showtimeRepository = showtimeRepository;
        this.filmRepository = filmRepository;
        this.studioRepository = studioRepository;
    }

    public void create(CreateShowtimeRequest request) throws ValidationException, SQLException {
        validateCreateShowtimeRequest(request);

        try {
            Database.beginTransaction();

            Film film = this.filmRepository.findById(request.filmId);
            if (film == null) {
                throw new ValidationException("Film not found");
            }

            Studio studio = this.studioRepository.find(request.studioId);
            if (studio == null) {
                throw new ValidationException("Studio not found");
            }

            Showtime[] showtimes = this.showtimeRepository.findByStudioId(request.studioId);
            Showtime newShowtime = new Showtime();
            newShowtime.filmId = film.id;
            newShowtime.studioId = studio.id;

            LocalTime nextAvailableTime;

            if (showtimes.length == 0) {
                nextAvailableTime = openingTime;
            } else {
                Showtime lastShowtime = showtimes[showtimes.length - 1];
                Film lastFilm = this.filmRepository.findById(lastShowtime.filmId);
                LocalTime lastEndTime = LocalTime.parse(lastShowtime.showtime, formatter).plusMinutes(lastFilm.duration);
                nextAvailableTime = lastEndTime.plusMinutes(breakTimeMinutes);
            }

            if (nextAvailableTime.plusMinutes(film.duration).isAfter(closingTime) || nextAvailableTime.plusMinutes(film.duration).equals(closingTime)) {
                throw new ValidationException("Showtime cannot be scheduled after the closing time (21:00)");
            }

            newShowtime.showtime = nextAvailableTime.format(formatter);
            this.showtimeRepository.save(newShowtime);

            Database.commitTransaction();
        } catch (SQLException e) {
            Database.rollbackTransaction();
            throw e;
        }
    }

    private void validateCreateShowtimeRequest(CreateShowtimeRequest request) throws ValidationException {
        if (request.filmId == null || request.filmId < 0 || request.studioId == null || request.studioId < 0) {
            throw new ValidationException("Film ID, Studio ID, and Showtime are required");
        }
    }

    public void sortShowtime(int studioId) throws ValidationException, SQLException {
        if (studioId < 0) {
            throw new ValidationException("Studio ID is required");
        }
        try {
            Database.beginTransaction();

            Showtime[] showtimes = this.showtimeRepository.findByStudioId(studioId);

            if (showtimes.length == 0) {
                throw new ValidationException("No showtime found");
            }

            LocalTime nextAvailableTime = openingTime;

            for (Showtime showtime : showtimes) {
                Film film = this.filmRepository.findById(showtime.filmId);
                LocalTime showtimeTime = LocalTime.parse(showtime.showtime, formatter);

                if (showtimeTime.isBefore(nextAvailableTime)) {
                    showtimeTime = nextAvailableTime;
                }

                showtime.showtime = showtimeTime.format(formatter);
                nextAvailableTime = showtimeTime.plusMinutes(film.duration).plusMinutes(breakTimeMinutes);

                this.showtimeRepository.update(showtime);
            }

            Database.commitTransaction();
        } catch (SQLException e) {
            Database.rollbackTransaction();
            throw e;
        }
    }

    public FindAllShowtimeResponse getShowtimes(int StudioId) throws SQLException, ValidationException {

        if (StudioId < 0) {
            throw new ValidationException("Studio ID is required");
        }

        Showtime[] showtimes = this.showtimeRepository.findByStudioId(StudioId);

        FindAllShowtimeResponse request = new FindAllShowtimeResponse();
        request.showtimes = showtimes;

        return request;

    }

    public void remove(RemoveShowtimeRequest request) throws ValidationException, SQLException {
        if (request.id == null || request.id < 0) {
            throw new ValidationException("ID is required");
        }

        try {
            Database.beginTransaction();

            if (this.showtimeRepository.find(request.id) == null) {
                throw new ValidationException("Showtime not found");
            }

            this.showtimeRepository.delete(request.id);

            Database.commitTransaction();
        } catch (SQLException e) {
            Database.rollbackTransaction();
            throw e;
        }

    }

}
