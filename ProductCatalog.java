import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProductCatalog extends JFrame {
    private DefaultTableModel tableModel;
    private JTable productTable;
    private ArrayList<Product> products;
    private int productCounter = 1;

    public ProductCatalog() {
        setTitle("Katalog Produk Qemmla Skin and Body Care");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        products = new ArrayList<>();
        String[] kolom = {"Kode", "Nama", "Harga", "Kategori", "Produksi", "Exp", "Status"};
        tableModel = new DefaultTableModel(kolom, 0);
        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.setRowHeight(28);

        JTableHeader header = productTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(60, 120, 180));
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < productTable.getColumnCount(); i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(productTable);

        // Input fields
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField productionDateField = new JTextField("yyyy-mm-dd");
        JTextField expiryDateField = new JTextField("yyyy-mm-dd");
        JButton addButton = new JButton("➕ Tambah Produk");

        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);
        nameField.setFont(inputFont);
        priceField.setFont(inputFont);
        categoryField.setFont(inputFont);
        productionDateField.setFont(inputFont);
        expiryDateField.setFont(inputFont);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(46, 139, 87));
        addButton.setForeground(Color.WHITE);

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String category = categoryField.getText().trim();
            String productionDate = productionDateField.getText().trim();
            String expiryDate = expiryDateField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || category.isEmpty()
                    || productionDate.isEmpty() || expiryDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                String kodeProduk = String.format("P%03d", productCounter++);
                Product product = new Product(kodeProduk, name, price, category, productionDate, expiryDate);
                products.add(product);

                String status = getStatusKadaluarsa(expiryDate);
                tableModel.addRow(new Object[]{
                        product.getCode(),
                        product.getName(),
                        String.format("Rp%.2f", product.getPrice()),
                        product.getCategory(),
                        product.getProductionDate(),
                        product.getExpiryDate(),
                        status
                });

                nameField.setText("");
                priceField.setText("");
                categoryField.setText("");
                productionDateField.setText("yyyy-mm-dd");
                expiryDateField.setText("yyyy-mm-dd");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka");
            }
        });

        // Panel input
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        inputPanel.setBackground(new Color(245, 250, 255));

        inputPanel.add(new JLabel("Nama Produk:", JLabel.RIGHT));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Harga Produk:", JLabel.RIGHT));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Kategori Produk:", JLabel.RIGHT));
        inputPanel.add(categoryField);
        inputPanel.add(new JLabel("Tanggal Produksi:", JLabel.RIGHT));
        inputPanel.add(productionDateField);
        inputPanel.add(new JLabel("Tanggal Kadaluarsa:", JLabel.RIGHT));
        inputPanel.add(expiryDateField);
        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        getContentPane().setBackground(new Color(230, 240, 255));
        setVisible(true);
    }

    // Menghitung status berdasarkan tanggal Exp
    private String getStatusKadaluarsa(String expiryDateStr) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long days = java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);

            if (days < 0) return "❌ Sudah Kedaluwarsa";
            else if (days <= 7) return "⚠ BUANG - Exp dalam " + days + " hari";
            else if (days <= 30) return "🔔 PROMO - Exp dalam " + days + " hari";
            else return "✅ Aman - Exp dalam " + days + " hari";
        } catch (Exception e) {
            return "⛔ Format Tanggal Salah";
        }
    }

    class Product {
        private String code;
        private String name;
        private double price;
        private String category;
        private String productionDate;
        private String expiryDate;

        public Product(String code, String name, double price, String category, String productionDate, String expiryDate) {
            this.code = code;
            this.name = name;
            this.price = price;
            this.category = category;
            this.productionDate = productionDate;
            this.expiryDate = expiryDate;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getCategory() { return category; }
        public String getProductionDate() { return productionDate; }
        public String getExpiryDate() { return expiryDate; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductCatalog::new);
    }
}
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProductCatalog extends JFrame {
    private DefaultTableModel tableModel;
    private JTable productTable;
    private ArrayList<Product> products;
    private int productCounter = 1;

    public ProductCatalog() {
        setTitle("Katalog Produk");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        products = new ArrayList<>();
        String[] kolom = {"Kode", "Nama", "Harga", "Kategori", "Produksi", "Exp", "Status"};
        tableModel = new DefaultTableModel(kolom, 0);
        productTable = new JTable(tableModel);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productTable.setRowHeight(28);

        JTableHeader header = productTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(60, 120, 180));
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < productTable.getColumnCount(); i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(productTable);

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField categoryField = new JTextField();

        JComboBox<Integer> prodYearBox = new JComboBox<>();
        JComboBox<Integer> prodMonthBox = new JComboBox<>();
        JComboBox<Integer> prodDayBox = new JComboBox<>();

        JComboBox<Integer> expYearBox = new JComboBox<>();
        JComboBox<Integer> expMonthBox = new JComboBox<>();
        JComboBox<Integer> expDayBox = new JComboBox<>();

        int currentYear = LocalDate.now().getYear();
        for (int y = currentYear - 5; y <= currentYear + 5; y++) {
            prodYearBox.addItem(y);
            expYearBox.addItem(y);
        }
        for (int m = 1; m <= 12; m++) {
            prodMonthBox.addItem(m);
            expMonthBox.addItem(m);
        }
        for (int d = 1; d <= 31; d++) {
            prodDayBox.addItem(d);
            expDayBox.addItem(d);
        }

        JButton addButton = new JButton("➕ Tambah Produk");

        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);
        nameField.setFont(inputFont);
        priceField.setFont(inputFont);
        categoryField.setFont(inputFont);
        prodYearBox.setFont(inputFont);
        prodMonthBox.setFont(inputFont);
        prodDayBox.setFont(inputFont);
        expYearBox.setFont(inputFont);
        expMonthBox.setFont(inputFont);
        expDayBox.setFont(inputFont);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(46, 139, 87));
        addButton.setForeground(Color.WHITE);

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            String category = categoryField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi");
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                String kodeProduk = String.format("P%03d", productCounter++);

                String productionDate = String.format("%04d-%02d-%02d",
                        prodYearBox.getSelectedItem(),
                        prodMonthBox.getSelectedItem(),
                        prodDayBox.getSelectedItem());

                String expiryDate = String.format("%04d-%02d-%02d",
                        expYearBox.getSelectedItem(),
                        expMonthBox.getSelectedItem(),
                        expDayBox.getSelectedItem());

                Product product = new Product(kodeProduk, name, price, category, productionDate, expiryDate);
                products.add(product);

                String status = getStatusKadaluarsa(expiryDate);
                tableModel.addRow(new Object[]{
                        product.getCode(),
                        product.getName(),
                        String.format("Rp%.2f", product.getPrice()),
                        product.getCategory(),
                        product.getProductionDate(),
                        product.getExpiryDate(),
                        status
                });

                nameField.setText("");
                priceField.setText("");
                categoryField.setText("");
                prodYearBox.setSelectedIndex(5);
                prodMonthBox.setSelectedIndex(0);
                prodDayBox.setSelectedIndex(0);
                expYearBox.setSelectedIndex(5);
                expMonthBox.setSelectedIndex(0);
                expDayBox.setSelectedIndex(0);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Harga harus berupa angka");
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 8));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        inputPanel.setBackground(new Color(245, 250, 255));

        inputPanel.add(new JLabel("Nama Produk:", JLabel.RIGHT));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Harga Produk:", JLabel.RIGHT));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Kategori Produk:", JLabel.RIGHT));
        inputPanel.add(categoryField);

        JPanel prodDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        prodDatePanel.setBackground(new Color(245, 250, 255));
        prodDatePanel.add(prodYearBox);
        prodDatePanel.add(new JLabel("-"));
        prodDatePanel.add(prodMonthBox);
        prodDatePanel.add(new JLabel("-"));
        prodDatePanel.add(prodDayBox);

        JPanel expDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        expDatePanel.setBackground(new Color(245, 250, 255));
        expDatePanel.add(expYearBox);
        expDatePanel.add(new JLabel("-"));
        expDatePanel.add(expMonthBox);
        expDatePanel.add(new JLabel("-"));
        expDatePanel.add(expDayBox);

        inputPanel.add(new JLabel("Tanggal Produksi:", JLabel.RIGHT));
        inputPanel.add(prodDatePanel);
        inputPanel.add(new JLabel("Tanggal Kadaluarsa:", JLabel.RIGHT));
        inputPanel.add(expDatePanel);

        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        getContentPane().setBackground(new Color(230, 240, 255));
        setVisible(true);
    }

    private String getStatusKadaluarsa(String expiryDateStr) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate expiryDate = LocalDate.parse(expiryDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            long days = java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);

            if (days < 0) return "❌ Sudah Kedaluwarsa";
            else if (days <= 7) return "⚠ BUANG - Exp dalam " + days + " hari";
            else if (days <= 30) return "🔔 PROMO - Exp dalam " + days + " hari";
            else return "✅ Aman - Exp dalam " + days + " hari";
        } catch (Exception e) {
            return "⛔ Format Tanggal Salah";
        }
    }

    class Product {
        private String code;
        private String name;
        private double price;
        private String category;
        private String productionDate;
        private String expiryDate;

        public Product(String code, String name, double price, String category, String productionDate, String expiryDate) {
            this.code = code;
            this.name = name;
            this.price = price;
            this.category = category;
            this.productionDate = productionDate;
            this.expiryDate = expiryDate;
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getCategory() { return category; }
        public String getProductionDate() { return productionDate; }
        public String getExpiryDate() { return expiryDate; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductCatalog::new);
    }
}
