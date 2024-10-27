package Controller;

import Config.Database;
import Model.CreateShowtimeRequest;
import Model.FindAllShowtimeResponse;
import Model.RemoveShowtimeRequest;
import Repository.FilmRepository;
import Repository.ShowtimeRepository;
import Repository.StudioRepository;
import Services.ShowtimeService;

import java.sql.Connection;
import java.sql.SQLException;

import Exception.ValidationException;

public class ShowtimeController {

    private ShowtimeService showtimeService;

    public ShowtimeController() {
        Connection connection = Database.getConnection();
        ShowtimeRepository showtimeRepository = new ShowtimeRepository(connection);
        FilmRepository filmRepository = new FilmRepository(connection);
        StudioRepository studioRepository = new StudioRepository(connection);
        this.showtimeService = new ShowtimeService(showtimeRepository, filmRepository, studioRepository);
    }

    public void addShowtime() {
        try {
            int filmId = 0;
            int studioId = 0;

//            view input form for filmId and studioId
//            filmId = inputFilmId();
//            studioId = inputStudioId();
//            lalu masukan ke variabel di atas

            CreateShowtimeRequest request = new CreateShowtimeRequest();
            request.filmId = filmId;
            request.studioId = studioId;

            this.showtimeService.create(request);

//            view success message alert atau tampilan
//            System.out.println("Showtime added successfully");

        } catch (ValidationException | SQLException e) {
//            view error message alert atau tampilan
            System.out.println(e.getMessage());
        }
    }

    public void updateShowtime() {
        try {
            int studioId = 0;
//        tombol sort showtime untuk mereset jam tayang berdasarkan studioId

            this.showtimeService.sortShowtime(studioId);

//            view success message alert atau tampilan
//            System.out.println("Showtime updated successfully");

//            tampilakan jam tayang yang sudah diurutkan

            FindAllShowtimeResponse response = this.showtimeService.getShowtimes(studioId);
//            tampilkan dalam bentuk list atau tabel , dengan view
//            System.out.println("Showtimes:");
//            for (Showtime showtime : response.showtimes) {
//                System.out.println(showtime.showtime);
//            }


        }catch (ValidationException | SQLException e){
//            view error message alert atau tampilan
            System.out.println(e.getMessage());
        }
    }

    public void removeShowtime() {
        try{

            int showtimeId = 0;

//            view input form for showtimeId
//            showtimeId = inputShowtimeId();
//            lalu masukan ke variabel di atas

            RemoveShowtimeRequest request = new RemoveShowtimeRequest();
            request.id = showtimeId;
            this.showtimeService.remove(request);

//            view success message alert atau tampilan
//            System.out.println("Showtime removed successfully");

        }catch (ValidationException | SQLException e){
//            view error message alert atau tampilan
            System.out.println(e.getMessage());
        }
    }

}
