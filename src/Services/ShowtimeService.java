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
import java.util.Arrays;
import java.util.Comparator;

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

    public Showtime create(CreateShowtimeRequest request) throws ValidationException, SQLException {
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
            Showtime res = this.showtimeRepository.save(newShowtime);

            Database.commitTransaction();
            return res;
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

    public void sortShowtime(int studioId) throws SQLException, ValidationException {
        if (studioId < 0) {
            throw new ValidationException("Studio ID is required");
        }
        try {
            Database.beginTransaction();

            // Ambil semua showtime berdasarkan studio dan urutkan berdasarkan waktu tayang saat ini
            Showtime[] showtimes = this.showtimeRepository.findByStudioId(studioId);

            if (showtimes.length == 0) {
                throw new ValidationException("No showtime found");
            }

            // Urutkan showtimes berdasarkan waktu tayang yang ada
            Arrays.sort(showtimes, Comparator.comparing(showtime -> LocalTime.parse(showtime.showtime, formatter)));

            LocalTime nextAvailableTime = openingTime;

            for (Showtime showtime : showtimes) {
                Film film = this.filmRepository.findById(showtime.filmId);

                // Jika waktu showtime saat ini sebelum nextAvailableTime, set showtime ke nextAvailableTime
                LocalTime showtimeTime = LocalTime.parse(showtime.showtime, formatter);
                if (showtimeTime.isBefore(nextAvailableTime)) {
                    showtimeTime = nextAvailableTime;
                }

                // Set showtime waktu tayang yang sudah disesuaikan
                showtime.showtime = showtimeTime.format(formatter);

                // Hitung waktu tayang berikutnya berdasarkan durasi film dan jeda waktu
                nextAvailableTime = showtimeTime.plusMinutes(film.duration).plusMinutes(breakTimeMinutes);

                // Pastikan waktu berikutnya tidak melewati waktu tutup studio
                if (nextAvailableTime.isAfter(closingTime)) {
                    throw new ValidationException("Cannot schedule showtime past closing time for studio " + studioId);
                }

                // Update showtime ke database
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
        int chairCapacity = this.studioRepository.find(StudioId).capacity;

        FindAllShowtimeResponse request = new FindAllShowtimeResponse();
        request.showtimes = showtimes;
        request.chairCapacity = chairCapacity;

        return request;

    }

    public Showtime getShowtimeById(int showtimeID) throws SQLException, ValidationException {
        if(showtimeID < 0){
            throw new ValidationException("Showtime ID is required");
        }

        Showtime showtime = this.showtimeRepository.find(showtimeID);
        if(showtime == null){
            throw new ValidationException("Showtime not found");
        }

        return showtime;
    }

    public FindAllShowtimeResponse getAllShowtimes() throws SQLException {
        Showtime[] showtimes = this.showtimeRepository.getAll();

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

    public int getStudioCapacity(int studioId) throws SQLException, ValidationException {
        Studio studio = this.studioRepository.find(studioId);
        if (studio == null) {
            throw new ValidationException("Studio not found");
        }
        return studio.capacity;
    }


}
