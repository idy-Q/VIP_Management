package io.github.idyq.model.entity;

import java.time.LocalDateTime;

public class ServiceItem {
    private int serviceItemID;
    private String name;
    private String description;
    private double price;
    private int duration;
    private LocalDateTime createdAt;

    public ServiceItem() {}

    public int getServiceItemID() {
        return serviceItemID;
    }

    public void setServiceItemID(int serviceItemID) {
        this.serviceItemID = serviceItemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
