import java.util.HashMap;

public class Database {
    private static HashMap<String, String> users = new HashMap<>();

    static {
        users.put("admin", "123");
        users.put("user", "pass");
    }

    public static boolean authenticate(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }
}
