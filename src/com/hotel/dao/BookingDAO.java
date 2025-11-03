package com.hotel.dao;

import com.hotel.model.Booking;
import com.hotel.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class BookingDAO 
{

    public int createBooking(int customerId, int roomId, Timestamp checkIn, String status) 
    {
        String sql = "INSERT INTO bookings (customer_id, room_id, check_in_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            ps.setInt(1, customerId);
            ps.setInt(2, roomId);
            ps.setTimestamp(3, checkIn);
            ps.setString(4, status);
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet k = ps.getGeneratedKeys()) 
            {
                if (k.next()) return k.getInt(1);
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return -1;
    }

    public Booking findById(int bookingId) 
    {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setCustomerId(rs.getInt("customer_id"));
                    b.setRoomId(rs.getInt("room_id"));
                    b.setCheckInDate(rs.getTimestamp("check_in_date"));
                    b.setCheckOutDate(rs.getTimestamp("check_out_date"));
                    b.setTotalAmount(rs.getBigDecimal("total_amount"));
                    b.setStatus(rs.getString("status"));
                    b.setCreatedAt(rs.getTimestamp("created_at"));
                    return b;
                }
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return null;
    }

    public boolean checkIn(int bookingId, Connection conn) throws SQLException 
    {
        String updateBooking = "UPDATE bookings SET status='CHECKED_IN', check_in_date=? WHERE booking_id=?";
        try (PreparedStatement ps = conn.prepareStatement(updateBooking)) 
        {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, bookingId);
            
            return ps.executeUpdate() > 0;
        }
    }

    public boolean checkOut(int bookingId) 
    {
        String sel = "SELECT b.booking_id, b.room_id, b.check_in_date, r.price_per_night FROM bookings b JOIN rooms r ON b.room_id = r.room_id WHERE b.booking_id = ? AND b.status = 'CHECKED_IN'";
        String updBooking = "UPDATE bookings SET status='CHECKED_OUT', check_out_date=?, total_amount=? WHERE booking_id=?";
        String updRoom = "UPDATE rooms SET status='AVAILABLE' WHERE room_id=?";
        try (Connection conn = DBUtil.getConnection()) 
        {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sel))
            {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) 
                {
                    if (!rs.next()) 
                    {
                        conn.rollback();
                        return false;
                    }
                    int roomId = rs.getInt("room_id");
                    Timestamp checkIn = rs.getTimestamp("check_in_date");
                    BigDecimal price = rs.getBigDecimal("price_per_night");

                    long days = ChronoUnit.DAYS.between(checkIn.toLocalDateTime().toLocalDate(), LocalDateTime.now().toLocalDate());
                    if (days <= 0) days = 1; // minimum 1 day

                    BigDecimal total = price.multiply(BigDecimal.valueOf(days));

                    try (PreparedStatement upb = conn.prepareStatement(updBooking);
                         PreparedStatement upr = conn.prepareStatement(updRoom)) {
                        upb.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                        upb.setBigDecimal(2, total);
                        upb.setInt(3, bookingId);
                        upb.executeUpdate();

                        upr.setInt(1, roomId);
                        upr.executeUpdate();
                    }
                    conn.commit();
                    return true;
                }
            } 
            catch (Exception ex) 
            {
                conn.rollback();
                ex.printStackTrace();
            } 
            finally 
            {
                conn.setAutoCommit(true);
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return false;
    }

    public boolean allocateRoomAndMarkOccupied(int roomId) 
    {
        String sql = "UPDATE rooms SET status='OCCUPIED' WHERE room_id = ? AND status='AVAILABLE'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return false;
    }
}
