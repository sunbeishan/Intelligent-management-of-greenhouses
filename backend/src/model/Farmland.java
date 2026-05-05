package model;

import java.sql.Timestamp;

public class Farmland {
    private int id;
    private String name;
    private String area;
    private double acreage;
    private String soilType;
    private String crop;
    private String status;
    private Timestamp createTime;
    private Timestamp updateTime;

    public Farmland() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public double getAcreage() { return acreage; }
    public void setAcreage(double acreage) { this.acreage = acreage; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
}