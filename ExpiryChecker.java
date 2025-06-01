import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExpiryChecker extends JFrame {
    public ExpiryChecker(List<Product> productList) {
        setTitle("Cek Kedaluwarsa Produk");
        setSize(550, 350);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        StringBuilder tampil = new StringBuilder();
        tampil.append("Daftar Produk:\n\n");

        LocalDate hariIni = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Product p : productList) {
            LocalDate expDate = p.getExpiryDate().toInstant()
                      .atZone(java.time.ZoneId.systemDefault())
                      .toLocalDate();

            long selisihHari = java.time.temporal.ChronoUnit.DAYS.between(hariIni, expDate);

            tampil.append("- ").append(p.getName())
                  .append(" (Exp: ").append(expDate.format(formatter)).append(")\n");


            if (selisihHari >= 0 && selisihHari <= 7) {
                tampil.append("  ⚠ *BUANG segera* - kedaluwarsa dalam ")
                      .append(selisihHari).append(" hari.\n");
            } else if (selisihHari > 7 && selisihHari <= 30) {
                tampil.append("  🔔 *PROMOkan* - segera jual, expired dalam ")
                      .append(selisihHari).append(" hari.\n");
            }

            tampil.append("\n");
        }

        area.setText(tampil.toString());
        add(new JScrollPane(area));
        setVisible(true);
    }
    public static String generateReminder(List<Product> productList) {
    StringBuilder output = new StringBuilder();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate today = LocalDate.now();

    boolean adaReminder = false;

    for (Product p : productList) {
        LocalDate expDate = p.getExpiryDate().toInstant()
                              .atZone(java.time.ZoneId.systemDefault())
                              .toLocalDate();
        long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(today, expDate);

        if (daysLeft <= 30 && daysLeft >= 0) {
            adaReminder = true;
            output.append("- ").append(p.getName())
                  .append(" (Exp: ").append(expDate.format(formatter)).append(")\n");


            if (daysLeft <= 7) {
                output.append("  ⚠ *BUANG segera* - kedaluwarsa dalam ").append(daysLeft).append(" hari.\n\n");
            } else {
                output.append("  🔔 *PROMOSIKAN* - expired dalam ").append(daysLeft).append(" hari.\n\n");
            }
        }
    }

    return adaReminder ? output.toString() : "Tidak ada produk yang mendekati expired.";
}

}

