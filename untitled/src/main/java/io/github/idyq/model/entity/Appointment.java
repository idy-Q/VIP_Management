package io.github.idyq.model.entity;

import java.time.LocalDateTime;

public class Appointment {
    private int appointmentID;
    private Integer memberID; // 可为空
    private int serviceItemID;
    private Integer staffID; // 可为空
    private LocalDateTime appointmentDate;
    private String status;
    private LocalDateTime createdAt;

    public Appointment() {}

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Integer getMemberID() {
        return memberID;
    }

    public void setMemberID(Integer memberID) {
        this.memberID = memberID;
    }

    public int getServiceItemID() {
        return serviceItemID;
    }

    public void setServiceItemID(int serviceItemID) {
        this.serviceItemID = serviceItemID;
    }

    public Integer getStaffID() {
        return staffID;
    }

    public void setStaffID(Integer staffID) {
        this.staffID = staffID;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
