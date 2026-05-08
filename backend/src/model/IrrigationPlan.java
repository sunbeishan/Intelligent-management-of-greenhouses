package model;

import java.sql.Time;
import java.sql.Timestamp;

public class IrrigationPlan {
    private int id;
    private int farmlandId;
    private String farmlandName;
    private int deviceId;
    private String deviceName;
    private String planName;
    private Time startTime;
    private int duration;
    private double waterAmount;
    private String frequency;
    private String weekDays;
    private String status;
    private Timestamp createTime;
    private Timestamp updateTime;

    public IrrigationPlan() {}

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

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getWaterAmount() { return waterAmount; }
    public void setWaterAmount(double waterAmount) { this.waterAmount = waterAmount; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public String getWeekDays() { return weekDays; }
    public void setWeekDays(String weekDays) { this.weekDays = weekDays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Timestamp getUpdateTime() { return updateTime; }
    public void setUpdateTime(Timestamp updateTime) { this.updateTime = updateTime; }
}