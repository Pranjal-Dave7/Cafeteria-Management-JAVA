CREATE DATABASE IF NOT EXISTS cafeteria.db;
USE cafeteria_db;

CREATE TABLE IF NOT EXISTS Freezeland(
    ItemID INT PRIMARY KEY AUTO_INCREMENT,
    ItemNAme VARCHAR(100) NOT NULL,
    Price DECIMAL(10,2) NOT NULL
);

INSERT INTO Freezeland(ItemID, ItemName, Price) VALUES
(101, 'Club Sandwich', 120.00),
(102, 'Cold Coffee', 80.00),
(103, 'Cheese Pizza', 180.00);

CREATE TABLE IF NOT EXISTS Apna_Adda(
    ItemID INT PRIMARY KEY AUTO_INCREMENT,
    ItemName VARCHAR(100) NOT NULL,
    Price DECIMAL (10,2) NOT NULL
);

INSERT INTO Apna_Adda(ItemID, ItemName, Price)VALUES
(201, 'Masala Dosa', 90.00),
(202, 'Filter Coffee', 40.00);

CREATE TABLE IF NOT EXISTS Mumbaiya(
    ItemID INT PRIMARY KEY AUTO_INCREMENT,
    ItemName VARCHAR(100) NOT NULL,
    Price DECIMAL (10,2) NOT NULL
);

INSERT INTO Mumbaiya(ItemID, ItemNAme, Price)VALUES
(301, 'Vada Pav', 35.00),
(302, 'Pav Bhaji', 110.00);

CREATE TABLE IF NOT EXISTS Nescafe(
    ItemID INT PRIMARY KEY AUTO_INCREMENT,
    ItemName VARCHAR(100) NOT NULL,
    Price DECIMAL(10,2) NOT NULL
);

INSERT INTO Nescafe(ItemID, ItemName, Price) VALUES
(401, 'Iced Latte', 95.00),
(402, 'Hazelnut Coffee', 120.00);