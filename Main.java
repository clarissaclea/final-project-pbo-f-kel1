// File: Main.java

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QeemlaLoginForm().setVisible(true);
        });
    }
}