package App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.mysql.cj.jdbc.Driver;

public class Database {
    private static Connection conn;

    // Method untuk mendapatkan koneksi ke database
    public static Connection getConnection() {
        if (conn == null) {
            try {
                // Menggunakan konfigurasi untuk koneksi database
                String url = "jdbc:mysql://localhost:3306/nontonFilm";
                String user = "root";
                String pass = "";

                // Register JDBC driver
                DriverManager.registerDriver(new Driver());

                // Membuat koneksi
                conn = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                System.out.println("Error database: " + e.getMessage());
            }
        }
        return conn;
    }

    // Method untuk memulai transaksi
    public static void beginTransaction() {
        try {
            if (conn != null) {
                conn.setAutoCommit(false);  // Menonaktifkan auto-commit
                System.out.println("Transaksi dimulai.");
            } else {
                System.out.println("Koneksi tidak tersedia.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal memulai transaksi: " + e.getMessage());
        }
    }

    // Method untuk commit transaksi
    public static void commitTransaction() {
        try {
            if (conn != null) {
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

    // Method untuk rollback transaksi
    public static void rollbackTransaction() {
        try {
            if (conn != null) {
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
}
