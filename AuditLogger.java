import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class AuditLogger {

    public static void log(String aksi, String detail, String emailPengguna) {
        String sql = "INSERT INTO log_audit (waktu_aksi, aksi, detail, email_pengguna) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setString(2, aksi);
            stmt.setString(3, detail);
            stmt.setString(4, emailPengguna);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
