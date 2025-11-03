package com.hotel.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Room 
{
    private int roomId;
    private String roomNumber;
    private String type;
    private BigDecimal pricePerNight;
    private String status;
    private Timestamp createdAt;

    
    public int getRoomId() 
    { 
    	return roomId; 
    }
    public void setRoomId(int roomId) 
    { 
    	this.roomId = roomId; 
    }
    public String getRoomNumber() 
    { 
    	return roomNumber; 
    }
    public void setRoomNumber(String roomNumber) 
    { 
    	this.roomNumber = roomNumber; 
    }
    public String getType() 
    { 
    	return type; 
    }
    public void setType(String type) 
    { 
    	this.type = type; 
    }
    public BigDecimal getPricePerNight() 
    { 
    	return pricePerNight; 
    }
    public void setPricePerNight(BigDecimal pricePerNight) 
    { 
    	this.pricePerNight = pricePerNight; 
    }
    public String getStatus() 
    { 
    	return status; 
    }
    public void setStatus(String status) 
    { 
    	this.status = status; 
    }
    public Timestamp getCreatedAt() 
    { 
    	return createdAt; 
    }
    public void setCreatedAt(Timestamp createdAt) 
    { 
    	this.createdAt = createdAt; 
    }
}
