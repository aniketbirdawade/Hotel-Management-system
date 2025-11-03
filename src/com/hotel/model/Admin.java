package com.hotel.model;

import java.sql.Timestamp;

public class Admin 
{
    private int adminId;
    private String username;
    private String passwordHash;
    private Timestamp lockedUntil;

    public int getAdminId() 
    { 
    	return adminId; 
    }
    public void setAdminId(int adminId) 
    { 
    	this.adminId = adminId; 
    }
    public String getUsername() 
    { 
    	return username; 
    }
    public void setUsername(String username) 
    { 
    	this.username = username; 
    }
    public String getPasswordHash() 
    { 
    	return passwordHash; 
    }
    public void setPasswordHash(String passwordHash) 
    { 
    	this.passwordHash = passwordHash; 
    	}
    public Timestamp getLockedUntil() 
    { 
    	return lockedUntil; 
    }
    public void setLockedUntil(Timestamp lockedUntil) 
    { 
    	this.lockedUntil = lockedUntil; 
    }
}
