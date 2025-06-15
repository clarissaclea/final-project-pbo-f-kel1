// File: CekExpiredForm.java

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.io.File;
import java.sql.SQLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CekExpiredForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private ProductDAO productDAO;
    private ExpiryChecker expiryChecker;

    public CekExpiredForm(ProductDAO productDAO) {
        this.productDAO = productDAO;
        this.expiryChecker = new ExpiryChecker(productDAO);
        setTitle("⏰ Produk Kedaluwarsa");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Gambar background yang kamu upload
        CustomPanel backgroundPanel = new CustomPanel("assets/logo_qeemla.png");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Cek Expired Produk");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(6, 94, 84));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] kolom = {"Kode", "Nama", "Harga", "Stok", "Tanggal Expired", "Status"};
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
                    String status = (String) getValueAt(row, 5);
                    if (status != null) {
                        if (status.contains("Sudah Kedaluwarsa")) {
                            c.setBackground(new Color(255, 100, 100, 180));
                        } else if (status.contains("BUANG") || status.contains("PROMO")) {
                            c.setBackground(new Color(255, 255, 100, 180));
                        } else {
                            c.setBackground(row % 2 == 0 ? new Color(255, 255, 255, 180) : new Color(240, 248, 255, 180));
                        }
                    }
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
        populateExpiredProducts();
    }

    public void populateExpiredProducts() {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                Date expiryDate = product.getExpiryDate();
                if (expiryDate != null) {
                    String status = getStatusKadaluarsa(expiryDate);
                    if (status.contains("Kedaluwarsa") || status.contains("BUANG") || status.contains("PROMO")) {
                        model.addRow(new Object[]{
                            product.getCode(),
                            product.getName(),
                            product.getPrice(),
                            product.getStock(),
                            sdf.format(expiryDate),
                            status
                        });
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat produk dari database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String getStatusKadaluarsa(Date expiryDate) {
        try {
            Date today = new Date();
            long diffInMillies = expiryDate.getTime() - today.getTime();
            long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (days < 0) return "❌ Sudah Kedaluwarsa";
            else if (days <= 7) return "⚠ BUANG - Exp dalam " + days + " hari";
            else if (days <= 30) return "🔔 PROMO - Exp dalam " + days + " hari";
            else return "✅ Aman - Exp dalam " + days + " hari";
        } catch (Exception e) {
            e.printStackTrace();
            return "⛔ Error Cek Tanggal";
        }
    }

    class CustomPanel extends JPanel {
        private Image backgroundImage;

        public CustomPanel(String imagePath) {
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    backgroundImage = new ImageIcon(file.getAbsolutePath()).getImage();
                } else {
                    URL resource = getClass().getClassLoader().getResource(imagePath);
                    if (resource != null) {
                        backgroundImage = new ImageIcon(resource).getImage();
                    } else {
                        System.err.println("Background image not found: " + imagePath);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading background image: " + e.getMessage());
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
