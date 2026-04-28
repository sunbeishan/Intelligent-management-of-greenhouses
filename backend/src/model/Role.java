package model;

import java.sql.Timestamp;

public class Role {
    private int id;
    private String name;
    private String description;
    private Timestamp createTime;

    public Role() {}

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}
