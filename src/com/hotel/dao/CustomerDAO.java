package com.hotel.dao;

import com.hotel.model.Customer;
import com.hotel.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO 
{

    public int addCustomer(Customer c) 
    {
        String sql = "INSERT INTO customers (name, phone, address, id_proof) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getIdProof());
            int affected = ps.executeUpdate();
            if (affected == 0) return -1;
            try (ResultSet keys = ps.getGeneratedKeys()) 
            {
                if (keys.next()) return keys.getInt(1);
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return -1;
    }

    public Customer findById(int id) 
    {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    Customer c = new Customer();
                    c.setCustomerId(rs.getInt("customer_id"));
                    c.setName(rs.getString("name"));
                    c.setPhone(rs.getString("phone"));
                    c.setAddress(rs.getString("address"));
                    c.setIdProof(rs.getString("id_proof"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    return c;
                }
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        	}
        return null;
    }

    public boolean updateCustomer(Customer c, String changedFields) 
    {
        String sql = "UPDATE customers SET name=?, phone=?, address=?, id_proof=? WHERE customer_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getIdProof());
            ps.setInt(5, c.getCustomerId());
            int upd = ps.executeUpdate();
            if (upd > 0) 
            {
                logUpdate(c.getCustomerId(), changedFields);
                return true;
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return false;
    }

    private void logUpdate(int customerId, String changedFields) 
    {
        String sql = "INSERT INTO customer_updates (customer_id, updated_fields) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, customerId);
            ps.setString(2, changedFields);
            ps.executeUpdate();
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
    }

    public boolean deleteCustomer(int id) 
    {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, id);
            int d = ps.executeUpdate();
            return d > 0;
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return false;
    }

    public List<Customer> searchByNameOrPhoneOrId(String q) 
    {
        String sql = "SELECT * FROM customers WHERE name LIKE ? OR phone LIKE ? OR customer_id = ?";
        List<Customer> res = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, "%" + q + "%");
            ps.setString(2, "%" + q + "%");
            int id = -1;
            try 
            { 
            	id = Integer.parseInt(q); 
            	} 
            catch (NumberFormatException ex) 
            { 
            	id = -1; 
            }
            if (id == -1) ps.setNull(3, Types.INTEGER);
            
            else ps.setInt(3, id);
            
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    Customer c = new Customer();
                    c.setCustomerId(rs.getInt("customer_id"));
                    c.setName(rs.getString("name"));
                    c.setPhone(rs.getString("phone"));
                    c.setAddress(rs.getString("address"));
                    c.setIdProof(rs.getString("id_proof"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    res.add(c);
                }
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return res;
    }

    public List<Customer> findAll(int offset, int limit) 
    {
        String sql = "SELECT * FROM customers ORDER BY customer_id LIMIT ? OFFSET ?";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) 
                {
                    Customer c = new Customer();
                    c.setCustomerId(rs.getInt("customer_id"));
                    c.setName(rs.getString("name"));
                    c.setPhone(rs.getString("phone"));
                    c.setAddress(rs.getString("address"));
                    c.setIdProof(rs.getString("id_proof"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(c);
                }
            }
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return list;
    }

    public int countAll() 
    {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) 
        {
            if (rs.next()) return rs.getInt(1);
        } 
        catch (SQLException e) 
        { 
        	e.printStackTrace(); 
        }
        return 0;
    }
}
