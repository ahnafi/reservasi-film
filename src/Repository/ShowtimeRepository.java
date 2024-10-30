package Repository;

import Domain.Showtime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeRepository {

    private Connection connection;

    public ShowtimeRepository(Connection connection) {
        this.connection = connection;
    }

    public Showtime save(Showtime req) throws SQLException {
        String sql = "INSERT INTO showtime (Film_ID, Studio_ID, Jam_Tayang) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, req.filmId);
            statement.setInt(2, req.studioId);
            statement.setString(3, req.showtime);
            statement.executeUpdate();

            // Mengambil ID yang baru dibuat
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {

                    req.id = generatedKeys.getInt(1);

                    return req;
                } else {
                    throw new SQLException("Gagal mendapatkan ID, tidak ada ID yang dibuat.");
                }
            }
        }
    }

    public Showtime update(Showtime showtime) throws SQLException {
        String sql = "UPDATE showtime SET Jam_Tayang = ? WHERE Film_ID = ? AND Studio_ID = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setString(1, showtime.showtime);
            statement.setInt(2, showtime.filmId);
            statement.setInt(3, showtime.studioId);

            statement.executeUpdate();
            return showtime;
        }
    }

    public Showtime[] getAll() throws SQLException {
        String sql = "SELECT Showtime_ID,Film_ID,Studio_ID,Jam_Tayang FROM showtime";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            ResultSet res = statement.executeQuery();

            // Menggunakan ArrayList untuk fleksibilitas ukuran
            List<Showtime> showtimeList = new ArrayList<>();

            while (res.next()) {
                Showtime showtime = new Showtime();
                showtime.id = res.getInt("Showtime_ID");
                showtime.filmId = res.getInt("Film_ID");
                showtime.studioId = res.getInt("Studio_ID");
                showtime.showtime = res.getString("Jam_Tayang");

                showtimeList.add(showtime);  // Tambah ke list
            }

            // Mengonversi ArrayList ke array Showtime[]
            return showtimeList.toArray(new Showtime[0]);
        }
    }

    public Showtime[] findByStudioId(int studioId) throws SQLException {
        String sql = "SELECT Showtime_ID, Film_ID, Studio_ID, Jam_Tayang FROM showtime WHERE Studio_ID = ? ORDER BY Jam_Tayang ASC";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, studioId);
            ResultSet res = statement.executeQuery();

            List<Showtime> showtimeList = new ArrayList<>();

            while (res.next()) {
                Showtime showtime = new Showtime();
                showtime.id = res.getInt("Showtime_ID");
                showtime.filmId = res.getInt("Film_ID");
                showtime.studioId = res.getInt("Studio_ID");
                showtime.showtime = res.getString("Jam_Tayang");

                showtimeList.add(showtime);
            }

            return showtimeList.toArray(new Showtime[0]);
        }
    }

    public Showtime find(int id) throws SQLException {
        String sql = "SELECT Showtime_ID,Film_ID,Studio_ID,Jam_Tayang FROM showtime WHERE Showtime_ID = ?";

        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    Showtime showtime = new Showtime();
                    showtime.id = res.getInt("Showtime_ID");
                    showtime.filmId = res.getInt("Film_ID");
                    showtime.studioId = res.getInt("Studio_ID");
                    showtime.showtime = res.getString("Jam_Tayang");

                    return showtime;
                } else {
                    return null;
                }
            }
        }
    }

    public void delete(int id) throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("DELETE FROM showtime WHERE Showtime_ID = ?");
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public void deleteAll() throws SQLException {
        PreparedStatement statement = this.connection.prepareStatement("DELETE FROM showtime");
        statement.executeUpdate();
    }
}
