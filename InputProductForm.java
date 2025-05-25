import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Date;
import java.util.List;

public class InputProductForm extends JFrame {
    private JTextField tfBarcode, tfNama, tfHarga, tfKategori;
    private JSpinner spinnerProduksi, spinnerExpired;
    private List<Product> productList;
    private JLabel photoLabel;
    private File selectedPhoto;

    public InputProductForm(List<Product> productList) {
        this.productList = productList;

        setTitle("Form Input Produk");
        setSize(500, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel background = new BackgroundPanel("assets/logo_qeemla.png");
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
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        tfBarcode = new JTextField(20);
        tfNama = new JTextField(20);
        tfHarga = new JTextField(20);
        tfKategori = new JTextField(20);

        spinnerProduksi = new JSpinner(new SpinnerDateModel());
        spinnerProduksi.setEditor(new JSpinner.DateEditor(spinnerProduksi, "yyyy-MM-dd"));

        spinnerExpired = new JSpinner(new SpinnerDateModel());
        spinnerExpired.setEditor(new JSpinner.DateEditor(spinnerExpired, "yyyy-MM-dd"));

        String[] labels = {"Kode Barcode", "Nama Produk", "Harga", "Kategori", "Tgl Produksi", "Tgl Expired"};
        Component[] inputs = {tfBarcode, tfNama, tfHarga, tfKategori, spinnerProduksi, spinnerExpired};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(Color.BLACK);
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            formPanel.add(inputs[i], gbc);
        }

        gbc.gridx = 0;
        gbc.gridy = labels.length;
        JLabel photoText = new JLabel("Foto Produk");
        photoText.setForeground(Color.BLACK);
        formPanel.add(photoText, gbc);

        gbc.gridx = 1;
        JButton btnPilihFoto = new JButton("Pilih Gambar");
        btnPilihFoto.addActionListener(e -> pilihFoto());
        formPanel.add(btnPilihFoto, gbc);

        gbc.gridy++;
        photoLabel = new JLabel();
        photoLabel.setPreferredSize(new Dimension(120, 120));
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        photoLabel.setHorizontalAlignment(JLabel.CENTER);
        photoLabel.setVerticalAlignment(JLabel.CENTER);
        formPanel.add(photoLabel, gbc);

        mainPanel.add(formPanel);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBackground(new Color(6, 94, 84));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSimpan.setFocusPainted(false);
        btnSimpan.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSimpan.setMaximumSize(new Dimension(150, 40));
        btnSimpan.addActionListener(e -> simpanProduct());

        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(btnSimpan);

        background.add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void pilihFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Foto Produk");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedPhoto = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(selectedPhoto.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            photoLabel.setIcon(new ImageIcon(img));
        }
    }

    private void simpanProduct() {
        try {
            String kode = tfBarcode.getText().trim();
            String nama = tfNama.getText().trim();
            double harga = Double.parseDouble(tfHarga.getText().trim());
            String kategori = tfKategori.getText().trim();
            Date tglProduksi = (Date) spinnerProduksi.getValue();
            Date tglExpired = (Date) spinnerExpired.getValue();
            String photoPath = selectedPhoto != null ? selectedPhoto.getAbsolutePath() : null;

            Product produkBaru = new Product(kode, nama, harga, kategori, tglProduksi, tglExpired, photoPath);
            productList.add(produkBaru);

            JOptionPane.showMessageDialog(this, "Produk berhasil ditambahkan!\nFoto: " +
                    (selectedPhoto != null ? selectedPhoto.getName() : "Tidak ada"));
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Input tidak valid: " + ex.getMessage());
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
