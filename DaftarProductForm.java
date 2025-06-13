import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class DaftarProductForm extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private ProductDAO productDAO;

    public DaftarProductForm(ProductDAO productDAO) {
        this.productDAO = productDAO;
        setTitle("Daftar Produk");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        CustomPanel backgroundPanel = new CustomPanel("assets/logo_qeemla.png");
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Daftar Produk yang Tersedia");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(6, 94, 84));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] kolom = {"Foto", "Kode", "Nama", "Kategori", "Harga", "Stok", "Tgl Produksi", "Tgl Expired"};
        model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return ImageIcon.class;
                return Object.class;
            }
        };
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    String expiryDateStr = (String) getValueAt(row, 7);
                    if (expiryDateStr != null && !expiryDateStr.isEmpty() && !expiryDateStr.equals("N/A")) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date expiryDate = sdf.parse(expiryDateStr);
                            Date today = new Date();
                            long diffInMillies = expiryDate.getTime() - today.getTime();
                            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                            if (diffInDays < 0) {
                                c.setBackground(new Color(255, 150, 150, 180));
                            } else if (diffInDays <= 30) {
                                c.setBackground(new Color(255, 255, 150, 180));
                            } else {
                                c.setBackground(row % 2 == 0 ? new Color(255, 255, 255, 180) : new Color(240, 248, 255, 180));
                            }
                        } catch (Exception e) {
                            c.setBackground(row % 2 == 0 ? new Color(255, 255, 255, 180) : new Color(240, 248, 255, 180));
                        }
                    } else {
                        c.setBackground(row % 2 == 0 ? new Color(255, 255, 255, 180) : new Color(240, 248, 255, 180));
                    }
                } else {
                    c.setBackground(new Color(173, 216, 230, 200));
                }
                return c;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon) value);
                }
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                return label;
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(90);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(6, 94, 84));
        header.setForeground(Color.WHITE);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(80);
        table.setGridColor(new Color(180, 180, 180));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton editButton = new JButton("Edit");
        styleButton(editButton, new Color(255, 165, 0), Color.WHITE);
        editButton.addActionListener(e -> editSelectedProduct());

        JButton deleteButton = new JButton("Hapus");
        styleButton(deleteButton, new Color(220, 20, 60), Color.WHITE);
        deleteButton.addActionListener(e -> deleteSelectedProduct());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(panel, BorderLayout.CENTER);

        refreshTable();
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    public void refreshTable() {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                String productionDateStr = (product.getProductionDate() != null) ? sdf.format(product.getProductionDate()) : "N/A";
                String expiryDateStr = (product.getExpiryDate() != null) ? sdf.format(product.getExpiryDate()) : "N/A";

                ImageIcon imageIcon = null;
                if (product.getPhotoPath() != null && !product.getPhotoPath().isEmpty()) {
                    File photoFile = new File(product.getPhotoPath());
                    if (photoFile.exists()) {
                        Image img = new ImageIcon(photoFile.getAbsolutePath()).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                        imageIcon = new ImageIcon(img);
                    } else {
                        imageIcon = createPlaceholderImage(70, 70, "No Photo");
                    }
                } else {
                    imageIcon = createPlaceholderImage(70, 70, "No Photo");
                }

                model.addRow(new Object[]{
                    imageIcon,
                    product.getCode(),
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getStock(),
                    productionDateStr,
                    expiryDateStr
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat daftar produk: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private ImageIcon createPlaceholderImage(int width, int height, String text) {
        BufferedImage placeholder = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = placeholder.createGraphics();
        g.setColor(new Color(200, 200, 200, 100));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        FontMetrics fm = g.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = ((height - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);
        g.dispose();
        return new ImageIcon(placeholder);
    }

    private void editSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productCode = (String) model.getValueAt(selectedRow, 1);

        try {
            Product productToEdit = productDAO.getProductByCode(productCode);
            if (productToEdit != null) {
                InputProductForm inputForm = new InputProductForm(productDAO, productToEdit);
                inputForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        refreshTable();
                    }
                });
                inputForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Produk tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saat mengambil data produk: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteSelectedProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productCode = (String) model.getValueAt(selectedRow, 1);
        String productName = (String) model.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Anda yakin ingin menghapus produk '" + productName + "' (Kode: " + productCode + ")?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productDAO.deleteProduct(productCode);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Produk berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error saat menghapus produk: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    class CustomPanel extends JPanel {
        private Image backgroundImage;

        public CustomPanel(String imagePath) {
            try {
                URL imageUrl = getClass().getClassLoader().getResource(imagePath);
                if (imageUrl != null) {
                    backgroundImage = new ImageIcon(imageUrl).getImage();
                } else {
                    System.err.println("Background image not found (classpath): " + imagePath);
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
