CREATE DATABASE IF NOT EXISTS pims_db;
USE pims_db;

DROP TABLE IF EXISTS sale_items;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS medicines;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

CREATE TABLE suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

CREATE TABLE medicines (
    medicine_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100) NOT NULL,
    medicine_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT NOT NULL,
    reorder_level INT NOT NULL,
    expiry_date DATE NOT NULL,
    supplier_id INT,
    CONSTRAINT fk_medicine_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT fk_sale_user FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE sale_items (
    sale_item_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_sale_item_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

INSERT INTO users (username, password, role, full_name) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Admin', 'System Administrator'),
('cashier', 'c246650737293ddc18fc357393db78d1ecc9d1fd1af95469115e4a29f983359a', 'Cashier', 'Front Desk Cashier');

INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('MediCore Distributors', 'Lerato M', '011-100-2001', 'sales@medicore.co.za', '12 Main Road, Johannesburg'),
('PharmaLine Wholesale', 'Sipho N', '021-555-1111', 'orders@pharmaline.co.za', '45 Dock Street, Cape Town'),
('HealthSource Ltd', 'Aisha K', '031-444-7890', 'hello@healthsource.co.za', '8 Marine Drive, Durban');

INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Paracetamol 500mg', 'MediLife', 'Tablet', 35.99, 200, 50, '2027-05-30', 1),
('Amoxicillin 250mg', 'CureMed', 'Capsule', 89.50, 80, 30, '2027-02-10', 2),
('Cough Syrup 100ml', 'Relief Pharma', 'Syrup', 59.99, 60, 20, '2026-09-20', 1),
('Vitamin C 1000mg', 'NutriLabs', 'Tablet', 120.00, 150, 40, '2027-11-15', 3),
('Hydrocortisone Cream', 'DermaCare', 'Cream', 75.00, 25, 25, '2026-09-10', 3);
