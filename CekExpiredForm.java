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


