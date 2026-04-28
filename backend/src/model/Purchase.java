package model;

public class Purchase {
    private String id;
    private String materialName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String supplier;
    private String purchaseDate;
    
    public Purchase() {}
    
    public Purchase(String id, String materialName, int quantity, double unitPrice, 
                    double totalPrice, String supplier, String purchaseDate) {
        this.id = id;
        this.materialName = materialName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.supplier = supplier;
        this.purchaseDate = purchaseDate;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    
    public String getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(String purchaseDate) { this.purchaseDate = purchaseDate; }
}
