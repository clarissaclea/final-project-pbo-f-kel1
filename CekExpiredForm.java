import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CekExpiredForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public CekExpiredForm(List<Product> allProducts) {
        setTitle("⏰Produk Expired");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabel produk exp
        model = new DefaultTableModel(new String[]{"Kode", "Nama", "Exp", "Status"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);

        // Filter hanya produk yang sudah exp
        for (Product p : allProducts) {
            String status = getStatusKadaluarsa(p.getExpiryDate());
            if (status.contains("Kedaluwarsa")) {
                model.addRow(new Object[]{p.getCode(), p.getName(), p.getExpiryDate(), status});
            }
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private String getStatusKadaluarsa(java.util.Date expiryDate) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate expiryLocalDate = expiryDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            long days = java.time.temporal.ChronoUnit.DAYS.between(today, expiryLocalDate);
            return days < 0 ? "❌ Sudah Expired" : "✅ Belum Expired";
        } catch (Exception e) {
            return "⛔ Format Tanggal Salah";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // TODO: Ganti dengan daftar produk nyata dari database atau file
            List<Product> dummyList = List.of();
            new CekExpiredForm(dummyList).setVisible(true);
        });
    }
}
