package Repository;

import Domain.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentRepository {
    private Connection connection;

    public PaymentRepository(Connection connection) {
        this.connection = connection;
    }

    public Payment save(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (Reservation_ID, Amount, Payment_Date, Status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, payment.reservationId);
            statement.setBigDecimal(2, payment.amount);
            statement.setString(3, payment.paymentDate );
            statement.setString(4, payment.status);

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.id = generatedKeys.getInt(1);
                    return payment;
                }
            }
        }
    }

    public Payment update(Payment payment) throws SQLException {
        String sql = "UPDATE payment SET Reservation_ID = ?, Amount = ?, Payment_Date = ?, Status = ? WHERE Payment_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, payment.reservationId);
            statement.setBigDecimal(2, payment.amount);
            statement.setString(3, payment.paymentDate);
            statement.setString(4, payment.status);
            statement.setInt(5, payment.id);

            statement.executeUpdate();
            return payment;
        }
    }

    public Payment findById(int id) throws SQLException {
        String sql = "SELECT Payment_ID, Reservation_ID, Amount, Payment_Date, Status FROM payment WHERE Payment_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.id = rs.getInt("Payment_ID");
                    payment.reservationId = rs.getInt("Reservation_ID");
                    payment.amount = rs.getBigDecimal("Amount");
                    payment.paymentDate = rs.getString("Payment_Date");
                    payment.status = rs.getString("Status");
                    return payment;
                } else {
                    return null;
                }
            }
        }
    }

    public Payment[] findAll() throws SQLException {
        String sql = "SELECT Payment_ID, Reservation_ID, Amount, Payment_Date, Status FROM payment";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();
            if (rowCount > 0) {
                Payment[] result = new Payment[rowCount];
                int i = 0;

                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.id = rs.getInt("Payment_ID");
                    payment.reservationId = rs.getInt("Reservation_ID");
                    payment.amount = rs.getBigDecimal("Amount");
                    payment.paymentDate = rs.getString("Payment_Date");
                    payment.status = rs.getString("Status");

                    result[i++] = payment;
                }

                return result;
            } else {
                return new Payment[0];
            }
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM payment WHERE Payment_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM payment";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }
}
