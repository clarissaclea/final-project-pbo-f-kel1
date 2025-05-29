import java.util.HashMap;
import java.util.Map;

public class Database {
    // Simulasi tabel pengguna seperti di SQL
    private static Map<String, String> users = new HashMap<>();

    // Static block untuk mengisi "data" seperti data di database
    static {
        users.put("admin", "123");
        users.put("user", "abc");
        users.put("john", "pass123");
    }

    // Fungsi autentikasi
    public static boolean authenticate(String username, String password) {
        // Mengecek apakah username ada dan password sesuai
        return users.containsKey(username) && users.get(username).equals(password);
    }
}
