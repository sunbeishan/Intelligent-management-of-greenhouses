package model;

import java.sql.Timestamp;

public class MonitorPoint {
    private int id;
    private String name;
    private String location;
    private int farmlandId;
    private String status;
    private double temperature;
    private int humidity;
    private int light;
    private int co2;
    private Timestamp lastUpdate;
    private Timestamp createTime;

    public MonitorPoint() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getFarmlandId() { return farmlandId; }
    public void setFarmlandId(int farmlandId) { this.farmlandId = farmlandId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public int getLight() { return light; }
    public void setLight(int light) { this.light = light; }

    public int getCo2() { return co2; }
    public void setCo2(int co2) { this.co2 = co2; }

    public Timestamp getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}