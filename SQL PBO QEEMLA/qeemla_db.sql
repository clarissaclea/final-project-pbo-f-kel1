CREATE DATABASE qeemla_db;

CREATE TABLE log_audit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    waktu_aksi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    aksi VARCHAR(100),
    detail TEXT,
    email_pengguna VARCHAR(100)
);

CREATE TABLE products (
    kode VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(100),
    kategori VARCHAR(50),
    harga DECIMAL(10, 2),
    stok INT,
    production_date DATE,
    expiry_date DATE,
    photo_path VARCHAR(255)
);
