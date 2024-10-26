package Repository;

import Domain.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReservationRepository {

    private Connection connection;

    public ReservationRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (Showtime_ID, Nomor_Kursi, Status) VALUES (?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, reservation.showtimeId);
            statement.setString(2, reservation.chairNumber);
            statement.setString(3, reservation.status);

            statement.executeUpdate();
        }
    }

    public Reservation update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET Showtime_ID = ?, Nomor_Kursi = ?, Status = ? WHERE Reservation_ID = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, reservation.showtimeId);
            statement.setString(2, reservation.chairNumber);
            statement.setString(3, reservation.status);
            statement.setInt(4, reservation.id); // Pastikan Reservation_ID ada di dalam domain

            statement.executeUpdate();
            return reservation;
        }
    }

    public Reservation find(int id) throws SQLException {
        String sql = "SELECT Reservation_ID, Showtime_ID, Nomor_Kursi, Status FROM reservation WHERE Reservation_ID = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.id = rs.getInt("Reservation_ID");
                    reservation.showtimeId = rs.getInt("Showtime_ID");
                    reservation.chairNumber = rs.getString("Nomor_Kursi");
                    reservation.status = rs.getString("Status");
                    return reservation;
                } else {
                    return null; // Reservasi tidak ditemukan
                }
            }
        }
    }

    public Reservation[] findAll() throws SQLException {
        String sql = "SELECT Reservation_ID, Showtime_ID, Nomor_Kursi, Status FROM reservation";
        try (PreparedStatement statement = this.connection.prepareStatement(sql); ResultSet rs = statement.executeQuery()) {

            rs.last(); // Pindah ke baris terakhir untuk mendapatkan jumlah baris
            int rowCount = rs.getRow();
            rs.beforeFirst(); // Kembali ke baris pertama

            if (rowCount > 0) {
                Reservation[] result = new Reservation[rowCount];
                int i = 0;

                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.id = rs.getInt("Reservation_ID");
                    reservation.showtimeId = rs.getInt("Showtime_ID");
                    reservation.chairNumber = rs.getString("Nomor_Kursi");
                    reservation.status = rs.getString("Status");

                    result[i++] = reservation;
                }

                return result;
            } else {
                return new Reservation[0]; // Kembalikan array kosong jika tidak ada reservasi
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM reservation WHERE Reservation_ID = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM reservation";
        try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }
}
