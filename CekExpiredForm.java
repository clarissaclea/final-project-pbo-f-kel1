import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.awt.Dimension;

public class CekExpiredForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public CekExpiredForm(List<Product> allProducts) {
        setTitle("⏰ Produk Kedaluwarsa");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabel produk kadaluwarsa
        model = new DefaultTableModel(new String[]{"Kode", "Nama", "Exp", "Status"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);

        // Filter hanya produk yang sudah kadaluwarsa
        for (Product p : allProducts) {
            String status = getStatusKadaluarsa(p.getExpiryDate());
            if (status.contains("Kedaluwarsa")) {
                model.addRow(new Object[]{p.getCode(), p.getName(), p.getExpiryDate(), status});
            }
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tombol untuk scan barcode
        JButton scanButton = new JButton("Scan Barcode");
        scanButton.addActionListener(e -> openWebcamScanner());

        // Tombol untuk upload gambar barcode
        JButton uploadButton = new JButton("Upload Barcode");
        uploadButton.addActionListener(e -> openUploadDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(scanButton);
        buttonPanel.add(uploadButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void openWebcamScanner() {
        Webcam webcam = Webcam.getDefault();

        if (webcam == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada webcam terdeteksi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Dimension size = new Dimension(640, 480);
        webcam.setViewSize(size);
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);

        JFrame window = new JFrame("Scan Barcode");
        window.add(panel);
        window.setResizable(true);
        window.pack();

        // Set size dan posisi supaya lebih pas
        window.setSize(700, 550);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);

        new Thread(() -> {
            boolean scanned = false;

            while (!scanned) {
                try {
                    BufferedImage image = webcam.getImage();
                    if (image == null) {
                        System.out.println("[DEBUG] Gagal mendapatkan gambar dari webcam.");
                        Thread.sleep(100);
                        continue;
                    } else {
                        System.out.println("[DEBUG] Gambar webcam berhasil ditangkap.");
                        System.out.println("[DEBUG] Mencoba mendeteksi barcode...");
                    }

                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
Map<DecodeHintType, Object> hints = new java.util.HashMap<>();
hints.put(DecodeHintType.POSSIBLE_FORMATS, java.util.Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_128, BarcodeFormat.CODE_39));
hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

Result result = new MultiFormatReader().decode(bitmap, hints);
                    if (result != null) {
                        String data = result.getText(); // format: Kode|Nama|YYYY-MM-DD
                        System.out.println("Barcode terbaca: " + data);

                        String[] parts = data.split("\\|");

                        if (parts.length == 3) {
                            String kode = parts[0];
                            String namaProduk = parts[1];
                            LocalDate tanggal = LocalDate.parse(parts[2], DateTimeFormatter.ISO_DATE);
                            String status = tanggal.isBefore(LocalDate.now())
                                    ? "❌ Sudah Kedaluwarsa"
                                    : "✅ Belum Kedaluwarsa";

                            Toolkit.getDefaultToolkit().beep(); // Bunyi bip
                            JOptionPane.showMessageDialog(window, "Produk " + namaProduk + " " + status);

                            // Tambahkan langsung ke tabel
                            SwingUtilities.invokeLater(() -> {
                                model.addRow(new Object[]{
                                        kode,
                                        namaProduk,
                                        java.sql.Date.valueOf(tanggal),
                                        status
                                });
                            });

                        } else {
                            JOptionPane.showMessageDialog(window, "Format barcode tidak valid.\nGunakan: Kode|Nama|YYYY-MM-DD");
                        }

                        scanned = true;
                        webcam.close();
                        window.dispose();
                    }

                    Thread.sleep(300);
                } catch (NotFoundException e) {
                    // barcode tidak ditemukan, lanjut saja
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void openUploadDialog() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage image = javax.imageio.ImageIO.read(fileChooser.getSelectedFile());
                if (image == null) {
                    JOptionPane.showMessageDialog(this, "File bukan gambar yang valid.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                Result decodeResult = new MultiFormatReader().decode(bitmap);
                String data = decodeResult.getText(); // format: Kode|Nama|YYYY-MM-DD

                System.out.println("Barcode terbaca dari upload: " + data);

                String[] parts = data.split("\\|");
                if (parts.length == 3) {
                    String kode = parts[0];
                    String namaProduk = parts[1];
                    LocalDate tanggal = LocalDate.parse(parts[2], DateTimeFormatter.ISO_DATE);
                    String status = tanggal.isBefore(LocalDate.now())
                            ? "❌ Sudah Kedaluwarsa"
                            : "✅ Belum Kedaluwarsa";

                    Toolkit.getDefaultToolkit().beep(); // Bunyi bip
                    JOptionPane.showMessageDialog(this, "Produk " + namaProduk + " " + status);

                    // Tambahkan langsung ke tabel
                    model.addRow(new Object[]{
                            kode,
                            namaProduk,
                            java.sql.Date.valueOf(tanggal),
                            status
                    });

                } else {
                    JOptionPane.showMessageDialog(this, "Format barcode tidak valid.\nGunakan: Kode|Nama|YYYY-MM-DD");
                }

            } catch (NotFoundException e) {
                JOptionPane.showMessageDialog(this, "Barcode tidak ditemukan di gambar.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memproses gambar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getStatusKadaluarsa(java.util.Date expiryDate) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate expiryLocalDate = expiryDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            long days = java.time.temporal.ChronoUnit.DAYS.between(today, expiryLocalDate);
            return days < 0 ? "❌ Sudah Kedaluwarsa" : "✅ Belum Kedaluwarsa";
        } catch (Exception e) {
            return "⛔ Format Tanggal Salah";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Dummy kosong, ganti dengan data asli
            List<Product> dummyList = List.of();
            new CekExpiredForm(dummyList).setVisible(true);
        });
    }
}
