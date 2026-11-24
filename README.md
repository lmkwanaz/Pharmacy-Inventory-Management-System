Pharmacy Inventory Management System (PIMS) - User Manual
===========================================================

1. Overview
-----------
PIMS is a Java Swing desktop application for HealthFirst Pharmacy.
It supports:
- User login and role-based access
- Point of Sale (Cashier)
- Inventory and supplier management (Admin)
- User management for cashier accounts (Admin)
- Report generation (Sales, Item-Wise, Low Stock, Expiry)

2. Technology Stack
-------------------
- Java (Swing GUI)
- JDBC (MySQL connectivity)
- MySQL Database

3. Default Login Credentials
----------------------------
Admin account:
- Username: admin
- Password: admin123

Cashier account:
- Username: cashier
- Password: cash123

4. Database Setup
-----------------
1. Open MySQL Workbench (or mysql command line).
2. Execute the script in database.sql.
3. This creates the database pims_db, all required tables, and sample data.

5. Configure Database Credentials in the App
--------------------------------------------
Open src/pims/config/AppConfig.java and set:
- DB_URL
- DB_USER
- DB_PASSWORD

Default values in code are:
- jdbc:mysql://localhost:3306/pims_db?useSSL=false&serverTimezone=UTC
- root
- root

6. Build and Run (from project root)
------------------------------------
Compile:
  javac -d out src/pims/Main.java src/pims/**/*.java

Run:
  java -cp out pims.Main

Note:
- Ensure MySQL JDBC driver is available in your classpath.
- Example driver name: mysql-connector-j-8.x.x.jar

7. Functional Modules
---------------------
A) Authentication Module
- Login with username and password.
- Redirected automatically based on role:
  - Admin -> Admin Dashboard
  - Cashier -> Cashier Dashboard

B) Cashier Module
- Point of Sale:
  - Search medicine
  - Add items to cart
  - Clear cart
  - Checkout to process sale
- Billing:
  - Auto-generated bill window
  - Option to print
  - Option to save bill to .txt
- Stock Check:
  - Search and view price/availability without sale

C) Administrator Module
- Manage Medicines (CRUD)
- Manage Suppliers (CRUD)
- Manage Users (create/delete Cashier accounts)
- Reports:
  - Sales Report
  - Item-Wise Sales Report
  - Low Stock Report
  - Expiry Report (next 1 month)

8. Screenshots Required for Submission
--------------------------------------
Capture and place in /screenshots:
- Login Screen
- Admin Dashboard (tabs visible)
- Manage Medicines tab with data
- Cashier POS screen with cart items
- Generated Bill window
- Reports:
  - Sales
  - Item-Wise
  - Low Stock
  - Expiry

9. Packaging for Submission
---------------------------
Zip file name format:
  YourName_StudentNumber_PRO732.zip

Include in zip:
1) Executable (.exe) named yourname_pims.exe
2) /src folder with all .java files
3) database.sql
4) /screenshots folder
5) README.txt

10. Suggested .exe Packaging Tools
----------------------------------
To produce the required .exe, use one of these wrappers:
- Launch4j
- jpackage (JDK 14+)

You may first create a runnable JAR, then wrap it into .exe.
