package model;

public class Inventory {
    private int id;
    private String name;
    private String type;
    private String category;
    private String spec;
    private int stock;
    private String unit;
    private String lastUpdate;
    
    public Inventory() {}
    
    public Inventory(int id, String name, String type, String category, String spec, 
                     int stock, String unit, String lastUpdate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.category = category;
        this.spec = spec;
        this.stock = stock;
        this.unit = unit;
        this.lastUpdate = lastUpdate;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
}
