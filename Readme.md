# 🅿️ Parking El Paisa

Sistema de gestión de parqueaderos desarrollado con Java Servlets, MySQL y HTML/CSS/JS.

## 🛠️ Requisitos

- Java 11
- Apache Tomcat 9
- MySQL 8
- IntelliJ IDEA

## ⚙️ Configuración

### 1. Base de datos
Ejecutar en MySQL Workbench:
```sql
CREATE DATABASE parking_management_db;
USE parking_management_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE parking_spots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20)
);

CREATE TABLE tariffs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    vehicle_type_id INT,
    hourly_rate DOUBLE,
    is_active BOOLEAN
);

CREATE TABLE parking_tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20),
    vehicle_type_id INT,
    user_id INT,
    entry_time DATETIME,
    exit_time DATETIME,
    total_amount DOUBLE,
    status VARCHAR(20)
);

INSERT INTO tariffs(vehicle_type_id, hourly_rate, is_active) VALUES (1, 5000, true), (2, 3000, true);
INSERT INTO parking_spots(status) VALUES ('AVAILABLE'),('AVAILABLE'),('AVAILABLE'),('AVAILABLE'),('AVAILABLE'),('AVAILABLE'),('AVAILABLE'),('AVAILABLE'),('AVAILABLE'),('AVAILABLE');
```

### 2. Credenciales de BD
En `src/main/java/com/parking/util/DBConexion.java` ajusta:
```java
private static final String USUARIO = "root";
private static final String PASSWORD = "tu_contraseña";
```

### 3. Ejecutar
1. Abrir proyecto en IntelliJ IDEA
2. Configurar Tomcat 9 como servidor
3. Hacer Deploy del artifact `ParkingElPaisa:war exploded`
4. Abrir `http://localhost:8080/ParkingElPaisa`

## 👤 Roles
- **Operador**: registra entradas y salidas de vehículos
- **Cliente**: consulta su historial de parqueos

## 🚗 Funcionalidades
- Registro e inicio de sesión de usuarios
- Registro de entrada y salida de vehículos
- Cobro automático por horas
- Control de cupos disponibles
- Validación de vehículos duplicados
