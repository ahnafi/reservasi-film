package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/nontonfilm";
                String user = "root";
                String pass = "";

                conn = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                System.out.println("Error database: " + e.getMessage());
            }
        }
        return conn;
    }

    public static void beginTransaction() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.setAutoCommit(false);  // Menonaktifkan auto-commit
                System.out.println("Transaksi dimulai.");
            } else {
                System.out.println("Koneksi tidak tersedia.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal memulai transaksi: " + e.getMessage());
        }
    }

    public static void commitTransaction() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.commit();  // Melakukan commit
                conn.setAutoCommit(true);  // Mengaktifkan kembali auto-commit
                System.out.println("Transaksi berhasil di-commit.");
            } else {
                System.out.println("Koneksi tidak tersedia.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal commit transaksi: " + e.getMessage());
        }
    }

    public static void rollbackTransaction() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();  // Melakukan rollback
                conn.setAutoCommit(true);  // Mengaktifkan kembali auto-commit
                System.out.println("Transaksi di-rollback.");
            } else {
                System.out.println("Koneksi tidak tersedia.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal rollback transaksi: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }
}
