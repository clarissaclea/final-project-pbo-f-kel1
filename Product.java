// File: Product.java

import java.util.Date;

public class Product {
    private String code;
    private String name;
    private String category;    // Menggantikan 'description'
    private double price;
    private int stock;
    private Date productionDate; // Tanggal Produksi
    private Date expiryDate;
    private String photoPath;    // Jalur Foto Produk

    // Konstruktor lengkap dengan semua kolom
    public Product(String code, String name, String category, double price, int stock, Date productionDate, Date expiryDate, String photoPath) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        this.photoPath = photoPath;
    }

    // Konstruktor default (penting untuk ProductDAO saat membaca data)
    public Product() {
    }

    // --- Getters ---
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public Date getProductionDate() { return productionDate; }
    public Date getExpiryDate() { return expiryDate; }
    public String getPhotoPath() { return photoPath; }

    // --- Setters ---
    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setProductionDate(Date productionDate) { this.productionDate = productionDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    @Override
    public String toString() {
        return "Product{" +
               "code='" + code + '\'' +
               ", name='" + name + '\'' +
               ", category='" + category + '\'' +
               ", price=" + price +
               ", stock=" + stock +
               ", productionDate=" + productionDate +
               ", expiryDate=" + expiryDate +
               ", photoPath='" + photoPath + '\'' +
               '}';
    }
}
