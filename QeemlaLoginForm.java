import javax.swing.*;
import java.awt.*;

public class QeemlaLoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public QeemlaLoginForm() {
        setTitle("Qeemla Login");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(false);
        setLayout(new GridLayout(1, 2));

        // Panel kiri: branding Qeemla
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(6, 94, 84));
        leftPanel.setLayout(new BorderLayout());

        // Tambahkan logo
        ImageIcon logoIcon = new ImageIcon("assets/logo_qeemla.png");
        Image logo = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logo));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel brandLabel = new JLabel("QEEMLA", JLabel.CENTER);
        brandLabel.setFont(new Font("Georgia", Font.BOLD, 36));
        brandLabel.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Skin and Body Care", JLabel.CENTER);
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tagline.setForeground(Color.WHITE);

        JPanel brandingPanel = new JPanel(new GridLayout(3, 1));
        brandingPanel.setOpaque(false);
        brandingPanel.add(logoLabel);
        brandingPanel.add(brandLabel);
        brandingPanel.add(tagline);

        leftPanel.add(brandingPanel, BorderLayout.CENTER);

        // Panel kanan: form login
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("SIGN IN TO QEEMLA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(6, 94, 84));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(6, 94, 84));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        // Aksi tombol login
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (email.equalsIgnoreCase("admin@qeemla.com") && password.equals("1234")) {
                JOptionPane.showMessageDialog(this, "Login Berhasil", "Welcome", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                // Ambil nama dari email
                String username = email.split("@")[0];
                new DashboardMenu(username);
            } else {
                JOptionPane.showMessageDialog(this, "Login Gagal", "Email atau password salah!", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tambah komponen ke form login
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        rightPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        rightPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        rightPanel.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy++;
        rightPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        rightPanel.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        rightPanel.add(loginButton, gbc);

        add(leftPanel);
        add(rightPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QeemlaLoginForm::new);
    }
}
