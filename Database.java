public class Main {
    public static void main(String[] args) {
        boolean isAuthenticated = Database.authenticate("admin", "123");
        System.out.println("Authenticated: " + isAuthenticated);  // Output: Authenticated: true
        
        isAuthenticated = Database.authenticate("user", "wrongpass");
        System.out.println("Authenticated: " + isAuthenticated);  // Output: Authenticated: false
    }
}
