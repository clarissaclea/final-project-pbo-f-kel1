import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardMenu extends JFrame {
    private List<Product> productList = new ArrayList<>();

    public DashboardMenu(String username) {
        setTitle("📋 QEEMLA Dashboard");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Top Panel (Header)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(6, 94, 84));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        ImageIcon logoIcon = new ImageIcon("assets/logo_qeemla.png");
        Image logoImage = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));

        JLabel welcomeLabel = new JLabel("Selamat Datang, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Center Panel (Menu Button)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JButton inputBtn = createStyledButton("➕  Input Produk");
        JButton listBtn = createStyledButton("📦  Daftar Produk");
        JButton cekExpBtn = createStyledButton("⏰  Cek Produk Expired");
        JButton remindBtn = createStyledButton("🔔  Reminder Produk");

        inputBtn.addActionListener(e -> new InputProductForm(productList));
        listBtn.addActionListener(e -> new DaftarProductForm(productList));
        cekExpBtn.addActionListener(e -> new CekExpiredForm(productList).setVisible(true));
        remindBtn.addActionListener(e -> {
            String reminder = ExpiryChecker.generateReminder(productList);
            JOptionPane.showMessageDialog(this, reminder, "Pengingat Produk Expired", JOptionPane.INFORMATION_MESSAGE);
            });

        centerPanel.add(Box.createVerticalStrut(10)); centerPanel.add(inputBtn);
        centerPanel.add(Box.createVerticalStrut(15)); centerPanel.add(listBtn);
        centerPanel.add(Box.createVerticalStrut(15)); centerPanel.add(cekExpBtn);
        centerPanel.add(Box.createVerticalStrut(15)); centerPanel.add(remindBtn);
        centerPanel.add(Box.createVerticalStrut(10));

        // Footer (opsional)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        JLabel versionLabel = new JLabel("Qeemla Skin & Body Care © 2025");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(120, 120, 120));
        bottomPanel.add(versionLabel);

        // Add to Frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(new Color(6, 94, 84));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(300, 45));
        button.setMaximumSize(new Dimension(300, 45));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardMenu("admin"));
    }
}

