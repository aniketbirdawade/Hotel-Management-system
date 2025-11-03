package com.hotel.dao;

import com.hotel.model.Room;
import com.hotel.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO 
{

    public boolean addRoom(Room r) 
    {
        String sql = "INSERT INTO rooms (room_number, type, price_per_night, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getType());
            ps.setBigDecimal(3, r.getPricePerNight());
            ps.setString(4, r.getStatus() == null ? "AVAILABLE" : r.getStatus());
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return false;
    }

    public Room findByRoomNumber(String roomNumber) 
    {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, roomNumber);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("room_id"));
                    r.setRoomNumber(rs.getString("room_number"));
                    r.setType(rs.getString("type"));
                    r.setPricePerNight(rs.getBigDecimal("price_per_night"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    return r;
                }
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return null;
    }

    public Room findById(int id) 
    {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("room_id"));
                    r.setRoomNumber(rs.getString("room_number"));
                    r.setType(rs.getString("type"));
                    r.setPricePerNight(rs.getBigDecimal("price_per_night"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    return r;
                }
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return null;
    }

    public boolean updateRoom(Room r, String changedFields) 
    {
        String sql = "UPDATE rooms SET type=?, price_per_night=?, status=? WHERE room_number=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, r.getType());
            ps.setBigDecimal(2, r.getPricePerNight());
            ps.setString(3, r.getStatus());
            ps.setString(4, r.getRoomNumber());
            int u = ps.executeUpdate();
            if (u > 0) { logHistory(r.getRoomId(), changedFields); return true; 
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return false;
    }

    private void logHistory(int roomId, String changedFields) 
    {
        String sql = "INSERT INTO room_history (room_id, changed_fields) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, roomId);
            ps.setString(2, changedFields);
            ps.executeUpdate();
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
    }

    public boolean deleteRoomIfNotBooked(String roomNumber) 
    {
        String checkSql = "SELECT r.room_id FROM rooms r " +
                "JOIN bookings b ON r.room_id = b.room_id " +
                "WHERE r.room_number = ? AND b.status IN ('BOOKED','CHECKED_IN')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) 
        {
            check.setString(1, roomNumber);
            try (ResultSet rs = check.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return false; // can't delete
                }
            }
            String delSql = "DELETE FROM rooms WHERE room_number = ?";
            try (PreparedStatement del = conn.prepareStatement(delSql)) 
            {
                del.setString(1, roomNumber);
                return del.executeUpdate() > 0;
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return false;
    }

    public List<Room> search(String qNumber, String qType, String qStatus) 
    {
        StringBuilder sb = new StringBuilder("SELECT * FROM rooms WHERE 1=1");
        if (qNumber != null && !qNumber.isBlank()) sb.append(" AND room_number = ?");
        if (qType != null && !qType.isBlank()) sb.append(" AND type = ?");
        if (qStatus != null && !qStatus.isBlank()) sb.append(" AND status = ?");
        List<Room> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (qNumber != null && !qNumber.isBlank()) ps.setString(idx++, qNumber);
            if (qType != null && !qType.isBlank()) ps.setString(idx++, qType);
            if (qStatus != null && !qStatus.isBlank()) ps.setString(idx++, qStatus);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("room_id"));
                    r.setRoomNumber(rs.getString("room_number"));
                    r.setType(rs.getString("type"));
                    r.setPricePerNight(rs.getBigDecimal("price_per_night"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(r);
                }
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return list;
    }

    public List<Room> findAll() 
    {
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        List<Room> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) 
        {
            while (rs.next()) 
            {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setType(rs.getString("type"));
                r.setPricePerNight(rs.getBigDecimal("price_per_night"));
                r.setStatus(rs.getString("status"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(r);
            }
        }
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return list;
    }
}
