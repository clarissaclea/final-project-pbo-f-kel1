import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.List;
import java.io.File;

public class DaftarProductForm extends JFrame {
    public DaftarProductForm(List<Product> productList) {
        setTitle("📦 Daftar Produk");
        setSize(950, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel background = new JLabel(new ImageIcon("assets/logo_qeemla.png"));
        setContentPane(background);
        background.setLayout(new BorderLayout());

        String[] kolom = {"Foto", "Kode", "Nama", "Harga", "Kategori", "Tgl Produksi", "Tgl Expired"};
        Object[][] data = new Object[productList.size()][7];

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < productList.size(); i++) {
            Product p = productList.get(i);

            ImageIcon imageIcon = null;
            if (p.getPhotoPath() != null && new File(p.getPhotoPath()).exists()) {
                Image img = new ImageIcon(p.getPhotoPath()).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                imageIcon = new ImageIcon(img);
            } else {
                imageIcon = new ImageIcon(new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB));
            }

            data[i][0] = imageIcon; 
            data[i][1] = p.getCode();
            data[i][2] = p.getName();
            data[i][3] = String.format("Rp%,.2f", p.getPrice());
            data[i][4] = p.getCategory();
            data[i][5] = sdf.format(p.getProductionDate());
            data[i][6] = sdf.format(p.getExpiryDate());
        }

        DefaultTableModel model = new DefaultTableModel(data, kolom) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? ImageIcon.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model) {
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

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(6, 94, 84));
        header.setForeground(Color.WHITE);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(90); 
        table.setGridColor(new Color(180, 180, 180));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Daftar Semua Produk Qeemla Skin & Body Care");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(6, 94, 84));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }
}
