// File: InputProductForm.java

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
import java.time.temporal.ChronoUnit; 
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;
import javax.imageio.ImageIO; //  import ini untuk ImageIO
import java.text.SimpleDateFormat;
import java.util.Calendar; 

// import github
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
    // --- PENAMBAHAN DEKLARASI tfStok ---
    private JTextField tfBarcode, tfNama, tfHarga, tfKategori, tfStok;
    private JSpinner spinnerProduksi, spinnerExpired;
    private ProductDAO productDAO;
    private JLabel photoLabel;
    private File selectedPhoto;

    private Webcam webcam = null;
    private WebcamPanel webcamPanel = null;
    private Thread barcodeScannerThread = null;
    private JButton startWebcamBtn, stopWebcamBtn, captureBtn;
    private JPanel webcamControlPanel;
    private JPanel contentPanel;
    private boolean isEditMode = false;
    private String originalCode = null;
    private String emailLogin; 

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
        predefinedProducts.put("8998800100018", new PredefinedProductInfo("Facial Wash Brightening", 35000.0, "Pembersih Wajah", "assets/facialwash_qeemla_skin.jpg"));
        predefinedProducts.put("8998800100025", new PredefinedProductInfo("Serum Qeemla Skin", 50000.0, "Pelembab", "assets/booster serum qeemla skin.png"));
        predefinedProducts.put("8998800100032", new PredefinedProductInfo("Sunscreen SPF 50 PA+++", 45000.0, "Perlindungan Matahari", "assets/day cream qeemla skin.png"));
        predefinedProducts.put("8998800100049", new PredefinedProductInfo("Toner", 40000.0, "Perawatan Jerawat", "assets/toner qeemla skin.png"));
    }

    public InputProductForm(ProductDAO productDAO, String emailLogin) {
    super(); // Memanggil constructor JFrame
    this.productDAO = productDAO;
    this.emailLogin = emailLogin;

    setTitle("➕ Input Produk Baru");
    setSize(800, 750);
    setMinimumSize(new Dimension(800, 750));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    initComponents(); // panggil UI builder
}
    // Constructor untuk mode edit produk
    public InputProductForm(ProductDAO productDAO, Product productToEdit, String emailLogin) {
    super(); // Memanggil constructor JFrame
    this.productDAO = productDAO;
    this.emailLogin = emailLogin;
    this.isEditMode = true;
    this.originalCode = productToEdit.getCode();

    setTitle("✏️ Edit Produk");
    setSize(800, 750);
    setMinimumSize(new Dimension(800, 750));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    initComponents(); // panggil UI builder

    tfBarcode.setText(productToEdit.getCode());
    tfBarcode.setEditable(false);
    loadProductData(productToEdit);
}

    private void initComponents() { //untuk bagian belakang background 
        CustomPanel background = new CustomPanel("assets/logo_qeemla.png");
        setContentPane(background);
        background.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Form Tambah Produk");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(6, 94, 84));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        contentPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1; contentPanel.add(new JLabel("Barcode:"), gbc);
        tfBarcode = new JTextField(20);
        gbc.gridx = 1; contentPanel.add(tfBarcode, gbc);
        JButton scanBarcodeBtn = new JButton("Scan Barcode");
        styleButton(scanBarcodeBtn, new Color(6, 94, 84), Color.WHITE);
        gbc.gridx = 2; contentPanel.add(scanBarcodeBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 2; contentPanel.add(new JLabel("Nama Produk:"), gbc);
        tfNama = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; contentPanel.add(tfNama, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; contentPanel.add(new JLabel("Harga:"), gbc);
        tfHarga = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; contentPanel.add(tfHarga, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 4; contentPanel.add(new JLabel("Kategori:"), gbc);
        tfKategori = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; contentPanel.add(tfKategori, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 5; contentPanel.add(new JLabel("Stok:"), gbc);
        tfStok = new JTextField(20);
        gbc.gridx = 1; gbc.gridwidth = 2; contentPanel.add(tfStok, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 6; contentPanel.add(new JLabel("Tanggal Produksi:"), gbc);
        spinnerProduksi = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spinnerProduksi.setEditor(new JSpinner.DateEditor(spinnerProduksi, "yyyy-MM-dd"));
        gbc.gridx = 1; gbc.gridwidth = 2; contentPanel.add(spinnerProduksi, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 7; contentPanel.add(new JLabel("Tanggal Expired:"), gbc);
        spinnerExpired = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spinnerExpired.setEditor(new JSpinner.DateEditor(spinnerExpired, "yyyy-MM-dd"));
        gbc.gridx = 1; gbc.gridwidth = 2; contentPanel.add(spinnerExpired, gbc); gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 8; contentPanel.add(new JLabel("Foto Produk:"), gbc);
        photoLabel = new JLabel("Tidak ada foto", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 1; contentPanel.add(photoLabel, gbc);

        JButton choosePhotoBtn = new JButton("Pilih Foto");
        styleButton(choosePhotoBtn, new Color(6, 94, 84), Color.WHITE);
        gbc.gridx = 2; contentPanel.add(choosePhotoBtn, gbc);

        webcamControlPanel = new JPanel(new FlowLayout());
        webcamControlPanel.setOpaque(false);
        startWebcamBtn = new JButton("Mulai Webcam");
        stopWebcamBtn = new JButton("Berhenti Webcam");
        captureBtn = new JButton("Ambil Gambar");

        styleButton(startWebcamBtn, new Color(46, 139, 87), Color.WHITE);
        styleButton(stopWebcamBtn, new Color(178, 34, 34), Color.WHITE);
        styleButton(captureBtn, new Color(65, 105, 225), Color.WHITE);

        webcamControlPanel.add(startWebcamBtn);
        webcamControlPanel.add(stopWebcamBtn);
        webcamControlPanel.add(captureBtn);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 3;
        contentPanel.add(webcamControlPanel, gbc);

        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.QVGA.getSize());
            webcamPanel = new WebcamPanel(webcam);
            gbc.gridx = 0; gbc.gridy = 10;
            gbc.gridwidth = 3;
            gbc.fill = GridBagConstraints.CENTER;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            contentPanel.add(webcamPanel, gbc);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        JButton simpanBtn = new JButton("Simpan Produk");
        JButton batalBtn = new JButton("Batal");
        styleButton(simpanBtn, new Color(6, 94, 84), Color.WHITE);
        styleButton(batalBtn, new Color(220, 20, 60), Color.WHITE);
        buttonPanel.add(simpanBtn);
        buttonPanel.add(batalBtn);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        background.add(scrollPane, BorderLayout.CENTER);
        background.add(buttonPanel, BorderLayout.SOUTH);

        scanBarcodeBtn.addActionListener(e -> startBarcodeScanner());
        choosePhotoBtn.addActionListener(e -> choosePhoto());
        simpanBtn.addActionListener(e -> simpanProduk());
        batalBtn.addActionListener(e -> dispose());

        startWebcamBtn.addActionListener(e -> startWebcam());
        stopWebcamBtn.addActionListener(e -> stopWebcam());
        captureBtn.addActionListener(e -> capturePhotoFromWebcam());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                stopWebcam();
            }
        });

        tfBarcode.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    fillProductInfo(tfBarcode.getText());
                }
            }
        });
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private void startBarcodeScanner() {
        if (webcam == null || !webcam.isOpen()) {
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
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
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
                        // No barcode found
                    }
                }

                if (result != null) {
                    String barcode = result.getText();
                    SwingUtilities.invokeLater(() -> {
                        tfBarcode.setText(barcode);
                        fillProductInfo(barcode);
                    });
                    barcodeFound = true;
                }
            }
            if (barcodeFound) {
                JOptionPane.showMessageDialog(InputProductForm.this, "Barcode berhasil dipindai: " + tfBarcode.getText(), "Scan Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else if (!Thread.currentThread().isInterrupted()) {
                JOptionPane.showMessageDialog(InputProductForm.this, "Scanning barcode dihentikan.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        barcodeScannerThread.start();
        JOptionPane.showMessageDialog(this, "Mulai scanning barcode...", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // Ganti metode fillProductInfo Anda dengan yang ini
    private void fillProductInfo(String barcode) {
    PredefinedProductInfo info = predefinedProducts.get(barcode);
    if (info != null) {
        // Mengisi field yang sudah ada
        tfNama.setText(info.name);
        tfHarga.setText(String.valueOf(info.price));
        tfKategori.setText(info.category);

        // --- LOGIKA PENGATURAN TANGGAL OTOMATIS ---      
        
        // Tanggal hari ini
        Date today = new Date();
        
        // Atur tanggal produksi ke hari ini
        spinnerProduksi.setValue(today);

        // Hitung tanggal satu tahun dari sekarang untuk tanggal expired
        Calendar cal = Calendar.getInstance();
        cal.setTime(today); // Mulai dari hari ini
        cal.add(Calendar.YEAR, 1); // Tambahkan 1 tahun
        Date expiryDate = cal.getTime(); // Dapatkan hasilnya sebagai objek Date

        // Atur tanggal expired
        spinnerExpired.setValue(expiryDate);

        // Logika untuk menampilkan foto (tidak berubah)
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
        // Jika barcode tidak ditemukan, kosongkan semua field
        tfNama.setText("");
        tfHarga.setText("");
        tfKategori.setText("");
        photoLabel.setIcon(null);
        photoLabel.setText("Tidak ada foto");
        // Kosongkan juga tanggal atau set ke default
        spinnerProduksi.setValue(new Date());
        spinnerExpired.setValue(new Date());
        JOptionPane.showMessageDialog(this, "Produk dengan barcode " + barcode + " tidak ditemukan di data predefinisi.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}

    private void startWebcam() {
       if (webcam != null && !webcam.isOpen()) {
            new Thread(() -> {
                webcam.open();
                SwingUtilities.invokeLater(() -> {
                    if (webcamPanel != null) webcamPanel.start();
                    JOptionPane.showMessageDialog(this, "Webcam berhasil dimulai.", "Info", JOptionPane.INFORMATION_MESSAGE);
                });
            }).start();
        } else if (webcam == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada webcam ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Webcam sudah berjalan.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void stopWebcam() {
        if (barcodeScannerThread != null && barcodeScannerThread.isAlive()) {
            barcodeScannerThread.interrupt();
            barcodeScannerThread = null;
        }
        if (webcam != null && webcam.isOpen()) {
            new Thread(() -> {
                if (webcamPanel != null) webcamPanel.stop();
                webcam.close();
                SwingUtilities.invokeLater(() -> {
                     JOptionPane.showMessageDialog(this, "Webcam berhasil dihentikan.", "Info", JOptionPane.INFORMATION_MESSAGE);
                });
            }).start();
        }
    }

    private void capturePhotoFromWebcam() {
        if (webcam != null && webcam.isOpen()) {
            try {
                BufferedImage image = webcam.getImage();
                if (image != null) {
                    File uploadsDir = new File("uploads");
                    if (!uploadsDir.exists()) {
                        uploadsDir.mkdirs();
                    }
                    String fileName = "product_" + System.currentTimeMillis() + ".png";
                    selectedPhoto = new File(uploadsDir, fileName);
                    ImageIO.write(image, "PNG", selectedPhoto);
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
                photoLabel.setText("");
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
    String stokStr = tfStok.getText();
    Date productionDate = (Date) spinnerProduksi.getValue();
    Date expiryDate = (Date) spinnerExpired.getValue();
    String photoPath = (selectedPhoto != null) ? selectedPhoto.getAbsolutePath() : null;

    // Validasi input
    if (code.isEmpty() || nama.isEmpty() || hargaStr.isEmpty() || kategori.isEmpty() || stokStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua field (termasuk Stok) harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    double harga;
    int stok;
    try {
        harga = Double.parseDouble(hargaStr);
        stok = Integer.parseInt(stokStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Harga dan Stok harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        Product newProduct = new Product(code, nama, kategori, harga, stok, productionDate, expiryDate, photoPath);
        LogDAO logDAO = new LogDAO(); // Inisialisasi LogDAO

        if (isEditMode) {
            productDAO.updateProduct(newProduct);
            JOptionPane.showMessageDialog(this, "Produk berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Simpan log edit
            logDAO.simpanLog("Edit Produk", "Produk " + nama + " berhasil diperbarui", emailLogin);

        } else {
            productDAO.addProduct(newProduct);
            JOptionPane.showMessageDialog(this, "Produk berhasil disimpan ke database!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Simpan log tambah
            logDAO.simpanLog("Tambah Produk", "Produk " + nama + " berhasil ditambahkan", emailLogin);
        }

        clearForm();
        // Optional: dispose();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error saat menyimpan produk: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

    private void clearForm() {
        tfBarcode.setText("");
        tfNama.setText("");
        tfHarga.setText("");
        tfKategori.setText("");
        // --- MEMBERSIHKAN FIELD STOK ---
        tfStok.setText("");
        spinnerProduksi.setValue(new Date());
        spinnerExpired.setValue(new Date());
        photoLabel.setIcon(null);
        photoLabel.setText("Tidak ada foto");
        selectedPhoto = null;
        tfBarcode.setEditable(true);
        isEditMode = false;
        originalCode = null;
    }

    private void loadProductData(Product p) {
        tfBarcode.setText(p.getCode());
        tfNama.setText(p.getName());
        tfKategori.setText(p.getCategory());
        tfHarga.setText(String.valueOf(p.getPrice()));
        // --- MEMUAT DATA STOK KE FORM ---
        tfStok.setText(String.valueOf(p.getStock()));
        spinnerProduksi.setValue(p.getProductionDate());
        spinnerExpired.setValue(p.getExpiryDate());
        
        originalCode = p.getCode();

        if (p.getPhotoPath() != null) {
            selectedPhoto = new File(p.getPhotoPath());
            displayPhoto(selectedPhoto);
        }
    }

    public class CustomPanel extends JPanel {
        private Image backgroundImage;

        public CustomPanel(String imagePath) {
            try {
                URL imageUrl = getClass().getClassLoader().getResource(imagePath);
                if (imageUrl != null) {
                    backgroundImage = new ImageIcon(imageUrl).getImage();
                } else {
                    System.err.println("Background image not found (classpath): " + imagePath);
                    File directFile = new File(imagePath);
                    if (directFile.exists()) {
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
