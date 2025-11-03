Hotel Management System (Console-Based | Core Java + JDBC + MySQL)

Project Overview
The Hotel Management System is a console-based Java application designed to manage hotel operations such as Admin Login, Customer Management, Room Management, Booking, and Billing.  
It follows a modular architecture, demonstrating the use of Core Java, JDBC, and MySQL for real-world application development.


Features (User Stories)

1: Admin Login
- Validate username and password against the "admin" table.
- Allow only 3 failed attempts, then lock for 5 minutes.
- Display success/failure messages.
- Log all login attempts in the "login_attempts" table.

2: Add New Customer
- Add customer details: name, contact, address, ID proof.
- Validate 10-digit mobile number and mandatory fields.
- Auto-generate "customer_id".
- Store data in the "customers" table.

3: Update Customer Details
- Update existing customer details after validation.
- Ensure "customer_id" exists.
- Log updates with timestamps in "customer_history".

4: Delete Customer Record
- Validate "customer_id" before deletion.
- Confirm deletion from the user.
- Remove related bookings from the database.

5: Room Management (Add Room)
- Add rooms with room type, price, and availability.
- Room types: Single, Double, Deluxe, Suite.
- Store in the "rooms" table with unique room numbers.

6: Update Room Details
- Update room type, price, and status.
- Log all changes in the "room_history" table.

7: Delete Room
- Validate room existence before deletion.
- Prevent deletion of currently booked rooms.

8: Check-In Customer
- Assign an available room to a customer.
- Record check-in date/time and update room status to Occupied.

9: Check-Out Customer
- Validate active booking.
- Calculate bill: (days stayed × room price).
- Update room status to Available.
- Record check-out date/time and display bill.

10: Search Customer
- Search customers by name, contact, or ID (partial matches allowed).
- Display results in a clean tabular format.

11: Search Room
- Search rooms by number, type, or status.
- Display results in a neat console table.

12: View All Customers
- Display list of all customers with booking status.
- Support pagination for large datasets.

13: View All Rooms
- Display all rooms with type, price, and status.
- Highlight occupied rooms.

14: Generate Billing Report
- Display customer, room, check-in/out details, and total bill.
- Option to save bill in a text file.

15: Exit System
- Prompt for confirmation before exit.
- Close all database connections safely.
- Display farewell message.

--Tech Stack
- Programming Language: Core Java  
- Database: MySQL  
- Connectivity: JDBC  
- Paradigm: Object-Oriented Programming (OOP)  
- Concepts Used: Encapsulation, Inheritance, Polymorphism, Abstraction  

--Database Design
TABLES:
admin — Stores admin credentials and lock status
login_attempts — Logs every login attempt
customers — Stores customer details
rooms — Stores room information
bookings — Stores check-in/check-out records
customer_history — Logs customer updates
room_history — Logs room changes

--CORE CONCEPTS DEMONSTRATED
Follows Software Development Life Cycle (SDLC) — from requirement analysis to testing.
Implemented role-based access (Admin & Receptionist).
Structured modules for authentication, customer, room, and billing management.
Emphasis on system scalability, performance optimization, and debugging techniques.
Uses prepared statements to prevent SQL injection and improve database security.

--HOW TO RUN THE PROJECT
Clone the repository
git clone https://github.com/your-username/Hotel-Management-System.git
cd Hotel-Management-System
