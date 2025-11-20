-- Création du schéma (si pas déjà créé)
CREATE DATABASE IF NOT EXISTS hospital_bed_manager
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hospital_bed_manager;

-- Suppression dans le bon ordre (FK)
DROP TABLE IF EXISTS hospital_stay;
DROP TABLE IF EXISTS bed;
DROP TABLE IF EXISTS patient;

-- Table patient
CREATE TABLE patient (
                         id            VARCHAR(50)  NOT NULL PRIMARY KEY,
                         first_name    VARCHAR(100) NOT NULL,
                         last_name     VARCHAR(100) NOT NULL,
                         birth_date    DATE         NOT NULL,
                         sex           VARCHAR(10)  NOT NULL, -- MALE / FEMALE / OTHER
                         pmr           BOOLEAN      NOT NULL,
                         isolation     BOOLEAN      NOT NULL,
                         phone_number  VARCHAR(30),
                         notes         TEXT
) ENGINE=InnoDB;

-- Table bed
CREATE TABLE bed (
                     id                 VARCHAR(50)  NOT NULL PRIMARY KEY,
                     room_id            VARCHAR(50)  NOT NULL,
                     code               VARCHAR(50)  NOT NULL, -- ex: A01-1
                     status             VARCHAR(20)  NOT NULL, -- AVAILABLE, OCCUPIED, CLEANING, OUT_OF_ORDER
                     isolation_capable  BOOLEAN      NOT NULL
) ENGINE=InnoDB;

-- Table hospital_stay
CREATE TABLE hospital_stay (
                               id                       VARCHAR(50) NOT NULL PRIMARY KEY,
                               patient_id               VARCHAR(50) NOT NULL,
                               bed_id                   VARCHAR(50) NOT NULL,
                               stay_type                VARCHAR(10) NOT NULL, -- WEEK / DAY
                               admission_date           DATE        NOT NULL,
                               discharge_date_planned   DATE        NULL,
                               discharge_date_effective DATE        NULL,

                               CONSTRAINT fk_stay_patient
                                   FOREIGN KEY (patient_id) REFERENCES patient(id),

                               CONSTRAINT fk_stay_bed
                                   FOREIGN KEY (bed_id) REFERENCES bed(id)
) ENGINE=InnoDB;
