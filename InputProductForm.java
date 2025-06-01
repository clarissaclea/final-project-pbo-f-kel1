import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage; // Tambahkan baris ini
import java.sql.SQLException;
import java.util.List; // Tetap diperlukan untuk ExpiryChecker
import java.util.ArrayList; // Jika Anda perlu ArrayList di masa depan

public class DashboardMenu extends JFrame {
    private ProductDAO productDAO;
    private ExpiryChecker expiryChecker; // Deklarasikan ExpiryChecker

    // Referensi untuk form yang mungkin dibuka, agar bisa di-refresh
    private DaftarProductForm activeDaftarProductForm = null;
    private CekExpiredForm activeCekExpiredForm = null;
    // Jika Anda berencana membuat form khusus untuk reminder:
    // private JDialog activeExpiryCheckerForm = null; // Contoh jika reminder akan jadi form terpisah

    public DashboardMenu(String username) {
        setTitle("QEEMLA Skin&Body Care");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inisialisasi ProductDAO dan ExpiryChecker di sini
        this.productDAO = new ProductDAO();
        this.expiryChecker = new ExpiryChecker(productDAO); // Inisialisasi ExpiryChecker

        // Top panel dengan logo dan welcome text
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(6, 94, 84));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Logo
        ImageIcon logoIcon = new ImageIcon("assets/logo_qeemla.png");
        if (logoIcon.getImageLoadStatus() == MediaTracker.ERRORED) {
             System.err.println("Error loading logo image: assets/logo_qeemla.png");
             // Fallback atau placeholder jika gambar tidak ditemukan
             logoIcon = new ImageIcon(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB));
        }
        Image logoImage = logoIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImage));

        // Welcome label
        JLabel welcomeLabel = new JLabel("Selamat Datang, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Center panel dengan tombol-tombol
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 15, 15)); // Mengatur jarak antar tombol
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        centerPanel.setBackground(new Color(240, 248, 255)); // Warna latar belakang yang lebih terang

        JButton inputBtn = createStyledButton("Tambah Produk Baru");
        JButton listBtn = createStyledButton("Daftar Semua Produk");
        JButton cekExpBtn = createStyledButton("Cek Produk Kedaluwarsa");
        JButton remindBtn = createStyledButton("Pengingat Expired"); // Tombol ini yang akan kita perbaiki

        // Action Listeners - Sekarang meneruskan productDAO
        inputBtn.addActionListener(e -> {
            InputProductForm inputForm = new InputProductForm(productDAO);
            inputForm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // Ketika InputProductForm ditutup, refresh DaftarProductForm jika aktif
                    if (activeDaftarProductForm != null && activeDaftarProductForm.isVisible()) {
                        activeDaftarProductForm.refreshTable();
                    }
                    // Refresh CekExpiredForm juga
                    if (activeCekExpiredForm != null && activeCekExpiredForm.isVisible()) {
                        activeCekExpiredForm.populateExpiredProducts();
                    }
                }
            });
            inputForm.setVisible(true);
        });

        listBtn.addActionListener(e -> {
            if (activeDaftarProductForm == null || !activeDaftarProductForm.isVisible()) {
                activeDaftarProductForm = new DaftarProductForm(productDAO);
                activeDaftarProductForm.setVisible(true);
            } else {
                activeDaftarProductForm.toFront(); // Bawa ke depan
                activeDaftarProductForm.refreshTable(); // Refresh manual jika sudah ada
            }
        });

        cekExpBtn.addActionListener(e -> {
            if (activeCekExpiredForm == null || !activeCekExpiredForm.isVisible()) {
                activeCekExpiredForm = new CekExpiredForm(productDAO);
                activeCekExpiredForm.setVisible(true);
            } else {
                activeCekExpiredForm.toFront();
                activeCekExpiredForm.populateExpiredProducts();
            }
        });

        // --- PERBAIKAN UNTUK remindBtn ---
        remindBtn.addActionListener(e -> {

            expiryChecker.generateReminder(); // Langsung panggil metode reminder
        });
        // --- AKHIR PERBAIKAN UNTUK remindBtn ---


        centerPanel.add(inputBtn);
        centerPanel.add(listBtn);
        centerPanel.add(cekExpBtn);
        centerPanel.add(remindBtn);

        // Tambahkan ke frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);

        // --- PANGGILAN PENGINGAT SAAT APLIKASI DIBUKA (Hanya sekali di sini) ---
        // Panggil generateReminder langsung, karena ia sudah punya logika untuk mengambil data
        expiryChecker.generateReminder();
        // --- AKHIR PANGGILAN PENGINGAT AWAL ---
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

    // Main method (jika ada, biarkan tetap untuk testing langsung)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardMenu("AdminTester"));
    }
}
