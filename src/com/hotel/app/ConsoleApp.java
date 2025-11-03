package com.hotel.app;

import com.hotel.dao.*;
import com.hotel.model.*;
import com.hotel.util.DBUtil;
import com.hotel.util.HashUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp 
{
    private static final Scanner sc = new Scanner(System.in);
    private static final AdminDAO adminDAO = new AdminDAO();
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final RoomDAO roomDAO = new RoomDAO();
    private static final BookingDAO bookingDAO = new BookingDAO();

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final int LOCK_MINUTES = 5;

    public static void main(String[] args) 
    {
        System.out.println("=== Hotel Management System (Console) ===");

        if (!adminLoginFlow()) 
        {
            System.out.println("Exiting system.");
            return;
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addNewCustomer(); break;
                case "2": updateCustomer(); break;
                case "3": deleteCustomer(); break;
                case "4": addNewRoom(); break;
                case "5": updateRoom(); break;
                case "6": deleteRoom(); break;
                case "7": checkInCustomer(); break;
                case "8": checkOutCustomer(); break;
                case "9": searchCustomer(); break;
                case "10": searchRoom(); break;
                case "11": viewAllCustomers(); break;
                case "12": viewAllRooms(); break;
                case "13": generateBill(); break;
                case "14": running = exitFlow(); break;
                default: System.out.println("Invalid option.");
            }
        }
        closeResources();
    }

    private static boolean adminLoginFlow() 
    {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        Admin admin = adminDAO.findByUsername(username);
        if (admin == null) {
            System.out.println("Invalid username.");
            return false;
        }
        Timestamp lockedUntil = admin.getLockedUntil();
        if (lockedUntil != null && lockedUntil.after(Timestamp.valueOf(LocalDateTime.now()))) 
        {
            System.out.println("Account locked until: " + lockedUntil);
            return false;
        }

        int attempts = 0;
        while (attempts < MAX_LOGIN_ATTEMPTS) 
        {
            System.out.print("Password: ");
            String password = sc.nextLine().trim();
            String hash = HashUtil.sha256(password);
            if (hash.equals(admin.getPasswordHash())) 
            {
                adminDAO.logAttempt(username, true);
                adminDAO.unlockAccount(username);
                System.out.println("Login successful. Welcome, " + username + "!");
                return true;
            } 
            else 
            {
                attempts++;
                adminDAO.logAttempt(username, false);
                System.out.println("Incorrect password. Attempt " + attempts + " of " + MAX_LOGIN_ATTEMPTS);
                if (attempts >= MAX_LOGIN_ATTEMPTS) {
                    adminDAO.lockAccount(username, LOCK_MINUTES);
                    System.out.println("Too many attempts. Account locked for " + LOCK_MINUTES + " minutes.");
                    return false;
                }
            }
        }
        return false;
    }

    private static void printMainMenu() 
    {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Add New Customer");
        System.out.println("2. Update Customer");
        System.out.println("3. Delete Customer");
        System.out.println("4. Add New Room");
        System.out.println("5. Update Room");
        System.out.println("6. Delete Room");
        System.out.println("7. Check-in Customer");
        System.out.println("8. Check-out Customer");
        System.out.println("9. Search Customer");
        System.out.println("10. Search Room");
        System.out.println("11. View All Customers");
        System.out.println("12. View All Rooms");
        System.out.println("13. Generate Billing Report (for completed booking)");
        System.out.println("14. Exit");
        System.out.print("Choose: ");
    }

    private static void addNewCustomer() 
    {
        System.out.println("\n--- Add New Customer ---");
        System.out.print("Name: "); String name = sc.nextLine().trim();
        System.out.print("Phone (10 digits): "); String phone = sc.nextLine().trim();
        System.out.print("Address: "); String address = sc.nextLine().trim();
        System.out.print("ID Proof: "); String idProof = sc.nextLine().trim();

        if (name.isEmpty() || phone.isEmpty()) 
        {
            System.out.println("Name and phone are mandatory.");
            return;
        }
        if (!phone.matches("\\d{10}")) 
        {
            System.out.println("Phone must be 10 digits.");
            return;
        }
        Customer c = new Customer();
        c.setName(name);
        c.setPhone(phone);
        c.setAddress(address);
        c.setIdProof(idProof);

        int id = customerDAO.addCustomer(c);
        if (id > 0) System.out.println("Customer added successfully. Customer ID: " + id);
        else System.out.println("Failed to add customer.");
    }
    private static void updateCustomer() 
    {
        System.out.println("\n--- Update Customer ---");
        System.out.print("Customer ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Customer c = customerDAO.findById(id);
        if (c == null) 
        { 
        	System.out.println("Customer not found."); return; 
        }

        System.out.println("Leave blank to keep existing value.");
        System.out.println("Current name: " + c.getName());
        System.out.print("New name: "); String name = sc.nextLine().trim();
        System.out.println("Current phone: " + c.getPhone());
        System.out.print("New phone: "); String phone = sc.nextLine().trim();
        System.out.println("Current address: " + c.getAddress());
        System.out.print("New address: "); String address = sc.nextLine().trim();
        System.out.println("Current ID proof: " + c.getIdProof());
        System.out.print("New ID proof: "); String idProof = sc.nextLine().trim();

        StringBuilder changed = new StringBuilder();
        if (!name.isEmpty()) 
        { 
        	changed.append("name,"); c.setName(name); 
        }
        if (!phone.isEmpty()) 
        {
            if (!phone.matches("\\d{10}")) 
            { 
            	System.out.println("Phone invalid. Aborting update."); 
            	
            	return; 
            }
            changed.append("phone,"); c.setPhone(phone);
        }
        if (!address.isEmpty()) 
        { 
        	changed.append("address,"); 
        	c.setAddress(address); 
        }
        if (!idProof.isEmpty()) 
        { 
        	changed.append("id_proof,"); c.setIdProof(idProof);
        }

        if (changed.length() == 0) 
        { 
        	System.out.println("No changes made."); 
        	return; 
        }

        boolean ok = customerDAO.updateCustomer(c, changed.toString());
        if (ok) System.out.println("Customer updated.");
        else System.out.println("Update failed.");
    }

    private static void deleteCustomer() 
    {
        System.out.println("\n--- Delete Customer ---");
        System.out.print("Customer ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Customer c = customerDAO.findById(id);
        if (c == null) 
        { 
        	System.out.println("Customer not found."); 
        return; 
        }
        System.out.println("Confirm delete customer " + c.getName() + " (y/N): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (!"y".equals(confirm)) { System.out.println("Deletion cancelled."); 
        return; 
        }
        boolean ok = customerDAO.deleteCustomer(id);
        if (ok) System.out.println("Customer deleted.");
        else System.out.println("Deletion failed.");
    }

    private static void addNewRoom() 
    {
        System.out.println("\n--- Add New Room ---");
        System.out.print("Room Number: "); String rnum = sc.nextLine().trim();
        System.out.print("Type (Single/Double/Deluxe/Suite): "); String type = sc.nextLine().trim();
        System.out.print("Price per night: "); String price = sc.nextLine().trim();

        if (rnum.isEmpty() || type.isEmpty() || price.isEmpty()) 
        {
            System.out.println("All fields are mandatory.");
            return;
        }
        if (!type.matches("Single|Double|Deluxe|Suite")) 
        {
            System.out.println("Invalid type.");
            return;
        }
        try {
            BigDecimal p = new BigDecimal(price);
            Room r = new Room();
            r.setRoomNumber(rnum);
            r.setType(type);
            r.setPricePerNight(p);
            r.setStatus("AVAILABLE");
            boolean ok = roomDAO.addRoom(r);
            if (ok) System.out.println("Room added successfully.");
            else System.out.println("Failed to add room (maybe duplicate room number).");
        } 
        catch (NumberFormatException ex) 
        {
            System.out.println("Invalid price.");
        }
    }

    private static void updateRoom() 
    {
        System.out.println("\n--- Update Room ---");
        System.out.print("Room Number: "); String rnum = sc.nextLine().trim();
        Room r = roomDAO.findByRoomNumber(rnum);
        if (r == null) 
        { 
        	System.out.println("Room not found."); 
        return; 
        }

        System.out.println("Leave blank to keep existing value.");
        System.out.println("Current type: " + r.getType());
        System.out.print("New type: "); String type = sc.nextLine().trim();
        System.out.println("Current price: " + r.getPricePerNight());
        System.out.print("New price: "); String price = sc.nextLine().trim();
        System.out.println("Current status: " + r.getStatus());
        System.out.print("New status (AVAILABLE/OCCUPIED/MAINTENANCE): "); String status = sc.nextLine().trim();

        StringBuilder changed = new StringBuilder();
        if (!type.isEmpty()) 
        {
            if (!type.matches("Single|Double|Deluxe|Suite")) { System.out.println("Invalid type."); 
            return; 
            }
            r.setType(type); changed.append("type,");
        }
        if (!price.isEmpty()) 
        {
            try 
            {
                r.setPricePerNight(new BigDecimal(price)); changed.append("price,");
            } 
            catch (NumberFormatException ex) 
            { 
            	System.out.println("Invalid price."); 
            	
            	return; 
            }
        }
        if (!status.isEmpty()) 
        {
            if (!status.matches("AVAILABLE|OCCUPIED|MAINTENANCE")) 
            { 
            	System.out.println("Invalid status."); 
            	return; 
            }
            r.setStatus(status); changed.append("status,");
        }
        if (changed.length() == 0) 
        { 
        	System.out.println("No changes."); 
        	return; 
        }
        boolean ok = roomDAO.updateRoom(r, changed.toString());
        if (ok) System.out.println("Room updated.");
        else System.out.println("Update failed.");
    }

    private static void deleteRoom() 
    {
        System.out.println("\n--- Delete Room ---");
        System.out.print("Room Number: "); String rnum = sc.nextLine().trim();
        Room r = roomDAO.findByRoomNumber(rnum);
        if (r == null) 
        { 
        	System.out.println("Room not found."); 
        return; 
        }
        boolean ok = roomDAO.deleteRoomIfNotBooked(rnum);
        if (ok) System.out.println("Room deleted.");
        else 
        	System.out.println("Cannot delete room - either booked/occupied or deletion failed.");
    }

    private static void checkInCustomer() 
    {
        System.out.println("\n--- Check-in Customer ---");
        System.out.print("Customer ID: "); int cid = Integer.parseInt(sc.nextLine().trim());
        Customer c = customerDAO.findById(cid);
        if (c == null) 
        { 
        	System.out.println("Customer not found."); 
        return; 
        }
        System.out.print("Room Number to allocate: "); 
        String rnum = sc.nextLine().trim();
        Room r = roomDAO.findByRoomNumber(rnum);
        if (r == null) 
        { 
        	System.out.println("Room not found."); 
        	return; 
        }
        if (!"AVAILABLE".equals(r.getStatus())) 
        { 
        	System.out.println("Room not available."); 
        	return; 
        }

        try (Connection conn = DBUtil.getConnection()) 
        {
            conn.setAutoCommit(false);
            try 
            {
                int bookingId = bookingDAO.createBooking(cid, r.getRoomId(), Timestamp.valueOf(LocalDateTime.now()), "CHECKED_IN");
               
                if (bookingDAO.allocateRoomAndMarkOccupied(r.getRoomId())) 
                {
                    conn.commit();
                    System.out.println("Checked in. Booking ID: " + bookingId + ", Room: " + rnum);
                } 
                else 
                {
                    conn.rollback();
                    System.out.println("Failed to mark room as occupied.");
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
        catch (Exception ex) 
        { 
        	ex.printStackTrace(); 
        }
    }

    private static void checkOutCustomer() 
    {
        System.out.println("\n--- Check-out Customer ---");
        System.out.print("Booking ID: "); int bid = Integer.parseInt(sc.nextLine().trim());
        Booking b = bookingDAO.findById(bid);
        if (b == null) 
        { 
        	System.out.println("Booking not found."); 
       
        	return; 
        }
        if (!"CHECKED_IN".equals(b.getStatus())) 
        { 
        	System.out.println("Booking is not active (CHECKED_IN)."); 
        	return; 
        }
        boolean ok = bookingDAO.checkOut(bid);
        if (ok) System.out.println("Check-out successful. Bill generated.");
        else 
        	System.out.println("Check-out failed.");
    }

    private static void searchCustomer() 
    {
        System.out.println("\n--- Search Customer ---");
        System.out.print("Enter name / phone / id: ");
        String q = sc.nextLine().trim();
        if (q.isEmpty()) 
        { 
        	System.out.println("Search input required."); 
       
        	return; 
        }
        List<Customer> list = customerDAO.searchByNameOrPhoneOrId(q);
        if (list.isEmpty()) 
        { 
        	System.out.println("No results."); 
        	
        	return; 
        }
        System.out.printf("%-6s %-20s %-12s %-25s\n", "ID", "Name", "Phone", "Address");
        for (Customer c : list) {
        	
            System.out.printf("%-6d %-20s %-12s %-25s\n", c.getCustomerId(), c.getName(), c.getPhone(), c.getAddress());
        }
    }

    private static void searchRoom() 
    {
        System.out.println("\n--- Search Room ---");
        System.out.print("Room Number (exact or blank): "); String rnum = sc.nextLine().trim();
        System.out.print("Type (Single/Double/Deluxe/Suite or blank): "); String type = sc.nextLine().trim();
        System.out.print("Status (AVAILABLE/OCCUPIED/MAINTENANCE or blank): "); String status = sc.nextLine().trim();
        
        List<Room> rooms = roomDAO.search(rnum.isEmpty() ? null : rnum, type.isEmpty() ? null : type, status.isEmpty() ? null : status);
      
        if (rooms.isEmpty()) { System.out.println("No rooms found."); return; }
        System.out.printf("%-10s %-8s %-8s %-10s\n", "RoomNo", "Type", "Status", "Price");
        for (Room r : rooms) 
        {
            System.out.printf("%-10s %-8s %-8s %-10s\n", r.getRoomNumber(), r.getType(), r.getStatus(), r.getPricePerNight());
        }
    }

    private static void viewAllCustomers() 
    {
        System.out.println("\n--- All Customers ---");
        int total = customerDAO.countAll();
        int pageSize = 10;
        int pages = (total + pageSize - 1) / pageSize;
        if (total == 0) 
        { 
        	System.out.println("No customers."); return; 
        }
        for (int p = 0; p < pages; p++) 
        {
            System.out.println("Page " + (p + 1) + "/" + pages);
           
            List<Customer> list = customerDAO.findAll(p * pageSize, pageSize);
            
            System.out.printf("%-6s %-20s %-12s %-10s\n", "ID", "Name", "Phone", "CreatedAt");
            for (Customer c : list) 
            {
                System.out.printf("%-6d %-20s %-12s %-10s\n", c.getCustomerId(), c.getName(), c.getPhone(), c.getCreatedAt());
            }
            if (p < pages - 1) 
            {
                System.out.print("Next page? (y/N): ");
                String nx = sc.nextLine().trim().toLowerCase();
                if (!"y".equals(nx)) break;
            }
        }
    }

    private static void viewAllRooms() 
    {
        System.out.println("\n--- All Rooms ---");
       
        List<Room> rooms = roomDAO.findAll();
        
        if (rooms.isEmpty()) { System.out.println("No rooms."); 
        return; 
        }
        System.out.printf("%-10s %-8s %-8s %-10s\n", "RoomNo", "Type", "Status", "Price");
        for (Room r : rooms) 
        {
            String marker = "AVAILABLE".equals(r.getStatus()) ? "" : ( "OCCUPIED".equals(r.getStatus()) ? "*" : "");
            System.out.printf("%-10s %-8s %-8s %-10s %s\n", r.getRoomNumber(), r.getType(), r.getStatus(), r.getPricePerNight(), marker);
        }
        System.out.println("* occupied rooms are marked with '*'");
    }

    private static void generateBill() 
    {
        System.out.println("\n--- Generate Bill ---");
        System.out.print("Booking ID: "); int bid = Integer.parseInt(sc.nextLine().trim());
        Booking b = bookingDAO.findById(bid);
        if (b == null) 
        { 
        	System.out.println("Booking not found."); 
        	return; 
        }
        if (!"CHECKED_OUT".equals(b.getStatus())) 
        { 
        	System.out.println("Booking not completed (CHECKED_OUT)."); 
        	return; 
        }
        Customer c = customerDAO.findById(b.getCustomerId());
      
        Room r = roomDAO.findById(b.getRoomId());
       
        if (c == null || r == null) { System.out.println("Missing data."); return; }
        long days = java.time.temporal.ChronoUnit.DAYS.between(b.getCheckInDate().toLocalDateTime().toLocalDate(), b.getCheckOutDate().toLocalDateTime().toLocalDate());
      
        if (days <= 0) days = 1;
       
        System.out.println("---- BILL ----");
        System.out.println("Customer: " + c.getName());
        System.out.println("Room No: " + r.getRoomNumber());
        System.out.println("Check-in: " + b.getCheckInDate());
        System.out.println("Check-out: " + b.getCheckOutDate());
        System.out.println("Days stayed: " + days);
        System.out.println("Total: " + b.getTotalAmount());
        }

    private static boolean exitFlow() 
    {
        System.out.print("Confirm exit (y/N): ");
        String c = sc.nextLine().trim().toLowerCase();
        if ("y".equals(c)) 
        {
            System.out.println("Goodbye. Closing connections.");
            return false;
        }
        return true;
    }

    private static void closeResources() 
    {
        try 
        { 
        	sc.close(); 
        	} 
        catch (Exception e) 
        {
        	
        }
    }
}
