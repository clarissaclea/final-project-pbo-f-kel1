import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CekExpiredForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public CekExpiredForm(List<Product> allProducts) {
        setTitle("⏰ Produk Kedaluwarsa");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Background image seperti di DaftarProductForm
        JLabel background = new JLabel(new ImageIcon("assets/logo_qeemla.png"));
        setContentPane(background);
        background.setLayout(new BorderLayout());

        String[] kolom = {"Kode", "Nama", "Tanggal Expired", "Status"};
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 255, 180) : new Color(240, 248, 255, 180));
                } else {
                    c.setBackground(new Color(173, 216, 230, 200));
                }
                return c;
            }
        };

        // Header table style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(6, 94, 84));
        header.setForeground(Color.WHITE);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setGridColor(new Color(180, 180, 180));

        // Tambahkan data produk kadaluwarsa
        for (Product p : allProducts) {
            String status = getStatusKadaluarsa(p.getExpiryDate());
            if (status.contains("Kedaluwarsa")) {
                model.addRow(new Object[]{
                    p.getCode(),
                    p.getName(),
                    p.getExpiryDate(),
                    status
                });
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Daftar Produk yang Sudah Kedaluwarsa");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(6, 94, 84));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        background.add(panel, BorderLayout.CENTER);
        setVisible(true);
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
}
