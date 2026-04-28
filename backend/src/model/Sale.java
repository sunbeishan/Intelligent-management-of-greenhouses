package model;

public class Sale {
    private String id;
    private String productName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String customer;
    private String saleDate;
    
    public Sale() {}
    
    public Sale(String id, String productName, int quantity, double unitPrice, 
                double totalPrice, String customer, String saleDate) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.customer = customer;
        this.saleDate = saleDate;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }
    
    public String getSaleDate() { return saleDate; }
    public void setSaleDate(String saleDate) { this.saleDate = saleDate; }
}
