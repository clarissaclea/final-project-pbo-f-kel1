import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.awt.Dimension;
import java.sql.SQLException;

// Pastikan import ini ADA dan BENAR
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class InputProductForm extends JFrame {
    private JTextField tfBarcode, tfNama, tfHarga, tfKategori;
    private JSpinner spinnerProduksi, spinnerExpired;
    private ProductDAO productDAO;
    private JLabel photoLabel;
    private File selectedPhoto;

    // Webcam-related fields
    private Webcam webcam = null;
    private WebcamPanel webcamPanel = null;
    private Thread barcodeScannerThread = null;
    private JButton startWebcamBtn, stopWebcamBtn, captureBtn;
    private JPanel webcamControlPanel;
    private JPanel mainPanel; // Declare mainPanel at class level to make it accessible

    // --- Data Produk yang Telah Ditentukan Sebelumnya ---
    private static class PredefinedProductInfo {
        String name;
        double price;
        String category;
        String imagePath;

        public PredefinedProductInfo(String name, double price, String category, String imagePath) {
            this.name = name;
            this.price = price;
            this.category = category;
            this.imagePath = imagePath;
        }
    }

    private static final Map<String, PredefinedProductInfo> predefinedProducts = new HashMap<>();
    static {
        // Contoh produk predefinisi
        predefinedProducts.put("8998800100018", new PredefinedProductInfo("Facial Wash Brightening", 35000.0, "Pembersih Wajah", "assets/photo/facial_wash_brightening.jpg"));
        predefinedProducts.put("8998800100025", new PredefinedProductInfo("Daily Moisturizer Hydrating", 50000.0, "Pelembab", "assets/photo/daily_moisturizer_hydrating.jpg"));
        predefinedProducts.put("8998800100032", new PredefinedProductInfo("Sunscreen SPF 50 PA+++", 45000.0, "Perlindungan Matahari", "assets/photo/sunscreen_spf_50.jpg"));
        predefinedProducts.put("8998800100049", new PredefinedProductInfo("Acne Spot Treatment", 40000.0, "Perawatan Jerawat", "assets/photo/acne_spot_treatment.jpg"));
        predefinedProducts.put("8998800100056", new PredefinedProductInfo("Brightening Serum Vit C", 60000.0, "Serum", "assets/photo/brightening_serum_vit_c.jpg"));
    }

    // Ubah konstruktor untuk menerima ProductDAO
    public InputProductForm(ProductDAO productDAO) {
        this.productDAO = productDAO; // Assign the ProductDAO
        setTitle("➕ Input Produk Baru");
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Custom JPanel sebagai background dengan gambar
        CustomPanel background = new CustomPanel("assets/logo_qeemla.png");
        setContentPane(background);
        background.setLayout(new BorderLayout());

        mainPanel = new JPanel(new GridBagLayout()); // Initialize mainPanel here
        mainPanel.setOpaque(false); // Agar background terlihat

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Form Tambah Produk");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(6, 94, 84));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Form Input
        gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(new JLabel("Barcode:"), gbc);
        tfBarcode = new JTextField(20);
        gbc.gridx = 1; mainPanel.add(tfBarcode, gbc);
        JButton scanBarcodeBtn = new JButton("Scan Barcode");
        scanBarcodeBtn.setBackground(new Color(6, 94, 84));
        scanBarcodeBtn.setForeground(Color.WHITE);
        scanBarcodeBtn.setFocusPainted(false);
        gbc.gridx = 2; mainPanel.add(scanBarcodeBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 2; mainPanel.add(new JLabel("Nama Produk:"), gbc);
        tfNama = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; mainPanel.add(tfNama, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        gbc.gridx = 0; gbc.gridy = 3; mainPanel.add(new JLabel("Harga:"), gbc);
        tfHarga = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; mainPanel.add(tfHarga, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4; mainPanel.add(new JLabel("Kategori:"), gbc);
        tfKategori = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; mainPanel.add(tfKategori, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 5; mainPanel.add(new JLabel("Tanggal Produksi:"), gbc);
        SpinnerDateModel dateModelProduksi = new SpinnerDateModel();
        spinnerProduksi = new JSpinner(dateModelProduksi);
        JSpinner.DateEditor dateEditorProduksi = new JSpinner.DateEditor(spinnerProduksi, "yyyy-MM-dd");
        spinnerProduksi.setEditor(dateEditorProduksi);
        gbc.gridx = 1; gbc.gridwidth = 2; mainPanel.add(spinnerProduksi, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 6; mainPanel.add(new JLabel("Tanggal Expired:"), gbc);
        SpinnerDateModel dateModelExpired = new SpinnerDateModel();
        spinnerExpired = new JSpinner(dateModelExpired);
        JSpinner.DateEditor dateEditorExpired = new JSpinner.DateEditor(spinnerExpired, "yyyy-MM-dd");
        spinnerExpired.setEditor(dateEditorExpired);
        gbc.gridx = 1; gbc.gridwidth = 2; mainPanel.add(spinnerExpired, gbc);
        gbc.gridwidth = 1;

        // Photo Input
        gbc.gridx = 0; gbc.gridy = 7; mainPanel.add(new JLabel("Foto Produk:"), gbc);
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setText("Tidak ada foto");
        gbc.gridx = 1; mainPanel.add(photoLabel, gbc);

        JButton choosePhotoBtn = new JButton("Pilih Foto");
        choosePhotoBtn.setBackground(new Color(6, 94, 84));
        choosePhotoBtn.setForeground(Color.WHITE);
        choosePhotoBtn.setFocusPainted(false);
        gbc.gridx = 2; mainPanel.add(choosePhotoBtn, gbc);

        // Webcam Controls
        webcamControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        webcamControlPanel.setOpaque(false);
        startWebcamBtn = new JButton("Mulai Webcam");
        stopWebcamBtn = new JButton("Berhenti Webcam");
        captureBtn = new JButton("Ambil Gambar");

        // Styling buttons
        startWebcamBtn.setBackground(new Color(46, 139, 87)); // SeaGreen
        startWebcamBtn.setForeground(Color.WHITE);
        startWebcamBtn.setFocusPainted(false);
        stopWebcamBtn.setBackground(new Color(178, 34, 34)); // FireBrick
        stopWebcamBtn.setForeground(Color.WHITE);
        stopWebcamBtn.setFocusPainted(false);
        captureBtn.setBackground(new Color(65, 105, 225)); // RoyalBlue
        captureBtn.setForeground(Color.WHITE);
        captureBtn.setFocusPainted(false);

        webcamControlPanel.add(startWebcamBtn);
        webcamControlPanel.add(stopWebcamBtn);
        webcamControlPanel.add(captureBtn);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        mainPanel.add(webcamControlPanel, gbc); // Add webcamControlPanel to mainPanel

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        JButton simpanBtn = new JButton("Simpan Produk");
        JButton batalBtn = new JButton("Batal");

        simpanBtn.setBackground(new Color(6, 94, 84));
        simpanBtn.setForeground(Color.WHITE);
        simpanBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        simpanBtn.setFocusPainted(false);
        batalBtn.setBackground(new Color(220, 20, 60)); // Crimson
        batalBtn.setForeground(Color.WHITE);
        batalBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        batalBtn.setFocusPainted(false);

        buttonPanel.add(simpanBtn);
        buttonPanel.add(batalBtn);

        // Add mainPanel to a JScrollPane for scrollability if content exceeds frame size
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Hilangkan border scroll pane

        background.add(scrollPane, BorderLayout.CENTER);
        background.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        scanBarcodeBtn.addActionListener(e -> startBarcodeScanner());
        choosePhotoBtn.addActionListener(e -> choosePhoto());
        simpanBtn.addActionListener(e -> simpanProduk());
        batalBtn.addActionListener(e -> dispose());

        startWebcamBtn.addActionListener(e -> startWebcam());
        stopWebcamBtn.addActionListener(e -> stopWebcam());
        captureBtn.addActionListener(e -> capturePhotoFromWebcam());

        // Add WindowListener to stop webcam when form is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopWebcam();
            }
        });

        tfBarcode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    fillProductInfo(tfBarcode.getText());
                }
            }
        });
    }

    private void startBarcodeScanner() {
        if (webcam == null || !webcam.isOpen()) { // Check if webcam is not open
            JOptionPane.showMessageDialog(this, "Webcam belum dimulai. Harap mulai webcam terlebih dahulu.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (barcodeScannerThread != null && barcodeScannerThread.isAlive()) {
            JOptionPane.showMessageDialog(this, "Scanner barcode sudah berjalan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        barcodeScannerThread = new Thread(() -> {
            boolean barcodeFound = false;
            while (!barcodeFound && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(100); // Tunggu sebentar
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt(); // Restore the interrupted status
                    break; // Keluar dari loop jika thread diinterupsi
                }

                Result result = null;
                BufferedImage image = null;

                if (webcam.isOpen()) {
                    image = webcam.getImage();
                }

                if (image != null) {
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    try {
                        result = new MultiFormatReader().decode(bitmap);
                    } catch (NotFoundException e) {
                        // Tidak ada barcode ditemukan, lanjutkan
                    }
                }

                if (result != null) {
                    String barcode = result.getText();
                    SwingUtilities.invokeLater(() -> {
                        tfBarcode.setText(barcode);
                        fillProductInfo(barcode);
                        // Optional: stop webcam after successful scan
                        // stopWebcam(); // Uncomment if you want to stop webcam automatically
                    });
                    barcodeFound = true; // Set flag to exit loop
                }
            }
            if (barcodeFound) {
                JOptionPane.showMessageDialog(InputProductForm.this, "Barcode berhasil dipindai: " + tfBarcode.getText(), "Scan Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(InputProductForm.this, "Scanning barcode dihentikan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        barcodeScannerThread.start();
        JOptionPane.showMessageDialog(this, "Mulai scanning barcode...", "Info", JOptionPane.INFORMATION_MESSAGE);
    }


    private void fillProductInfo(String barcode) {
        PredefinedProductInfo info = predefinedProducts.get(barcode);
        if (info != null) {
            tfNama.setText(info.name);
            tfHarga.setText(String.valueOf(info.price));
            tfKategori.setText(info.category);
            // Load photo if available
            if (info.imagePath != null && !info.imagePath.isEmpty()) {
                File photoFile = new File(info.imagePath);
                if (photoFile.exists()) {
                    selectedPhoto = photoFile;
                    displayPhoto(selectedPhoto);
                } else {
                    System.err.println("Predefined photo not found: " + info.imagePath);
                    photoLabel.setIcon(null);
                    photoLabel.setText("Foto tidak ditemukan");
                }
            } else {
                photoLabel.setIcon(null);
                photoLabel.setText("Tidak ada foto");
            }
        } else {
            tfNama.setText("");
            tfHarga.setText("");
            tfKategori.setText("");
            photoLabel.setIcon(null);
            photoLabel.setText("Tidak ada foto");
            JOptionPane.showMessageDialog(this, "Produk dengan barcode " + barcode + " tidak ditemukan di data predefinisi.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void startWebcam() {
        if (webcam == null) {
            webcam = Webcam.getDefault();
            if (webcam != null) {
                webcam.setViewSize(WebcamResolution.VGA.getSize()); // Mengatur ukuran tampilan
                webcamPanel = new WebcamPanel(webcam);
                webcamPanel.setFPSDisplayed(true);
                webcamPanel.setDisplayDebugInfo(false);
                webcamPanel.setImageSizeDisplayed(false);
                webcamPanel.setMirrored(true); // Membalik gambar agar seperti cermin

                // Remove existing webcam control panel from mainPanel
                mainPanel.remove(webcamControlPanel);
                
                // Add webcamPanel to the appropriate position in mainPanel
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 9; // Letakkan di bawah kontrol webcam lama
                gbc.gridwidth = 3;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.insets = new Insets(8, 8, 8, 8);
                
                mainPanel.add(webcamPanel, gbc); // Corrected: Add webcamPanel directly to mainPanel

                // Re-add the webcamControlPanel below the webcamPanel
                gbc.gridx = 0;
                gbc.gridy = 10; // Geser ke bawah, di bawah webcamPanel
                gbc.gridwidth = 3;
                gbc.weightx = 0;
                gbc.weighty = 0;
                mainPanel.add(webcamControlPanel, gbc);

                // Revalidate and repaint mainPanel to update UI
                mainPanel.revalidate();
                mainPanel.repaint();

                // Start webcam in a separate thread to avoid freezing the UI
                new Thread(() -> {
                    webcam.open();
                }).start();
            } else {
                JOptionPane.showMessageDialog(this, "Tidak ada webcam ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Webcam sudah berjalan.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void stopWebcam() {
        if (webcam != null && webcam.isOpen()) {
            if (barcodeScannerThread != null && barcodeScannerThread.isAlive()) {
                barcodeScannerThread.interrupt(); // Menghentikan thread scanner barcode
                barcodeScannerThread = null;
            }
            webcam.close();
            // Remove webcamPanel from its parent (mainPanel)
            if (webcamPanel != null) {
                mainPanel.remove(webcamPanel); // Corrected: Remove webcamPanel directly from mainPanel
                webcamPanel = null;
            }

            // Restore webcamControlPanel's visibility and position if needed
            // (You might want to just hide webcamPanel and leave controls in place)
            // For now, let's just revalidate and repaint the main panel to reflect changes
            
            // Revalidate and repaint mainPanel to update UI
            mainPanel.revalidate();
            mainPanel.repaint();
            JOptionPane.showMessageDialog(this, "Webcam berhasil dihentikan.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Webcam tidak aktif.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void capturePhotoFromWebcam() {
        if (webcam != null && webcam.isOpen()) {
            try {
                BufferedImage image = webcam.getImage();
                if (image != null) {
                    // Buat folder 'uploads' jika belum ada
                    File uploadsDir = new File("uploads");
                    if (!uploadsDir.exists()) {
                        uploadsDir.mkdirs();
                    }

                    // Tentukan nama file yang unik (misalnya, berdasarkan timestamp)
                    String fileName = "product_" + System.currentTimeMillis() + ".png";
                    selectedPhoto = new File(uploadsDir, fileName);

                    // Simpan gambar
                    javax.imageio.ImageIO.write(image, "PNG", selectedPhoto);
                    displayPhoto(selectedPhoto);
                    JOptionPane.showMessageDialog(this, "Foto berhasil diambil!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengambil gambar dari webcam.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saat mengambil foto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Webcam tidak aktif.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void choosePhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Foto Produk");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPhoto = fileChooser.getSelectedFile();
            displayPhoto(selectedPhoto);
        }
    }

    private void displayPhoto(File photoFile) {
        if (photoFile != null && photoFile.exists()) {
            try {
                ImageIcon originalIcon = new ImageIcon(photoFile.getAbsolutePath());
                Image img = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(img));
                photoLabel.setText(""); // Hapus teks "Tidak ada foto"
            } catch (Exception e) {
                photoLabel.setIcon(null);
                photoLabel.setText("Gagal memuat foto");
                System.err.println("Error displaying photo: " + e.getMessage());
            }
        } else {
            photoLabel.setIcon(null);
            photoLabel.setText("Tidak ada foto");
        }
    }

    private void simpanProduk() {
        String code = tfBarcode.getText();
        String nama = tfNama.getText();
        String hargaStr = tfHarga.getText();
        String kategori = tfKategori.getText();
        Date productionDate = (Date) spinnerProduksi.getValue();
        Date expiryDate = (Date) spinnerExpired.getValue();
        String photoPath = (selectedPhoto != null) ? selectedPhoto.getAbsolutePath() : null;

        // Validasi input
        if (code.isEmpty() || nama.isEmpty() || hargaStr.isEmpty() || kategori.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double harga;
        try {
            harga = Double.parseDouble(hargaStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buat objek Product
        Product newProduct = new Product(code, nama, harga, kategori, productionDate, expiryDate, photoPath);

        try {
            productDAO.addProduct(newProduct); // Simpan produk ke database
            JOptionPane.showMessageDialog(this, "Produk berhasil disimpan ke database!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Kosongkan form setelah simpan
            tfBarcode.setText("");
            tfNama.setText("");
            tfHarga.setText("");
            tfKategori.setText("");
            spinnerProduksi.setValue(new Date()); // Reset ke tanggal hari ini
            spinnerExpired.setValue(new Date()); // Reset ke tanggal hari ini
            photoLabel.setIcon(null);
            photoLabel.setText("Tidak ada foto");
            selectedPhoto = null;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan produk: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Custom JPanel untuk background
    class CustomPanel extends JPanel {
        private Image backgroundImage;

        public CustomPanel(String imagePath) {
            try {
                URL imageUrl = getClass().getClassLoader().getResource(imagePath);
                if (imageUrl != null) {
                    backgroundImage = new ImageIcon(imageUrl).getImage();
                } else {
                    System.err.println("Background image not found (classpath): " + imagePath);
                    // Try direct path as a fallback if not in resources
                    File directFile = new File(imagePath);
                    if (directFile.exists()){
                        backgroundImage = new ImageIcon(directFile.getAbsolutePath()).getImage();
                    } else {
                        System.err.println("Background image not found (direct path): " + imagePath);
                        backgroundImage = null;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading background image: " + imagePath);
                e.printStackTrace();
                backgroundImage = null;
            }
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
