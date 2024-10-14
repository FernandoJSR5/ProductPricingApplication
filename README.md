# Product Pricing Application

## Description

This application is a solution that involves building a REST service in Spring Boot. The application simulates an e-commerce database that manages product prices based on different rate plans and priorities. The service exposes an endpoint that allows querying the final applicable price for a product based on the query date, product ID, and brand ID.

## PRICES Table

The database includes a `PRICES` table, which stores information about the applicable rates for products during specific periods. Below is an example of the table's structure:

| BRAND_ID | START_DATE          | END_DATE            | PRICE_LIST | PRODUCT_ID | PRIORITY | PRICE | CURR |
|----------|---------------------|---------------------|------------|------------|----------|-------|------|
| 1        | 2020-06-14 00:00:00 | 2020-12-31 23:59:59 | 1          | 35455      | 0        | 35.50 | EUR  |
| 1        | 2020-06-14 15:00:00 | 2020-06-14 18:30:00 | 2          | 35455      | 1        | 25.45 | EUR  |
| 1        | 2020-06-15 00:00:00 | 2020-06-15 11:00:00 | 3          | 35455      | 1        | 30.50 | EUR  |
| 1        | 2020-06-15 16:00:00 | 2020-12-31 23:59:59 | 4          | 35455      | 1        | 38.95 | EUR  |

### Table Fields

- **BRAND_ID**: Foreign key identifying the brand (in this case, `1 = ZARA`).
- **START_DATE**: The start date from which the rate applies.
- **END_DATE**: The end date until which the rate applies.
- **PRICE_LIST**: Unique identifier of the applicable rate.
- **PRODUCT_ID**: Unique identifier of the product.
- **PRIORITY**: Priority indicator to resolve conflicts when multiple rates apply. The rate with the highest priority is applied.
- **PRICE**: Final sale price of the product.
- **CURR**: ISO code of the currency in which the price is expressed.

## Test Requirements

The task was to build a **Spring Boot** service that exposes a REST endpoint to query applicable prices based on the following input parameters:

- **Application Date**: `applicationDate`
- **Product ID**: `productId`
- **Brand ID**: `brandId`

The service should return:

- Product ID (`productId`)
- Brand ID (`brandId`)
- Applicable rate (`priceList`)
- Application dates (`startDate` and `endDate`)
- Final applicable price (`price`)

Additionally, the following test cases were provided to validate the service:

1. **Test 1**: Request at 10:00 on June 14th for product `35455` with brand `ZARA`.
2. **Test 2**: Request at 16:00 on June 14th for product `35455` with brand `ZARA`.
3. **Test 3**: Request at 21:00 on June 14th for product `35455` with brand `ZARA`.
4. **Test 4**: Request at 10:00 on June 15th for product `35455` with brand `ZARA`.
5. **Test 5**: Request at 21:00 on June 16th for product `35455` with brand `ZARA`.

## Implemented Solution

### Project Structure

The project is organized into the following layers:

- **Domain**: Contains domain entities and core business logic. Key classes include `Price` entity.
- **Application**: Defines application services and interfaces. This layer includes:
   - `PriceServicePort`: An interface for business operations related to pricing.
   - `PriceRepositoryPort`: An interface for data access, implemented in the `driven` layer.
   - `PriceServiceUseCase`:  Implements the `PriceServicePort` interface to provide the logic for retrieving applicable prices based on user requests
- **Driving**: Handles incoming requests and responses. This layer includes REST controllers like `PriceControllerAdapter` that process HTTP requests and return responses.
- **Driven**: Interacts with external systems, including data access and repository implementations. This layer includes:
   - `PriceMORepository`: A repository implementation that interacts with the H2 in-memory database.

### In-Memory Database

An in-memory H2 database was used to store the provided example data. The database is automatically initialized when the application starts.

### Endpoint

The service exposes a REST endpoint at `/api/v1/prices/applicable` that accepts the aforementioned input parameters and returns the applicable price based on defined rules.

### Resilience and Caching Improvements

- **Resilience Enhancements**: Integrated Resilience4j to implement CircuitBreaker and Retry patterns for improved fault tolerance and reliability.
- **Caching**: Added CaffeineCacheManager to cache results, reducing the load on the database and improving response times.
- **Fallback Logic**: Implemented sophisticated fallback mechanisms to handle exceptions gracefully, including differentiating between PriceException and other runtime exceptions.

### API Documentation

Updated the API mustache template to enhance documentation and clarity for users.

### Tests

Unit Tests: Unit tests were implemented for the controller and service, verifying correct behavior in various scenarios.
Integration Tests: Integration tests were implemented to ensure all components work together correctly, including tests for resilience and caching logic.

### Running the Application

To run the application:

1. Clone the repository:
2. Build the Project:
    ```bash
   ./mvnw clean install
3. Run the project using your preferred IDE or by executing the following command:
   ```bash
   ./mvnw spring-boot:run
4. Accessing the H2 Database Console:
   ```bash
   http://localhost:8080/h2-console
5. Tests can be executed with:
   ```bash
   ./mvnw test

### Expected Results

For each test case, the service should return the correct price, taking into account the priority of the rates and the applicable date range. The improvements in resilience and caching should ensure the service handles high loads efficiently while maintaining responsiveness.
