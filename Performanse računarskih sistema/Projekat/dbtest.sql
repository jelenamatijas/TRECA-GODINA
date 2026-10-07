CREATE DATABASE IF NOT EXISTS dbtest;
USE dbtest;

DROP TABLE IF EXISTS narudzba;
DROP TABLE IF EXISTS korisnik;
DROP TABLE IF EXISTS proizvod;
DROP TABLE IF EXISTS drzava;

CREATE TABLE drzava (
    id INT AUTO_INCREMENT PRIMARY KEY,
    naziv VARCHAR(100) NOT NULL,
    kod VARCHAR(10) NOT NULL
);

CREATE TABLE proizvod (
    id INT AUTO_INCREMENT PRIMARY KEY,
    naziv_proizvoda VARCHAR(150) NOT NULL,
    cijena DECIMAL(10, 2) NOT NULL,
    dostupan BOOLEAN DEFAULT TRUE
);

CREATE TABLE korisnik (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ime_prezime VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL,
    datum_registracije DATETIME DEFAULT CURRENT_TIMESTAMP,
    drzava_id INT NOT NULL,
    FOREIGN KEY (drzava_id) REFERENCES drzava(id)
);

CREATE TABLE narudzba (
    id INT AUTO_INCREMENT PRIMARY KEY,
    kolicina INT NOT NULL,
    ukupna_cijena DECIMAL(12, 2) NOT NULL,
    korisnik_id INT NOT NULL,
    proizvod_id INT NOT NULL,
    FOREIGN KEY (korisnik_id) REFERENCES korisnik(id),
    FOREIGN KEY (proizvod_id) REFERENCES proizvod(id)
);