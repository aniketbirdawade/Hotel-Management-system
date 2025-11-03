package com.hotel.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Booking 
{
    private int bookingId;
    private int customerId;
    private int roomId;
    private Timestamp checkInDate;
    private Timestamp checkOutDate;
    private BigDecimal totalAmount;
    private String status;
    private Timestamp createdAt;

    
    public int getBookingId() 
    { 
    	return bookingId; 
    }
    public void setBookingId(int bookingId) 
    { 
    	this.bookingId = bookingId; 
    }
    public int getCustomerId() 
    { 
    	return customerId; 
    }
    public void setCustomerId(int customerId) 
    { 
    	this.customerId = customerId; 
    }
    public int getRoomId() 
    { 
    	return roomId; 
    }
    public void setRoomId(int roomId) 
    { 
    	this.roomId = roomId; 
    }
    public Timestamp getCheckInDate() 
    { 
    	return checkInDate; 
    }
    public void setCheckInDate(Timestamp checkInDate) 
    { 
    	this.checkInDate = checkInDate; 
    }
    public Timestamp getCheckOutDate() 
    { 
    	return checkOutDate; 
    }
    public void setCheckOutDate(Timestamp checkOutDate) 
    { 
    	this.checkOutDate = checkOutDate; 
    }
    public BigDecimal getTotalAmount() 
    { 
    	return totalAmount; 
    }
    public void setTotalAmount(BigDecimal totalAmount) 
    { 
    	this.totalAmount = totalAmount; 
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
