package com.smartpark.model;

// import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "slots")
public class Slot {
    @Id
    private String id;

    @Indexed(unique = true)
    private String slotNumber;

    private String state;
    private String location;
    private String slotStatus;
    private String vehicleType;
    private Boolean isAvailable = true;

    private Alerts alerts;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    public static class Alerts {
        private boolean systemError = false;
        private boolean maintenance = false;
        private boolean infrastructure = false;

        public boolean isSystemError() { return systemError; }
        public void setSystemError(boolean systemError) { this.systemError = systemError; }

        public boolean isMaintenance() { return maintenance; }
        public void setMaintenance(boolean maintenance) { this.maintenance = maintenance; }

        public boolean isInfrastructure() { return infrastructure; }
        public void setInfrastructure(boolean infrastructure) { this.infrastructure = infrastructure; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSlotStatus() { return slotStatus; }
    public void setSlotStatus(String slotStatus) { this.slotStatus = slotStatus; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public Alerts getAlerts() { return alerts; }
    public void setAlerts(Alerts alerts) { this.alerts = alerts; }
}
