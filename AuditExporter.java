import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.sql.*;
import java.io.File;

public class AuditExporter {

    public static void exportToExcel(String filePath) {
        String[] kolom = {"Waktu Aksi", "Aksi", "Detail", "Email Pengguna"};

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT waktu_aksi, aksi, detail, email_pengguna FROM log_audit");
             Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Laporan Audit");

            Font fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            CellStyle styleHeader = workbook.createCellStyle();
            styleHeader.setFont(fontHeader);

            Row barisHeader = sheet.createRow(0);
            for (int i = 0; i < kolom.length; i++) {
                Cell cell = barisHeader.createCell(i);
                cell.setCellValue(kolom[i]);
                cell.setCellStyle(styleHeader);
            }

            int nomorBaris = 1;
            while (rs.next()) {
                Row row = sheet.createRow(nomorBaris++);
                row.createCell(0).setCellValue(rs.getTimestamp("waktu_aksi").toString());
                row.createCell(1).setCellValue(rs.getString("aksi"));
                row.createCell(2).setCellValue(rs.getString("detail"));
                row.createCell(3).setCellValue(rs.getString("email_pengguna"));
            }

            File file = new File(filePath);
            file.getParentFile().mkdirs(); // buat folder jika belum ada
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("Laporan berhasil diekspor ke " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
