// File: ExpiryChecker.java

import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ExpiryChecker {

    private ProductDAO productDAO;

    public ExpiryChecker(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public String generateReminder() {
        StringBuilder output = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        boolean adaReminder = false;

        List<Product> productList;
        try {
            productList = productDAO.getAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error saat memuat data produk untuk pengingat: " + e.getMessage();
        }

        if (productList.isEmpty()) {
            return "Tidak ada produk dalam daftar.";
        }

        output.append("--- Pengingat Produk Kedaluwarsa ---\n\n");

        for (Product p : productList) {
            java.util.Date expiryDate = p.getExpiryDate();
            if (expiryDate == null) {
                continue;
            }

            // Konversi java.util.Date ke LocalDate
            LocalDate expDate = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long daysLeft = ChronoUnit.DAYS.between(today, expDate);

            if (daysLeft < 0) {
                output.append("❌ ").append(p.getName())
                      .append(" (Kode: ").append(p.getCode())
                      .append(") -> Sudah KEDALUWARSA pada ").append(expDate.format(formatter))
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
            return "Tidak ada produk yang memerlukan perhatian khusus saat ini.";
        }
        return output.toString();
    }
}
