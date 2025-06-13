// File: ExpiryChecker.java

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Date;

public class ExpiryChecker {

    private ProductDAO productDAO;

    public ExpiryChecker(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void showReminder() {
        StringBuilder output = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        boolean adaReminder = false;

        List<Product> productList;
        try {
            productList = productDAO.getAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal memuat data produk: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (productList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada produk dalam daftar.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        output.append("--- Pengingat Produk Kedaluwarsa ---\n\n");

        for (Product p : productList) {
            Date expiryDate = p.getExpiryDate();
            if (expiryDate == null) {
                continue;
            }

            LocalDate expDate;
            if (expiryDate instanceof java.sql.Date) {
                expDate = ((java.sql.Date) expiryDate).toLocalDate();
            } else {
                expDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }

            long daysLeft = ChronoUnit.DAYS.between(today, expDate);

            if (daysLeft < 0) {
                output.append("❌ ").append(p.getName())
                      .append(" (Kode: ").append(p.getCode())
                      .append(") -> Sudah KEDALUWARSA pada ")
                      .append(expDate.format(formatter))
                      .append(" (").append(Math.abs(daysLeft)).append(" hari lalu)\n");
                adaReminder = true;
            } else if (daysLeft <= 7) {
                output.append("⚠ ").append(p.getName())
                      .append(" (Kode: ").append(p.getCode())
                      .append(") -> SEGERA BUANG, kedaluwarsa dalam ")
                      .append(daysLeft).append(" hari (").append(expDate.format(formatter)).append(")\n");
                adaReminder = true;
            } else if (daysLeft <= 30) {
                output.append("🔔 ").append(p.getName())
                      .append(" (Kode: ").append(p.getCode())
                      .append(") -> PROMOkan, kedaluwarsa dalam ")
                      .append(daysLeft).append(" hari (").append(expDate.format(formatter)).append(")\n");
                adaReminder = true;
            }
        }

        if (!adaReminder) {
            JOptionPane.showMessageDialog(null, "Tidak ada produk yang memerlukan perhatian khusus saat ini.",
                    "Pengingat Produk Kedaluwarsa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, output.toString(),
                    "Pengingat Produk Kedaluwarsa", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
