package com.hotel.model;

import java.sql.Timestamp;

public class Customer 
{
    private int customerId;
    private String name;
    private String phone;
    private String address;
    private String idProof;
    private Timestamp createdAt;

    public int getCustomerId() 
    { 
    	return customerId; 
    	}
    public void setCustomerId(int customerId) 
    { 
    	this.customerId = customerId; 
    }
    public String getName() 
    { 
    	return name; 
    }
    public void setName(String name) 
    { 
    	this.name = name; 
    }
    public String getPhone() 
    { 
    	return phone; 
    }
    public void setPhone(String phone) 
    { 
    	this.phone = phone; 
    }
    public String getAddress() 
    { 
    	return address; 
    	}
    public void setAddress(String address) 
    { 
    	this.address = address; 
    	}
    public String getIdProof() 
    { 
    	return idProof;
    }
    public void setIdProof(String idProof) 
    { 
    	this.idProof = idProof; 
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
