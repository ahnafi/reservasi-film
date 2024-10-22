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

    // Method untuk menyimpan buku ke database
    public Film save(Film data) throws SQLException {
        String sql = "INSERT INTO film (Judul,Genre,Durasi) VALUES (?, ?, ?)";

        // Menggunakan PreparedStatement untuk mencegah SQL injection
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, data.title);
        statement.setString(2, data.genre );
        statement.setInt(3, data.duration);

        // Eksekusi query
        statement.executeUpdate();
        return data;
    }

    public Film findById(int id) throws SQLException {
        String sql = "SELECT * FROM film WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            Film film = new Film();
            film.id = rs.getInt("id");
            film.title = rs.getString("Judul");
            film.genre = rs.getString("Genre");
            film.duration = rs.getInt("Durasi");
            return film;
        } else {
            return null; // Film tidak ditemukan
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM film WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM film";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.executeUpdate();
    }
    
}
