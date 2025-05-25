import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardMenu extends JFrame {
    private List<Product> productList = new ArrayList<>();

    public DashboardMenu(String username) {
        setTitle("QEEMLA Dashboard");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel dengan logo dan welcome text
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(6, 94, 84));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Logo
        ImageIcon logoIcon = new ImageIcon("assets/logo_qeemla.png");
        Image logoImage = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Selamat Datang, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Panel tengah dengan tombol menu
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton inputBtn = createStyledButton("1. Input Produk");
        JButton listBtn = createStyledButton("2. Daftar Produk");
        JButton cekExpBtn = createStyledButton("3. Cek Produk Expired");
        JButton remindBtn = createStyledButton("4. Reminder Produk");

        // Aksi tombol
        inputBtn.addActionListener(e -> new InputProductForm(productList));
        listBtn.addActionListener(e -> new DaftarProductForm(productList));
        // Tambahkan aksi sesuai kebutuhan untuk expired dan reminder
        cekExpBtn.addActionListener(e -> new CekExpiredForm(productList).setVisible(true));
        remindBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Fitur reminder belum diimplementasi."));

        centerPanel.add(inputBtn);
        centerPanel.add(listBtn);
        centerPanel.add(cekExpBtn);
        centerPanel.add(remindBtn);

        // Tambahkan ke frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // Utility method untuk membuat tombol dengan style branding
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(new Color(6, 94, 84));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardMenu("admin"));
    }
}
