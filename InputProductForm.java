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

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class InputProductForm extends JFrame {
    private JTextField tfBarcode, tfNama, tfHarga, tfKategori;
    private JSpinner spinnerProduksi, spinnerExpired;
    private List<Product> productList;
    private JLabel photoLabel;
    private File selectedPhoto;

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

    private static final Map<String, PredefinedProductInfo> PREDEFINED_PRODUCTS = new HashMap<>();
    static {
       
        //ini buat barcode 
    
        PREDEFINED_PRODUCTS.put("3211", new PredefinedProductInfo("Serum Pencerah Wajah Pro", 175000, "Serum", "assets/product_serum_alpha.png"));
        PREDEFINED_PRODUCTS.put("3212", new PredefinedProductInfo("Lotion Badan Lembap Extra", 95000, "Lotion", "assets/product_lotion_berry.png"));
        PREDEFINED_PRODUCTS.put("8992772090012", new PredefinedProductInfo("Facial Wash Brightening", 65000, "Pembersih Wajah", "assets/product_facewash_bright.png"));
        
    }
  

    public InputProductForm(List<Product> productList) {
        this.productList = productList;

        setTitle("Form Input Produk");
        setSize(550, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel background = new BackgroundPanel("assets/logoQeemla.png");
        setContentPane(background);
        background.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Input Produk Baru");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(6, 94, 84));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        tfBarcode = new JTextField(15);
        tfNama = new JTextField(15);
        tfHarga = new JTextField(15);
        tfKategori = new JTextField(15);

        spinnerProduksi = new JSpinner(new SpinnerDateModel());
        spinnerProduksi.setEditor(new JSpinner.DateEditor(spinnerProduksi, "yyyy-MM-dd"));

        spinnerExpired = new JSpinner(new SpinnerDateModel());
        spinnerExpired.setEditor(new JSpinner.DateEditor(spinnerExpired, "yyyy-MM-dd"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblBarcode = new JLabel("Kode Barcode");
        lblBarcode.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBarcode.setForeground(Color.BLACK);
        formPanel.add(lblBarcode, gbc);

        gbc.gridx = 1;
        formPanel.add(tfBarcode, gbc);

        JPanel barcodeButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        barcodeButtonPanel.setOpaque(false);
        JButton btnScanBarcode = new JButton("Scan");
        btnScanBarcode.setToolTipText("Scan Barcode via Webcam");
        btnScanBarcode.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnScanBarcode.setMargin(new Insets(2, 5, 2, 5));
        btnScanBarcode.addActionListener(e -> openWebcamScanner());
        barcodeButtonPanel.add(btnScanBarcode);

        JButton btnUploadBarcode = new JButton("Upload");
        btnUploadBarcode.setToolTipText("Upload Barcode Image");
        btnUploadBarcode.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        btnUploadBarcode.setMargin(new Insets(2, 5, 2, 5));
        btnUploadBarcode.addActionListener(e -> openUploadDialog());
        barcodeButtonPanel.add(btnUploadBarcode);

        gbc.gridx = 2;
        formPanel.add(barcodeButtonPanel, gbc);

        String[] labels = {"Nama Produk", "Harga", "Kategori", "Tgl Produksi", "Tgl Expired"};
        Component[] inputs = {tfNama, tfHarga, tfKategori, spinnerProduksi, spinnerExpired};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbl.setForeground(Color.BLACK);
            formPanel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.gridwidth = 2;
            formPanel.add(inputs[i], gbc);
            gbc.gridwidth = 1;
        }

        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        JLabel photoText = new JLabel("Foto Produk");
        photoText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        photoText.setForeground(Color.BLACK);
        formPanel.add(photoText, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        JButton btnPhoto = new JButton("Pilih Gambar");
        btnPhoto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnPhoto.addActionListener(e -> pilihFoto());
        formPanel.add(btnPhoto, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(100, 100));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setVerticalAlignment(JLabel.CENTER);
        formPanel.add(photoLabel, gbc);
        gbc.gridwidth = 1;

        mainPanel.add(formPanel);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBackground(new Color(6, 94, 84));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSimpan.setMaximumSize(new Dimension(150, 40));
        btnSimpan.addActionListener(e -> simpanProduct());

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(btnSimpan);

        background.add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void processBarcodeData(String barcodeData) {
        String cleanBarcodeData = barcodeData.trim();
        PredefinedProductInfo productInfo = PREDEFINED_PRODUCTS.get(cleanBarcodeData);

        if (productInfo != null) {
            tfBarcode.setText(cleanBarcodeData);
            tfNama.setText(productInfo.name);
            tfHarga.setText(String.valueOf(productInfo.price));
            tfKategori.setText(productInfo.category);

            // Set data
            LocalDate today = LocalDate.now();
            LocalDate expirationDate = today.plusYears(1);
            spinnerProduksi.setValue(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            spinnerExpired.setValue(Date.from(expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            // Set photo
            selectedPhoto = null; 
            photoLabel.setIcon(null);

            try {
                // pilih barcode dari komputer
                URL imageUrl = getClass().getClassLoader().getResource(productInfo.imagePath);
                if (imageUrl != null) {
                    selectedPhoto = new File(imageUrl.toURI()); 
                    ImageIcon icon = new ImageIcon(imageUrl);
                    Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    photoLabel.setIcon(new ImageIcon(img));
                } else {
                    File imageFile = new File(productInfo.imagePath);
                    if (imageFile.exists()) {
                        selectedPhoto = imageFile;
                        ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        photoLabel.setIcon(new ImageIcon(img));
                    } else {
                         System.err.println("Image not found for product " + cleanBarcodeData + " at path: " + productInfo.imagePath);
                         JOptionPane.showMessageDialog(this, "Data produk ditemukan, tetapi gambar produk tidak ditemukan di: " + productInfo.imagePath, "Peringatan Gambar", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error loading image for product " + cleanBarcodeData + ": " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Data produk ditemukan, tetapi terjadi kesalahan saat memuat gambar.", "Error Gambar", JOptionPane.ERROR_MESSAGE);
            }

            JOptionPane.showMessageDialog(this, "Produk '" + productInfo.name + "' dimuat otomatis.", "Barcode Terdeteksi", JOptionPane.INFORMATION_MESSAGE);
        } else {
            
            tfBarcode.setText(cleanBarcodeData);
            tfNama.setText(""); 
            tfHarga.setText("");
            tfKategori.setText("");
            photoLabel.setIcon(null);
            selectedPhoto = null;

            LocalDate today = LocalDate.now();
            LocalDate expirationDate = today.plusYears(1);
            spinnerProduksi.setValue(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            spinnerExpired.setValue(Date.from(expirationDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            JOptionPane.showMessageDialog(this, "Kode Barcode: " + cleanBarcodeData + "\nProduk tidak terdefinisi, silakan lengkapi data.\nTgl Produksi & Expired otomatis diatur.", "Barcode Tidak Dikenal", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openWebcamScanner() {
        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada webcam terdeteksi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Dimension viewSize = new Dimension(640, 480);
        webcam.setViewSize(viewSize);
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setPreferredSize(viewSize);

        JFrame window = new JFrame("Scan Barcode");
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.pack();
        window.setLocationRelativeTo(this);
        window.setVisible(true);

        new Thread(() -> {
            boolean scanned = false;
            while (webcam.isOpen() && !scanned && window.isVisible()) {
                try {
                    BufferedImage image = webcam.getImage();
                    if (image == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Map<DecodeHintType, Object> hints = new java.util.HashMap<>();
                    hints.put(DecodeHintType.POSSIBLE_FORMATS, java.util.Arrays.asList(BarcodeFormat.CODE_128, BarcodeFormat.CODE_39, BarcodeFormat.EAN_13, BarcodeFormat.UPC_A, BarcodeFormat.QR_CODE));
                    hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                    Result result = new MultiFormatReader().decode(bitmap, hints);
                    if (result != null) {
                        String barcodeData = result.getText();
                        Toolkit.getDefaultToolkit().beep();
                        SwingUtilities.invokeLater(() -> processBarcodeData(barcodeData));
                        scanned = true;
                        webcam.close();
                        window.dispose();
                    }
                    Thread.sleep(200);
                } catch (NotFoundException e) { /* Continue scanning */ }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    webcam.close(); window.dispose(); break;
                }
                catch (Exception e) { e.printStackTrace(); }
            }
            if (webcam.isOpen()) webcam.close();
            if(window.isVisible()) window.dispose();
        }).start();
    }

    private void openUploadDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih Gambar Barcode");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedImage image = javax.imageio.ImageIO.read(selectedFile);
                if (image == null) {
                    JOptionPane.showMessageDialog(this, "File bukan gambar yang valid.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Map<DecodeHintType, Object> hints = new java.util.HashMap<>();
                hints.put(DecodeHintType.POSSIBLE_FORMATS, java.util.Arrays.asList(BarcodeFormat.CODE_128, BarcodeFormat.CODE_39, BarcodeFormat.EAN_13, BarcodeFormat.UPC_A, BarcodeFormat.QR_CODE));
                hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                Result decodeResult = new MultiFormatReader().decode(bitmap, hints);
                String barcodeData = decodeResult.getText();
                Toolkit.getDefaultToolkit().beep();
                processBarcodeData(barcodeData);
            } catch (NotFoundException e) {
                JOptionPane.showMessageDialog(this, "Barcode tidak ditemukan di gambar.", "Error Barcode", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memproses gambar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pilihFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Foto Produk");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPhoto = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedPhoto.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(img));
        }
    }

    private void simpanProduct() {
        try {
            String kode = tfBarcode.getText().trim();
            String nama = tfNama.getText().trim();
            if (kode.isEmpty() || nama.isEmpty() || tfHarga.getText().trim().isEmpty() || tfKategori.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi (kecuali foto jika memang tidak ada)!", "Input Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
                return;
            }
        
            double harga = Double.parseDouble(tfHarga.getText().trim());
            String kategori = tfKategori.getText().trim();
            Date tglProduksi = (Date) spinnerProduksi.getValue();
            Date tglExpired = (Date) spinnerExpired.getValue();

            if (tglExpired.before(tglProduksi)) {
                JOptionPane.showMessageDialog(this, "Tanggal Expired tidak boleh sebelum Tanggal Produksi.", "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String photoPath = selectedPhoto != null ? selectedPhoto.getAbsolutePath() : null;
            Product p = new Product(kode, nama, harga, kategori, tglProduksi, tglExpired, photoPath);
            productList.add(p);

            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!\nKode: " + kode + "\nNama: " + nama +
                    (photoPath != null ? "\nFoto: " + new File(photoPath).getName() : "\nFoto: Tidak ada"));

            //  Hapus bidang untuk input berikutnya
            tfBarcode.setText("");
            tfNama.setText("");
            tfHarga.setText("");
            tfKategori.setText("");
            spinnerProduksi.setValue(new Date());
            spinnerExpired.setValue(Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            photoLabel.setIcon(null);
            selectedPhoto = null;
            tfBarcode.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Harga tidak valid. Harap masukkan angka.", "Input Tidak Valid", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan produk: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        public BackgroundPanel(String imagePath) {
            try {
                URL imgUrl = getClass().getClassLoader().getResource(imagePath);
                if (imgUrl != null) {
                    backgroundImage = new ImageIcon(imgUrl).getImage();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<Product> dummyProductList = new java.util.ArrayList<>();
            new InputProductForm(dummyProductList).setVisible(true);
        });
    }
}
