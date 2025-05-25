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

        add(scanButton, BorderLayout.SOUTH);
    }

    private void openWebcamScanner() {
        Webcam webcam = Webcam.getDefault();

        if (webcam == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada webcam terdeteksi.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        webcam.setViewSize(new Dimension(640, 480));
        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);

        JFrame window = new JFrame("Scan Barcode");
        window.add(panel);
        window.setResizable(true);
        window.pack();
        window.setVisible(true);

        new Thread(() -> {
            while (true) {
                BufferedImage image = webcam.getImage();
                if (image == null) continue;

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    Result result = new MultiFormatReader().decode(bitmap);
                    if (result != null) {
                        String data = result.getText(); // format: NamaProduk|2025-06-01
                        String[] parts = data.split("\\|");

                        if (parts.length == 2) {
                            LocalDate tanggal = LocalDate.parse(parts[1], DateTimeFormatter.ISO_DATE);
                            String pesan = tanggal.isBefore(LocalDate.now())
                                    ? "Produk " + parts[0] + " sudah kedaluwarsa!"
                                    : "Produk " + parts[0] + " masih aman.";
                            JOptionPane.showMessageDialog(null, pesan);
                        } else {
                            JOptionPane.showMessageDialog(null, "Format barcode tidak valid.");
                        }

                        webcam.close();
                        window.dispose();
                        break;
                    }
                } catch (Exception ignored) {
                    // Jika gagal membaca barcode, lanjutkan loop
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
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
          
            List<Product> dummyList = List.of();
            new CekExpiredForm(dummyList).setVisible(true);
        });
    }
}
