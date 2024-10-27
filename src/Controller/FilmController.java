package Controller;

import Config.Database;
import Domain.Film;
import Model.*;
import Repository.FilmRepository;
import Services.FilmService;

import java.sql.Connection;
import java.sql.SQLException;

import Exception.ValidationException;

public class FilmController {

    private FilmService filmService;

    public FilmController() {
        Connection connection = Database.getConnection();

        FilmRepository filmRepository = new FilmRepository(connection);
        this.filmService = new FilmService(filmRepository);
    }

    public void showAll() {
        try {

            FindAllFilmResponse response = this.filmService.showAll();
            Film[] films = response.films;

//        gunakan view objek untuk menampilkan semua films
//        gunakan looping untuk menampilkan semua films
//        for (Film film : films) {
//        System.out.println(film.id + " " + film.title + " " + film.genre + " " + film.duration);
        } catch (SQLException err) {

//            hanlde error memakai tampilan atau alert
            System.out.println(err.getMessage());
        }
    }

    public void addFilm() {
        try {
            int id = 0;
            String title = "";
            String genre = "";
            int duration = 0;

//            tampilkan form untuk menambahkan film
//            masukan data data ke variabel diatas

            SaveFilmRequest request = new SaveFilmRequest();
            request.id = id;
            request.title = title;
            request.genre = genre;
            request.duration = duration;


            SaveFilmResponse response = this.filmService.add(request);
            Film film = response.film;

//            tampilkan alert atau tampilan jika berhasil
//            System.out.println("Film " + film.title + " berhasil ditambahkan");

        } catch (ValidationException | SQLException err) {
//            handle error dengan alert atau tampilan
            System.out.println(err.getMessage());
        }
    }

    public void updateFilm() {
        try {
            int id = 0;
            String title = "";
            String genre = "";
            int duration = 0;

//            tampilkan form untuk mengupdate film
//            masukan data data ke variabel diatas

            UpdateFilmRequest request = new UpdateFilmRequest();
            request.id = id;
            request.title = title;
            request.genre = genre;
            request.duration = duration;

            UpdateFilmResponse response = this.filmService.update(request);
            Film film = response.film;

//            tampilkan alert atau tampilan jika berhasil
//            System.out.println("Film " + film.title + " berhasil diupdate");

        } catch (ValidationException | SQLException err) {
            System.out.println(err.getMessage());
        }
    }

    public void deleteFilm() {
        try {
            int id = 0;

//            tampilkan form untuk menghapus film
//            masukan data data ke variabel diatas

            DeleteFilmRequest request = new DeleteFilmRequest();
            request.filmId = id;

            this.filmService.delete(request);

//            tampilkan alert atau tampilan jika berhasil
//            System.out.println("Film berhasil dihapus");

        } catch (ValidationException | SQLException err) {
//            handle error dengan alert atau tampilan
            System.out.println(err.getMessage());
        }
    }
}
