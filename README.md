# Product Pricing Application

## Description

This application implements a **Spring Boot REST service** that calculates the applicable price of a product based on the application date, product ID, and brand ID.

The service simulates an e-commerce pricing system where multiple price rules may apply to the same product during overlapping time ranges. When this happens, the rule with the **highest priority** is selected.

The solution follows a **layered architecture inspired by Hexagonal Architecture**, separating domain logic, application use cases, and infrastructure concerns such as persistence, caching, and resilience.

---

# PRICES Table

The database includes a `PRICES` table, which stores pricing rules for products during specific time periods.

| BRAND_ID | START_DATE          | END_DATE            | PRICE_LIST | PRODUCT_ID | PRIORITY | PRICE | CURR |
| -------- | ------------------- | ------------------- | ---------- | ---------- | -------- | ----- | ---- |
| 1        | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 1          | 35455      | 0        | 35.50 | EUR  |
| 1        | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 2          | 35455      | 1        | 25.45 | EUR  |
| 1        | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 3          | 35455      | 1        | 30.50 | EUR  |
| 1        | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 4          | 35455      | 1        | 38.95 | EUR  |

## Table Fields

* **BRAND_ID**: Foreign key identifying the brand (in this example, `1 = ZARA`).
* **START_DATE**: Start date when the price rule becomes valid.
* **END_DATE**: End date when the price rule expires.
* **PRICE_LIST**: Identifier of the price rule.
* **PRODUCT_ID**: Product identifier.
* **PRIORITY**: Determines which rule applies when multiple rules overlap.
* **PRICE**: Final price to apply.
* **CURR**: Currency ISO code.

---

# Functional Requirements

The application exposes a REST endpoint that receives:

* **Application Date**
* **Product ID**
* **Brand ID**

And returns:

* Product ID
* Brand ID
* Applicable price list
* Start date of the rule
* End date of the rule
* Final price

If multiple pricing rules match the request, the rule with the **highest priority** is selected.

---

# Test Scenarios

The following test scenarios were provided as part of the challenge:

1. Request at **2020-06-14 10:00** for product **35455**, brand **ZARA**
2. Request at **2020-06-14 16:00**
3. Request at **2020-06-14 21:00**
4. Request at **2020-06-15 10:00**
5. Request at **2020-06-16 21:00**

The service returns the correct price rule based on date ranges and priority.

---

# Architecture

The application follows a **clean layered architecture inspired by Hexagonal Architecture**.

## Domain Layer

Contains core domain objects and business rules.

Key elements:

* `Price` – Domain entity representing a pricing rule.

The domain layer is independent of frameworks and infrastructure.

---

## Application Layer

Defines application use cases and ports.

Key components:

* `PriceServicePort` – Defines business operations related to pricing.
* `PriceRepositoryPort` – Defines the persistence contract used by the application layer.
* `PriceServiceUseCase` – Implements business logic to retrieve the applicable price.

This layer orchestrates domain logic and communicates with infrastructure through ports.

---

## Driving Layer

Handles incoming HTTP requests.

Components include:

* `PriceControllerAdapter`

Responsibilities:

* Receives REST requests
* Validates input parameters
* Delegates to the application use case
* Returns the response DTO

---

## Driven Layer

Handles infrastructure concerns such as persistence, caching, and resilience.

Components include:

* `PriceRepositoryAdapter`
  Adapter implementing the `PriceRepositoryPort` defined in the application layer.

* `PriceRepositoryResilientAdapter`
  Component responsible for applying resilience patterns such as retry, caching, and fallback when accessing the database.

* `PriceMORepository`
  Spring Data JPA repository that interacts with the H2 database.

This separation ensures resilience and infrastructure concerns remain isolated from the application and domain layers.

---

# Persistence

The application uses:

* **Spring Data JPA**
* **H2 in-memory database**

The database is automatically initialized with Flyway migrations during application startup.

---

# Resilience and Caching

To improve reliability and performance, the repository layer integrates **Resilience4j** and **Caffeine caching**.

## Retry

The repository infrastructure applies **Resilience4j Retry** to automatically retry database operations when transient failures occur.

## Fallback

If all retry attempts fail, a **fallback mechanism** throws a `FallbackPriceException`, allowing the application to return a controlled failure instead of propagating low-level exceptions.

## Caching

Caffeine caching is used to store repository results, reducing repeated database queries for identical requests and improving response times.

All resilience and caching logic is encapsulated in the infrastructure layer, keeping the domain and application layers clean.

---

# API Endpoint

GET /api/v1/prices/applicable

## Request Parameters

| Parameter       | Description                           |
| --------------- | ------------------------------------- |
| applicationDate | Date when the price should be applied |
| productId       | Product identifier                    |
| brandId         | Brand identifier                      |

## Response Example

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45
}
```

---

# Testing

The project includes both **unit tests** and **integration tests**.

## Unit Tests

Unit tests validate individual components such as:

* Controllers
* Use cases
* Repository adapters

Mocks are used to isolate dependencies.

## Integration Tests

Integration tests verify:

* Database access
* Retry behavior
* Caching functionality
* End-to-end interaction between layers

---

# Running the Application

## 1. Clone the repository

git clone <repository-url>

## 2. Build the project

./mvnw clean install

## 3. Run the application

./mvnw spring-boot:run

---

# H2 Database Console

The H2 console is available at:

http://localhost:8080/h2-console

Configuration:

JDBC URL: jdbc:h2:mem:testdb
User: sa
Password: (empty)

---

# Running Tests

Execute all tests with:

./mvnw test

---

# Expected Behavior

The service returns the correct price rule according to:

* the application date
* the product ID
* the brand ID
* the priority of overlapping pricing rules

The resilience and caching mechanisms improve stability and performance when interacting with the persistence layer.
