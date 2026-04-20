USE sonriapp;

-- ========================
-- ELIMINAR TABLAS
-- ========================
DROP TABLE IF EXISTS citas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS pacientes;
DROP TABLE IF EXISTS servicios;
DROP TABLE IF EXISTS roles;

-- ========================
-- TABLA ROLES
-- ========================
CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       nombre VARCHAR(50) NOT NULL UNIQUE
);

-- ========================
-- TABLA USUARIOS
-- ========================
CREATE TABLE usuarios (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          dni VARCHAR(15) UNIQUE,
                          nombre VARCHAR(100) NOT NULL,
                          apellido VARCHAR(100) NOT NULL,
                          correo VARCHAR(150) NOT NULL UNIQUE,
                          telefono VARCHAR(9),
                          fecha_nacimiento DATE,
                          password VARCHAR(255) NOT NULL,
                          rol_id INT NOT NULL,
                          CONSTRAINT fk_usuario_rol
                              FOREIGN KEY (rol_id) REFERENCES roles(id)
                                  ON DELETE RESTRICT ON UPDATE CASCADE
);

-- ========================
-- TABLA PACIENTES
-- ========================
CREATE TABLE pacientes (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           dni VARCHAR(15) UNIQUE,
                           nombre VARCHAR(100) NOT NULL,
                           apellido VARCHAR(100) NOT NULL,
                           telefono VARCHAR(9),
                           fecha_nacimiento DATE,
                           genero VARCHAR(20),
                           direccion VARCHAR(255),
                           contacto_emergencia VARCHAR(9),
                           antecedentes TEXT
);

-- ========================
-- TABLA SERVICIOS
-- ========================
CREATE TABLE servicios (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           nombre VARCHAR(150) NOT NULL,
                           duracion INT NOT NULL,
                           precio DECIMAL(10,2) NOT NULL,
                           descripcion TEXT
);

-- ========================
-- TABLA CITAS
-- ========================
CREATE TABLE citas (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       paciente_id INT NOT NULL,
                       doctor_id INT NOT NULL,
                       servicio_id INT NOT NULL,
                       fecha DATE NOT NULL,
                       hora TIME NOT NULL,
                       estado ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'ATENDIDA') DEFAULT 'PENDIENTE',

                       CONSTRAINT fk_cita_paciente
                           FOREIGN KEY (paciente_id) REFERENCES pacientes(id)
                               ON DELETE CASCADE ON UPDATE CASCADE,

                       CONSTRAINT fk_cita_doctor
                           FOREIGN KEY (doctor_id) REFERENCES usuarios(id)
                               ON DELETE RESTRICT ON UPDATE CASCADE,

                       CONSTRAINT fk_cita_servicio
                           FOREIGN KEY (servicio_id) REFERENCES servicios(id)
                               ON DELETE RESTRICT ON UPDATE CASCADE
);