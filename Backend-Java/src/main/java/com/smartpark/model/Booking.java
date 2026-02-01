package com.smartpark.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private Slot slot;

    private String vehicleNumber;
    private Date startTime;
    private Date endTime;
    private Date actualEntryTime;
    private Date actualExitTime;

    private Integer actualDurationMinutes = 0;

    private String paymentStatus = "pending";

    private Double amount = 0.0;
    private Double parkingCharge = 0.0;
    private Double penaltyAmount = 0.0;
    private String penaltyType;
    private Boolean penaltyPaid = false;

    private String paymentMethod;

    @DBRef
    private Payment paymentId;

    private Integer slabs = 0;

    private String status = "active";

    private Integer plannedSlabs;
    private Double plannedCost;
    private Date penaltyAppliedAt;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Slot getSlot() { return slot; }
    public void setSlot(Slot slot) { this.slot = slot; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Date getActualEntryTime() { return actualEntryTime; }
    public void setActualEntryTime(Date actualEntryTime) { this.actualEntryTime = actualEntryTime; }

    public Date getActualExitTime() { return actualExitTime; }
    public void setActualExitTime(Date actualExitTime) { this.actualExitTime = actualExitTime; }

    public Integer getActualDurationMinutes() { return actualDurationMinutes; }
    public void setActualDurationMinutes(Integer actualDurationMinutes) { this.actualDurationMinutes = actualDurationMinutes; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getParkingCharge() { return parkingCharge; }
    public void setParkingCharge(Double parkingCharge) { this.parkingCharge = parkingCharge; }

    public Double getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(Double penaltyAmount) { this.penaltyAmount = penaltyAmount; }

    public String getPenaltyType() { return penaltyType; }
    public void setPenaltyType(String penaltyType) { this.penaltyType = penaltyType; }

    public Boolean getPenaltyPaid() { return penaltyPaid; }
    public void setPenaltyPaid(Boolean penaltyPaid) { this.penaltyPaid = penaltyPaid; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Payment getPaymentId() { return paymentId; }
    public void setPaymentId(Payment paymentId) { this.paymentId = paymentId; }

    public Integer getSlabs() { return slabs; }
    public void setSlabs(Integer slabs) { this.slabs = slabs; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getPlannedSlabs() { return plannedSlabs; }
    public void setPlannedSlabs(Integer plannedSlabs) { this.plannedSlabs = plannedSlabs; }

    public Double getPlannedCost() { return plannedCost; }
    public void setPlannedCost(Double plannedCost) { this.plannedCost = plannedCost; }

    public Date getPenaltyAppliedAt() { return penaltyAppliedAt; }
    public void setPenaltyAppliedAt(Date penaltyAppliedAt) { this.penaltyAppliedAt = penaltyAppliedAt; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
