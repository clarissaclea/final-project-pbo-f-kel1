import javax.swing.*;
import javax.swing.table.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class LogAuditForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public LogAuditForm() {
        setTitle("Log Aktivitas");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Gambar background
        CustomPanel backgroundPanel = new CustomPanel("assets/logo_qeemla.png");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Riwayat Log Aktivitas Pengguna");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(6, 94, 84));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Waktu", "Aksi", "Detail", "Email Pengguna"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(255, 255, 255, 180)
                            : new Color(240, 248, 255, 180));
                } else {
                    c.setBackground(new Color(173, 216, 230, 200));
                }
                return c;
            }
        };

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(6, 94, 84));
        header.setForeground(Color.WHITE);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.setGridColor(new Color(180, 180, 180));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        backgroundPanel.add(panel, BorderLayout.CENTER);
        loadAuditLog();
    }

    private void loadAuditLog() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT waktu_aksi, aksi, detail, email_pengguna FROM log_audit ORDER BY waktu_aksi DESC")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getTimestamp("waktu_aksi"),
                        rs.getString("aksi"),
                        rs.getString("detail"),
                        rs.getString("email_pengguna")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data log audit.");
        }
    }
}
