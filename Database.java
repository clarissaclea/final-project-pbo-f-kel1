import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/qeemla_db"; // ganti sesuai nama database kamu
    private static final String USER = "root"; // ganti jika user MySQL kamu beda
    private static final String PASSWORD = ""; // ganti sesuai password MySQL

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
