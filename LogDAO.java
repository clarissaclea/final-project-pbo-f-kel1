import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogDAO {

    public static void simpanLog(String aksi, String detail, String emailPengguna) {
        String query = "INSERT INTO log_audit (aksi, detail, email_pengguna) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();  // ✅ pakai DatabaseManager di sini
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, aksi);
            stmt.setString(2, detail);
            stmt.setString(3, emailPengguna);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Gagal menyimpan log audit.");
        }
    }
}
