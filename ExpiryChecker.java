import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExpiryChecker extends JFrame {
    public ExpiryChecker() {
        setTitle("Cek Kedaluwarsa Produk");
        setSize(550, 350);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setEditable(false);

        // Contoh data produk dan tanggal expired
        ArrayList<ProdukKadaluarsa> daftarProduk = new ArrayList<>();
        daftarProduk.add(new ProdukKadaluarsa("Toner A", "2025-06-01"));
        daftarProduk.add(new ProdukKadaluarsa("Serum B", "2025-05-15"));
        daftarProduk.add(new ProdukKadaluarsa("Moisturizer C", "2025-05-08")); // Akan expired 1 hari lagi

        StringBuilder tampil = new StringBuilder();
        tampil.append("Daftar Produk:\n\n");

        LocalDate hariIni = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (ProdukKadaluarsa produk : daftarProduk) {
            LocalDate tglExp = LocalDate.parse(produk.tanggalExp, format);
            long selisihHari = java.time.temporal.ChronoUnit.DAYS.between(hariIni, tglExp);

            tampil.append("- ").append(produk.nama)
                  .append(" (Exp: ").append(tglExp).append(")\n");

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

    // Inner class Produk
    class ProdukKadaluarsa {
        String nama;
        String tanggalExp;

        ProdukKadaluarsa(String nama, String tanggalExp) {
            this.nama = nama;
            this.tanggalExp = tanggalExp;
        }
    }
}
