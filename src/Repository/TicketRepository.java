package Repository;

import Domain.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketRepository {

    private Connection connection;

    public TicketRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO ticket (Reservation_ID, Nomor_Kursi, Purchase_Date) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticket.reservationId);
            statement.setString(2, ticket.chairNumber);
            statement.setString(3, ticket.purchaseDate);

            statement.executeUpdate();
        }
    }

    // Method untuk mengupdate ticket
    public Ticket update(Ticket ticket) throws SQLException {
        String sql = "UPDATE ticket SET Reservation_ID = ?, Nomor_Kursi = ?, Purchase_Date = ? WHERE Ticket_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticket.reservationId);
            statement.setString(2, ticket.chairNumber);
            statement.setString(3, ticket.purchaseDate );
            statement.setInt(4, ticket.id);

            statement.executeUpdate();
            return ticket;
        }
    }

    // Method untuk mencari ticket berdasarkan ID
    public Ticket findById(int id) throws SQLException {
        String sql = "SELECT Ticket_ID, Reservation_ID, Nomor_Kursi, Purchase_Date FROM ticket WHERE Ticket_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Ticket ticket = new Ticket();
                    ticket.id = rs.getInt("Ticket_ID");
                    ticket.reservationId = rs.getInt("Reservation_ID");
                    ticket.chairNumber = rs.getString("Nomor_Kursi");
                    ticket.purchaseDate = rs.getString("Purchase_Date");

                    return ticket;
                } else {
                    return null;
                }
            }
        }
    }

    // Method untuk mengambil semua data ticket
    public List<Ticket> findAll() throws SQLException {
        String sql = "SELECT Ticket_ID, Reservation_ID, Nomor_Kursi, Purchase_Date FROM ticket";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            List<Ticket> tickets = new ArrayList<>();
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.id = rs.getInt("Ticket_ID");
                ticket.reservationId = rs.getInt("Reservation_ID");
                ticket.chairNumber = rs.getString("Nomor_Kursi");
                ticket.purchaseDate = rs.getString("Purchase_Date");

                tickets.add(ticket);
            }
            return tickets;
        }
    }

    // Method untuk menghapus ticket berdasarkan ID
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM ticket WHERE Ticket_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Method untuk menghapus semua data ticket
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM ticket";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }
}
