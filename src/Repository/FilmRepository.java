package Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Domain.Film;

public class FilmRepository {
    private Connection conn;

    public FilmRepository(Connection conn) {
        this.conn = conn;
    }

    public Film save(Film data) throws SQLException {
        String sql = "INSERT INTO film (Film_ID,Judul, Genre, Durasi) VALUES (?,?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1,data.id);
            statement.setString(2, data.title);
            statement.setString(3, data.genre);
            statement.setInt(4, data.duration);

            // Eksekusi query
            statement.executeUpdate();
        }

        return data;
    }

    public Film update(Film film) throws SQLException {
        String sql = "UPDATE film SET Judul = ?, Genre = ?, Durasi = ? WHERE Film_ID = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1,film.title );
            statement.setString(2, film.genre );
            statement.setInt(3, film.duration );
            statement.setInt(4, film.id );
            statement.executeUpdate();

            return film;
        }
    }

    public Film[] findAll() throws SQLException {
        String sql = "SELECT Film_ID, Judul, Genre, Durasi FROM film";
        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            rs.last(); // Pindah ke baris terakhir untuk mendapatkan jumlah baris
            int rowCount = rs.getRow();
            rs.beforeFirst(); // Kembali ke baris pertama

            if (rowCount > 0) {
                Film[] result = new Film[rowCount];
                int i = 0;

                while (rs.next()) {
                    Film film = new Film();
                    film.id = rs.getInt("Film_ID");
                    film.title = rs.getString("Judul");
                    film.genre = rs.getString("Genre");
                    film.duration = rs.getInt("Durasi");

                    result[i] = film;
                    i++;
                }

                return result;
            } else {
                return new Film[0];
            }
        }
    }

    public Film findById(int id) throws SQLException {
        String sql = "SELECT Film_ID, Judul, Genre, Durasi FROM film WHERE Film_ID = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Film film = new Film();
                    film.id = rs.getInt("Film_ID");
                    film.title = rs.getString("Judul");
                    film.genre = rs.getString("Genre");
                    film.duration = rs.getInt("Durasi");
                    return film;
                } else {
                    return null; // Film tidak ditemukan
                }
            }
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM film WHERE Film_ID = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM film";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }
}
