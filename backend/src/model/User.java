package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;
    private String status;
    private String avatar;
    private Timestamp createTime;

    public User() {}

    public User(String username, String password, String name, String role, String status) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
}
