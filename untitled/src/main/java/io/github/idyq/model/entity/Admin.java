package io.github.idyq.model.entity;

import io.github.idyq.util.PasswordUtil;

import java.time.LocalDateTime;

public class Admin {
    private Integer adminId;
    private String username;
    private String passwordHash;
    private String salt;
    private String algorithm;
    private Integer iterations;
    private LocalDateTime createdAt;

    // 全参构造器（可选）
    public Admin(Integer adminId, String username, String passwordHash,
                 String salt, String algorithm, Integer iterations,
                 LocalDateTime createdAt) {
        this.adminId = adminId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.algorithm = algorithm;
        this.iterations = iterations;
        this.createdAt = createdAt;
    }

    // 无参构造器（必须）
    public Admin() {}

    // Getters & Setters
    public Integer getAdminId() { return adminId; }
    public void setAdminId(Integer adminId) { this.adminId = adminId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public Integer getIterations() { return iterations; }
    public void setIterations(Integer iterations) { this.iterations = iterations; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // 辅助方法（可选）
    public byte[] getSaltBytes() {
        return PasswordUtil.hexToBytes(this.salt);
    }

    public byte[] getPasswordHashBytes() {
        return PasswordUtil.hexToBytes(this.passwordHash);
    }
}