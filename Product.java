import java.util.Date;

public class Product {
    private String code;
    private String name;
    private double price;
    private String category;
    private Date productionDate;
    private Date expiryDate;
    private String photoPath;

    public Product(String code, String name, double price, String category,
                   Date productionDate, Date expiryDate, String photoPath) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.category = category;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        this.photoPath = photoPath;
    }

    public String getCode(){
        return code;
    }
    public String getName(){
        return name;
    }
    public double getPrice(){
        return price;
    }
    public String getCategory(){
        return category;
    }
    public Date getProductionDate(){
        return productionDate;
    }
    public Date getExpiryDate(){
        return expiryDate;
    }
    public String getPhotoPath(){
        return photoPath;
    }
}
