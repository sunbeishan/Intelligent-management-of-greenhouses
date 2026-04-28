package model;

public class Material {
    private int id;
    private String name;
    private String type;
    private String spec;
    private int stock;
    private String unit;
    private String supplier;
    
    public Material() {}
    
    public Material(int id, String name, String type, String spec, int stock, String unit, String supplier) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.spec = spec;
        this.stock = stock;
        this.unit = unit;
        this.supplier = supplier;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
}
