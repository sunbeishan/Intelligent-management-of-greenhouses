package model;

import java.sql.Timestamp;

public class IrrigationRecord {
    private int id;
    private int farmlandId;
    private String farmlandName;
    private int deviceId;
    private String deviceName;
    private int planId;
    private String planName;
    private Timestamp startTime;
    private Timestamp endTime;
    private int duration;
    private double waterAmount;
    private String type;
    private String status;
    private String operator;
    private String remark;
    private Timestamp createTime;

    public IrrigationRecord() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFarmlandId() { return farmlandId; }
    public void setFarmlandId(int farmlandId) { this.farmlandId = farmlandId; }

    public String getFarmlandName() { return farmlandName; }
    public void setFarmlandName(String farmlandName) { this.farmlandName = farmlandName; }

    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public int getPlanId() { return planId; }
    public void setPlanId(int planId) { this.planId = planId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getWaterAmount() { return waterAmount; }
    public void setWaterAmount(double waterAmount) { this.waterAmount = waterAmount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}