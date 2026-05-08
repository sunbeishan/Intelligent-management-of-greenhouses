package model;

import java.sql.Date;
import java.sql.Timestamp;

public class IrrigationDevice {
    private int id;
    private String name;
    private int farmlandId;
    private String farmlandName;
    private String deviceType;
    private String status;
    private double waterFlow;
    private double coverageArea;
    private Date installDate;
    private Timestamp createTime;
    private Timestamp updateTime;

    public IrrigationDevice() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getFarmlandId() { return farmlandId; }
    public void setFarmlandId(int farmlandId) { this.farmlandId = farmlandId; }

    public String getFarmlandName() { return farmlandName; }
    public void setFarmlandName(String farmlandName) { this.farmlandName = farmlandName; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getWaterFlow() { return waterFlow; }
    public void setWaterFlow(double waterFlow) { this.waterFlow = waterFlow; }

    public double getCoverageArea() { return coverageArea; }
    public void setCoverageArea(double coverageArea) { this.coverageArea = coverageArea; }

    public Date getInstallDate() { return installDate; }
    public void setInstallDate(Date installDate) { this.installDate = installDate; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
}