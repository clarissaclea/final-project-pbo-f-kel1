// File: DashboardMenu.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DashboardMenu extends JFrame {

    private String loggedInUsername;
    private ProductDAO productDAO;
    private ExpiryChecker expiryChecker;

    private DaftarProductForm activeDaftarProductForm = null;
    private CekExpiredForm activeCekExpiredForm = null;

    public DashboardMenu(String username) {
        this.loggedInUsername = username;
        this.productDAO = new ProductDAO();
        this.expiryChecker = new ExpiryChecker(productDAO);

        setTitle("QEEMLA Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            URL iconUrl = getClass().getClassLoader().getResource("assets/logo_qeemla.png");
            if (iconUrl != null) {
                ImageIcon frameIcon = new ImageIcon(iconUrl);
                setIconImage(frameIcon.getImage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        // =================== HEADER =====================
        JPanel topBarPanel = new JPanel(new BorderLayout());
        topBarPanel.setBackground(new Color(6, 94, 84)); // Ubah jadi hijau
        topBarPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel leftHeaderPanel = new JPanel();
        leftHeaderPanel.setOpaque(false);
        leftHeaderPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

        ImageIcon logoIcon = null;
        try {
            URL logoUrl = getClass().getClassLoader().getResource("assets/logo_qeemla.png");
            if (logoUrl != null) {
                Image originalImage = new ImageIcon(logoUrl).getImage();
                Image scaledImage = originalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel logoImageLabel = new JLabel();
        if (logoIcon != null) {
            logoImageLabel.setIcon(logoIcon);
        }

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel qeemlaTextLabel = new JLabel("QEEMLA");
        qeemlaTextLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        qeemlaTextLabel.setForeground(Color.WHITE); // Putih

        JLabel simproTextLabel = new JLabel("Sistem Informasi Manajemen Produk");
        simproTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        simproTextLabel.setForeground(new Color(220, 220, 220)); // Abu terang

        textPanel.add(qeemlaTextLabel);
        textPanel.add(simproTextLabel);

        leftHeaderPanel.add(logoImageLabel);
        leftHeaderPanel.add(textPanel);

        JLabel welcomeLabel = new JLabel("Selamat Datang, " + loggedInUsername + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setForeground(Color.WHITE); // Putih

        JButton logoutButton = new JButton("Logout");
        styleLogoutButton(logoutButton);

        topBarPanel.add(leftHeaderPanel, BorderLayout.WEST);
        topBarPanel.add(welcomeLabel, BorderLayout.CENTER);
        topBarPanel.add(logoutButton, BorderLayout.EAST);

        mainPanel.add(topBarPanel, BorderLayout.NORTH);

        // =================== TOMBOL =====================
        JPanel centerButtonsPanel = new JPanel(new GridLayout(4, 1, 0, 20));
        centerButtonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));
        centerButtonsPanel.setOpaque(false);

        JButton inputProductBtn = createDashboardButton("1. Input Produk");
        JButton listProductBtn = createDashboardButton("2. Daftar Produk");
        JButton checkExpiredBtn = createDashboardButton("3. Cek Produk Expired");
        JButton reminderBtn = createDashboardButton("4. Reminder Produk");

        centerButtonsPanel.add(inputProductBtn);
        centerButtonsPanel.add(listProductBtn);
        centerButtonsPanel.add(checkExpiredBtn);
        centerButtonsPanel.add(reminderBtn);

        mainPanel.add(centerButtonsPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // =================== EVENT HANDLER =====================
        inputProductBtn.addActionListener(e -> {
            InputProductForm inputForm = new InputProductForm(productDAO);
            inputForm.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    if (activeDaftarProductForm != null && activeDaftarProductForm.isVisible())
                        activeDaftarProductForm.refreshTable();
                    if (activeCekExpiredForm != null && activeCekExpiredForm.isVisible())
                        activeCekExpiredForm.populateExpiredProducts();
                }
            });
            inputForm.setVisible(true);
        });

        listProductBtn.addActionListener(e -> {
            if (activeDaftarProductForm == null || !activeDaftarProductForm.isVisible()) {
                activeDaftarProductForm = new DaftarProductForm(productDAO);
                activeDaftarProductForm.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent e) {
                        activeDaftarProductForm = null;
                    }
                });
                activeDaftarProductForm.setVisible(true);
            } else {
                activeDaftarProductForm.toFront();
                activeDaftarProductForm.refreshTable();
            }
        });

        checkExpiredBtn.addActionListener(e -> {
            if (activeCekExpiredForm == null || !activeCekExpiredForm.isVisible()) {
                activeCekExpiredForm = new CekExpiredForm(productDAO);
                activeCekExpiredForm.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent e) {
                        activeCekExpiredForm = null;
                    }
                });
                activeCekExpiredForm.setVisible(true);
            } else {
                activeCekExpiredForm.toFront();
                activeCekExpiredForm.populateExpiredProducts();
            }
        });

        reminderBtn.addActionListener(e -> {
            String reminderText = expiryChecker.generateReminder();
            JOptionPane.showMessageDialog(DashboardMenu.this, reminderText, "Pengingat Produk Kedaluwarsa", JOptionPane.INFORMATION_MESSAGE);
        });

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(DashboardMenu.this, "Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new QeemlaLoginForm().setVisible(true);
            }
        });
    }

    private JButton createDashboardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setBackground(new Color(6, 94, 84)); // hijau senada
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return button;
    }

    private void styleLogoutButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(220, 20, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
    }
}
