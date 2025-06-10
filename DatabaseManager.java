// File: DatabaseManager.java

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // Sesuaikan ini dengan konfigurasi MySQL/XAMPP Anda
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/qeemla_db"; // Pastikan nama DB-nya 'db_qeemla'
    private static final String USER = "root"; // Username database Anda
    private static final String PASSWORD = ""; // Password database Anda (kosong jika default XAMPP)

    public static Connection getConnection() throws SQLException {
        try {
            // Memuat driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver tidak ditemukan!");
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver tidak ditemukan. Pastikan Anda telah menambahkan 'mysql-connector-j-x.x.x.jar' ke classpath Anda.");
        }
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error saat menutup koneksi database: " + e.getMessage());
            }
        }
    }
}
