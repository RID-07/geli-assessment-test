# GELI Assessment Test - Warehouse Management System

Sistem manajemen warehouse berbasis microservices menggunakan Spring Boot dan Spring Cloud Netflix Eureka.

## 📋 Daftar Isi

- [Overview](#overview)
- [Arsitektur](#arsitektur)
- [Teknologi yang Digunakan](#teknologi-yang-digunakan)
- [Prerequisites](#prerequisites)
- [Database Schema](#database-schema)
- [Setup & Installation](#setup--installation)
- [Menjalankan Aplikasi](#menjalankan-aplikasi)
- [API Endpoints](#api-endpoints)
- [Service Ports](#service-ports)

## 🎯 Overview

Sistem ini terdiri dari 3 microservices:

1. **Eureka Service (Discovery Service)** - Service registry menggunakan Eureka Server
2. **Item Service** - Service untuk mengelola data item/barang
3. **Order Service** - Service untuk mengelola data order/pesanan

## 🏗️ Arsitektur

```
┌─────────────────┐
│ Eureka Service  │
│ (Discovery)     │
│ Port: 8761      │
└────────┬────────┘
         │
         ├─────────────────┬
         │                 │                
┌────────▼────────┐ ┌──────▼──────┐
│ Item Service    │ │ Order       │
│ Port: 8080      │ │ Service     │
│                 │ │ Port: 8081  │
└─────────────────┘ └─────────────┘
         │                 │
         └────────┬────────┘
                  │
         ┌────────▼────────┐
         │ MySQL Database   │
         │ warehouse        │
         └──────────────────┘
```

## 🛠️ Teknologi yang Digunakan

- **Java 17**
- **Spring Boot 3.5.8**
- **Spring Cloud 2025.0.0**
- **Spring Cloud Netflix Eureka** - Service Discovery
- **Spring Data JPA** - Database Access
- **MySQL** - Database
- **Lombok** - Code Generation
- **Maven** - Build Tool
- **OpenFeign** - HTTP Client (Order Service)
- **Resilience4j** - Circuit Breaker, Retry, Rate Limiter (Order Service)

## 📦 Prerequisites

Sebelum menjalankan aplikasi, pastikan Anda telah menginstall:

- **Java 17** atau lebih tinggi
- **Maven 3.6+**
- **MySQL 8.0+**
- **IDE** (IntelliJ IDEA, Eclipse, atau VS Code)

## 🗄️ Database Schema

### Tabel `item`

Tabel untuk menyimpan informasi item/barang.

```sql
CREATE TABLE `item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
  `name` varchar(255) NOT NULL COMMENT 'Item name',
  `quantity` int NOT NULL DEFAULT '0',
  `description` varchar(500) DEFAULT NULL COMMENT 'Item description',
  `category` int NOT NULL COMMENT 'Category code (1=Electronics, 2=Clothing, 3=Household)',
  `price_per_item` bigint DEFAULT '0',
  `active` tinyint(1) DEFAULT '0' COMMENT '1 = active, 0 = inactive',
  `create_date` datetime DEFAULT NULL COMMENT 'Record creation timestamp',
  `update_date` datetime DEFAULT NULL COMMENT 'Record last updated timestamp',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Stores basic item information';
```

**Kategori:**
- `1` = Electronics
- `2` = Clothing  
- `3` = Household

### Tabel `order`

Tabel untuk menyimpan informasi order/pesanan.

```sql
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `item_id` bigint NOT NULL,
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price` bigint NOT NULL DEFAULT '0',
  `quantity` int NOT NULL DEFAULT '0',
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

### Sample Data

```sql
INSERT INTO warehouse.item
(id, name, quantity, description, category, price_per_item, active, create_date, update_date)
VALUES
(1, 'SHARP H3C 32 Inch', 5, 'Sharp TV', 1, 2500000, 1, '2025-11-22 12:22:32', NULL),
(2, 'SHARP H30C 50 Inch', 10, 'Sharp TV', 1, 7500000, 1, '2025-11-22 12:22:50', NULL),
(3, 'SAMSUNG HZ20 32 Inch', 5, 'Samsung TV', 1, 5000000, 1, '2025-11-22 12:23:13', NULL),
(4, 'DAIKIN 2 PK', 12, 'Daikin AC', 2, 3500000, 1, '2025-11-22 12:23:39', NULL),
(5, 'MIDEA 1.5 PK', 3, 'Midea AC', 2, 2000000, 1, '2025-11-22 12:24:01', NULL),
(6, 'SAMSUNG 23HVC', 5, 'Samsung Refrigerator', 3, 4000000, 1, '2025-11-22 12:24:31', NULL),
(7, 'Sharp 245C', 4, 'Sharp Refrigerator', 3, 3450000, 1, '2025-11-22 12:24:45', NULL);
```

## 🚀 Setup & Installation

### 1. Setup Database

1. Buat database MySQL dengan nama `warehouse`:
```sql
CREATE DATABASE warehouse CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

2. Jalankan script SQL untuk membuat tabel (lihat bagian [Database Schema](#database-schema))

3. Insert sample data (opsional)

### 2. Konfigurasi Database

Edit file `application.properties` di setiap service untuk menyesuaikan koneksi database:

**eureka-service/src/main/resources/application.properties:**
```properties
server.port=8761
spring.application.name=discovery-service

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

**item-service/src/main/resources/application.properties:**
```properties
server.port=8080
server.servlet.context-path=/item-service

# Database connection
spring.datasource.url=jdbc:mysql://localhost:3306/warehouse?useUnicode=yes&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```

**order-service/src/main/resources/application.properties:**
```properties
server.port=8081
server.servlet.context-path=/order-service

# Database connection
spring.datasource.url=jdbc:mysql://localhost:3306/warehouse?useUnicode=yes&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true

# Resilience4j Configuration
resilience4j.circuitbreaker.instances.itemService.failureRateThreshold=50
resilience4j.circuitbreaker.instances.itemService.waitDurationInOpenState=10s
resilience4j.retry.instances.itemService.maxAttempts=3
resilience4j.retry.instances.itemService.waitDuration=1s
resilience4j.retry.instances.itemService.enableExponentialBackoff=true
resilience4j.timelimiter.instances.itemService.timeoutDuration=5s
resilience4j.ratelimiter.instances.itemService.limitForPeriod=10
resilience4j.ratelimiter.instances.itemService.limitRefreshPeriod=1s
resilience4j.ratelimiter.instances.itemService.timeoutDuration=0
```

## ▶️ Menjalankan Aplikasi

**PENTING:** Jalankan service-service dalam urutan berikut:

### 1. Eureka Service (Harus dijalankan pertama)

```bash
cd eureka-service
./mvnw spring-boot:run
```

Atau menggunakan Maven:
```bash
cd eureka-service
mvn spring-boot:run
```

Service akan berjalan di: `http://localhost:8761`

### 2. Item Service

Buka terminal baru:
```bash
cd item-service
./mvnw spring-boot:run
```

Service akan berjalan di: `http://localhost:8080/item-service`

### 3. Order Service

Buka terminal baru:
```bash
cd order-service
./mvnw spring-boot:run
```

Service akan berjalan di: `http://localhost:8081/order-service`

### Verifikasi

1. Buka browser dan akses Eureka Dashboard: `http://localhost:8761`
2. Pastikan `item-service` dan `order-service` terdaftar di Eureka

## 📡 API Endpoints

### Item Service

Base URL: `http://localhost:8080/item-service`

#### 1. Get List Items
```http
GET /item/list
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Success",
  "data": [
    {
      "id": 1,
      "name": "SHARP H3C 32 Inch",
      "quantity": 5,
      "description": "Sharp TV",
      "category": 1,
      "pricePerItem": 2500000,
      "active": true,
      "createDate": "2025-11-22 12:22:32",
      "updateDate": null
    }
  ]
}
```

#### 2. Get Item Detail
```http
GET /item/detail?id={id}
```

**Parameters:**
- `id` (Long, required) - Item ID

#### 3. Add Item
```http
POST /item/add
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Item Name",
  "quantity": 10,
  "description": "Item Description",
  "category": 1,
  "pricePerItem": 1000000,
  "active": true
}
```

**Category Codes:**
- `1` = Electronics
- `2` = Clothing
- `3` = Household

#### 4. Update Item
```http
POST /item/update?id={id}
Content-Type: application/json
```

**Parameters:**
- `id` (Long, required) - Item ID

**Request Body:** (sama seperti Add Item)

#### 5. Delete Item
```http
DELETE /item/delete?id={id}
```

**Parameters:**
- `id` (Long, required) - Item ID

---

### Order Service

Base URL: `http://localhost:8081/order-service`

#### 1. Get List Orders
```http
GET /order/list
```

#### 2. Get Order Detail
```http
GET /order/detail?id={id}
```

**Parameters:**
- `id` (Long, required) - Order ID

#### 3. Add Order
```http
POST /order/add
Content-Type: application/json
```

**Request Body:**
```json
{
  "customerName": "John Doe",
  "itemId": 1,
  "quantity": 2
}
```

**Note:** Service akan otomatis mengambil harga dari Item Service dan menghitung total price.

#### 4. Update Order
```http
POST /order/update?id={id}
Content-Type: application/json
```

**Parameters:**
- `id` (Long, required) - Order ID

**Request Body:** (sama seperti Add Order)

#### 5. Delete Order
```http
DELETE /order/delete?id={id}
```

**Parameters:**
- `id` (Long, required) - Order ID

---

## 🔌 Service Ports

| Service | Port | Context Path | Eureka URL |
|---------|------|--------------|------------|
| Eureka Service | 8761 | - | http://localhost:8761 |
| Item Service | 8080 | /item-service | http://localhost:8080/item-service |
| Order Service | 8081 | /order-service | http://localhost:8081/order-service |

## 🔧 Resilience4j Configuration (Order Service)

Order Service menggunakan Resilience4j untuk meningkatkan reliability:

- **Circuit Breaker:**
  - Failure Rate Threshold: 50%
  - Wait Duration in Open State: 10s

- **Retry:**
  - Max Attempts: 3
  - Wait Duration: 1s
  - Exponential Backoff: Enabled

- **Time Limiter:**
  - Timeout Duration: 5s

- **Rate Limiter:**
  - Limit for Period: 10 requests
  - Limit Refresh Period: 1s

## 📁 Struktur Project

```
geli-assessment-test/
├── eureka-service/          # Service Discovery (Eureka Server)
│   ├── src/
│   ├── pom.xml
│   └── ...
├── item-service/            # Item Management Service
│   ├── src/
│   ├── pom.xml
│   └── ...
├── order-service/           # Order Management Service
│   ├── src/
│   ├── pom.xml
│   └── ...
└── README.md               # Dokumentasi ini
```

## 📝 Catatan Penting

1. **Service Discovery:** Pastikan Eureka Service berjalan sebelum menjalankan service lainnya
2. **Database:** Pastikan MySQL sudah berjalan dan database `warehouse` sudah dibuat
3. **Port Conflicts:** Pastikan port 8761, 8080, dan 8081 tidak digunakan oleh aplikasi lain
4. **Timezone:** Aplikasi menggunakan timezone `Asia/Jakarta`

### Service tidak terdaftar di Eureka
- Pastikan Eureka Service sudah berjalan
- Cek konfigurasi `eureka.client.service-url.defaultZone` di application.properties
- Pastikan tidak ada firewall yang memblokir komunikasi antar service

### Database Connection Error
- Pastikan MySQL sudah berjalan
- Cek username dan password di application.properties
- Pastikan database `warehouse` sudah dibuat

### Port Already in Use
- Cek aplikasi yang menggunakan port tersebut
- Ubah port di application.properties jika diperlukan

