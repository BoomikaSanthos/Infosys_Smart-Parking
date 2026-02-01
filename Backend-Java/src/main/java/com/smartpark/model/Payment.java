package com.smartpark.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    @DBRef
    private Booking bookingId;

    private Double amount;
    private Double parkingCharge;
    private Double penaltyAmount = 0.0;
    private String penaltyType;

    private Integer durationMinutes;
    private Integer slabsUsed;

    private String status = "pending";
    private String method;

    private String vehicleNumber;
    private String slotNumber;

    @DBRef
    private User user;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Booking getBookingId() { return bookingId; }
    public void setBookingId(Booking bookingId) { this.bookingId = bookingId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getParkingCharge() { return parkingCharge; }
    public void setParkingCharge(Double parkingCharge) { this.parkingCharge = parkingCharge; }

    public Double getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(Double penaltyAmount) { this.penaltyAmount = penaltyAmount; }

    public String getPenaltyType() { return penaltyType; }
    public void setPenaltyType(String penaltyType) { this.penaltyType = penaltyType; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getSlabsUsed() { return slabsUsed; }
    public void setSlabsUsed(Integer slabsUsed) { this.slabsUsed = slabsUsed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
