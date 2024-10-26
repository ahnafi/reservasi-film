package Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection conn;

    // Method untuk mendapatkan koneksi ke database
    public static Connection getConnection() {
        if (conn == null) {
            try {
                // Menggunakan konfigurasi untuk koneksi database
                String url = "jdbc:mysql://localhost:3306/nontonfilm";  // Pastikan nama database sesuai
                String user = "root";
                String pass = "";

                // Membuat koneksi (tanpa register driver secara manual)
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

    // Method untuk commit transaksi
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

    // Method untuk rollback transaksi
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

    // Method untuk menutup koneksi
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
