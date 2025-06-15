// File: ProductDAO.java

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types; // Untuk menangani nilai NULL pada tanggal
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Menggunakan java.util.Date

public class ProductDAO {

    public void addProduct(Product product) throws SQLException {
        // Query INSERT disesuaikan dengan kolom 'kategori', 'production_date', 'expiry_date', 'photo_path'
        String sql = "INSERT INTO products (kode, nama, kategori, harga, stok, production_date, expiry_date, photo_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getCode());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getCategory()); // Menggunakan getCategory()
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getStock());

            // Set tanggal produksi
            if (product.getProductionDate() != null) {
                stmt.setDate(6, new java.sql.Date(product.getProductionDate().getTime()));
            } else {
                stmt.setNull(6, Types.DATE); // Set NULL jika tanggal kosong
            }

            // Set tanggal expired
            if (product.getExpiryDate() != null) {
                stmt.setDate(7, new java.sql.Date(product.getExpiryDate().getTime()));
            } else {
                stmt.setNull(7, Types.DATE); // Set NULL jika tanggal kosong
            }

            stmt.setString(8, product.getPhotoPath());

            stmt.executeUpdate();
        }
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        // Query SELECT disesuaikan dengan semua kolom
        String sql = "SELECT kode, nama, kategori, harga, stok, production_date, expiry_date, photo_path FROM products";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Konversi java.sql.Date ke java.util.Date
                Date productionDate = rs.getDate("production_date");
                Date expiryDate = rs.getDate("expiry_date");
                String photoPath = rs.getString("photo_path");

                Product product = new Product(
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getString("kategori"), // Menggunakan "kategori"
                        rs.getDouble("harga"),
                        rs.getInt("stok"),
                        productionDate,
                        expiryDate,
                        photoPath
                );
                products.add(product);
            }
        }
        return products;
    }

    public Product getProductByCode(String code) throws SQLException {
        String sql = "SELECT kode, nama, kategori, harga, stok, production_date, expiry_date, photo_path FROM products WHERE kode = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date productionDate = rs.getDate("production_date");
                    Date expiryDate = rs.getDate("expiry_date");
                    String photoPath = rs.getString("photo_path");

                    return new Product(
                            rs.getString("kode"),
                            rs.getString("nama"),
                            rs.getString("kategori"),
                            rs.getDouble("harga"),
                            rs.getInt("stok"),
                            productionDate,
                            expiryDate,
                            photoPath
                    );
                }
            }
        }
        return null;
    }

    public void updateProduct(Product product) throws SQLException {
        // Query UPDATE disesuaikan dengan kolom 'kategori', 'production_date', 'expiry_date', 'photo_path'
        String sql = "UPDATE products SET nama = ?, kategori = ?, harga = ?, stok = ?, production_date = ?, expiry_date = ?, photo_path = ? WHERE kode = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory()); // Menggunakan getCategory()
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());

            if (product.getProductionDate() != null) {
                stmt.setDate(5, new java.sql.Date(product.getProductionDate().getTime()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            if (product.getExpiryDate() != null) {
                stmt.setDate(6, new java.sql.Date(product.getExpiryDate().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, product.getPhotoPath());

            stmt.setString(8, product.getCode()); // Kode sebagai kondisi WHERE
            stmt.executeUpdate();
        }
    }

    public void deleteProduct(String code) throws SQLException {
        String sql = "DELETE FROM products WHERE kode = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.executeUpdate();
        }
    }
}