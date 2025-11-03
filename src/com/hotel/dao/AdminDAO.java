package com.hotel.dao;

import com.hotel.model.Admin;
import com.hotel.util.DBUtil;

import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class AdminDAO 
{

    public Admin findByUsername(String username) 
    {
        String sql = "SELECT * FROM admin WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) 
                {
                    Admin a = new Admin();
                    a.setAdminId(rs.getInt("admin_id"));
                    a.setUsername(rs.getString("username"));
                    a.setPasswordHash(rs.getString("password_hash"));
                    a.setLockedUntil(rs.getTimestamp("locked_until"));
                    return a;
                }
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        	}
        return null;
    }

    public void logAttempt(String username, boolean success) 
    {
        String sql = "INSERT INTO login_attempts (username, success) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, username);
            ps.setBoolean(2, success);
            ps.executeUpdate();
        } 
        catch (SQLException e) 
        {
        	e.printStackTrace(); 
        }
    }

    public void lockAccount(String username, int minutes) 
    {
        String sql = "UPDATE admin SET locked_until = ? WHERE username = ?";
        Timestamp until = Timestamp.from(Instant.now().plus(minutes, ChronoUnit.MINUTES));
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setTimestamp(1, until);
            ps.setString(2, username);
            ps.executeUpdate();
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        	
        }
    }

    public void unlockAccount(String username) 
    {
        String sql = "UPDATE admin SET locked_until = NULL WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, username);
            ps.executeUpdate();
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
    }
}
