# E-Commerce Challenge API

A complete RESTful API for an e-commerce platform, built with Spring Boot. This project implements core features such as product management, a shopping cart, order processing, user authentication, and advanced administrative reporting.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Core Features](#core-features)
- [Environment Setup](#environment-setup)
  - [Prerequisites](#prerequisites)
  - [Step-by-Step Guide](#step-by-step-guide)
- [API Documentation](#api-documentation)
  - [Authentication](#authentication)
  - [Users](#users)
  - [Products](#products)
  - [Shopping Cart](#shopping-cart)
  - [Orders](#orders)
  - [Reports](#reports)
- [Configuration Properties](#configuration-properties)

---

## Tech Stack

This API is built using the following technologies and libraries:

-   **Java 21**
-   **Spring Boot 3.4.5**
-   **Spring Security:** For JWT-based authentication and authorization.
-   **Spring Data JPA (Hibernate):** For data persistence.
-   **PostgreSQL:** Relational database.
-   **Maven:** Dependency management.
-   **Lombok:** To reduce boilerplate code.
-   **ModelMapper:** For object mapping (DTOs).

---

## Core Features

-   **Authentication & Authorization:** A complete system using JWT, with `ADMIN` and `CLIENT` roles for secure access control.
-   **Password Reset:** A secure, email-based flow using single-use tokens.
-   **Product Management:** Full CRUD capabilities for products, including activation/deactivation status.
-   **Inventory Management:** Real-time stock validation and automatic inventory updates upon purchase completion.
-   **Shopping Cart:** Functionality for adding, updating, and removing items.
-   **Order Lifecycle:** Full order processing, including checkout, querying, and status updates (`PENDING`, `SHIPPED`, `DELIVERED`, `CANCELED`).
-   **Advanced Reporting:** Endpoints for administrators to query sales and profit data (grouped by day, week, or month), low-stock products, best-selling products, and top clients.
-   **Standardization & Validation:** Global exception handling for unified error responses, pagination on all list endpoints, and adherence to the ISO 8601 date format.

---

## Environment Setup

Follow these steps to configure and run the project locally.

### Prerequisites

-   **JDK 21** or higher.
-   **Maven 3.8** or higher.
-   **PostgreSQL** installed and running.
-   An API client tool, such as **Postman** or **Insomnia**.

### Step-by-Step Guide

1.  **Clone the Repository**
    ```bash
    git clone <your-repository-url>
    cd e-commerce_challenge
    ```

2.  **Create the Database**
    -   Open your PostgreSQL client (e.g., `psql` or DBeaver).
    -   Create a new database. By default, the application is configured to use the name `db_e-commerce`.
        ```sql
        CREATE DATABASE "db_e-commerce";
        ```

3.  **Configure Environment Properties**
    -   Navigate to the `src/main/resources/application.properties` file.
    -   Modify the following properties to match your local PostgreSQL and email service configuration:
        ```properties
        # Database Configuration
        spring.datasource.url=jdbc:postgresql://localhost:5432/db_e-commerce
        spring.datasource.username=your_postgres_user
        spring.datasource.password=your_postgres_password

        # Email Service Configuration (for password reset)
        spring.mail.username=your_email@gmail.com
        spring.mail.password=your_gmail_app_password
        ```
    -   For more details on other properties, see the [Configuration Properties](#configuration-properties) section.

4.  **Run the Application**
    -   Use Maven to compile and run the project:
        ```bash
        mvn spring-boot:run
        ```
    -   The API will be available at `http://localhost:8080`.

---

## API Documentation

The following is a list of available endpoints, organized by resource.

**Note:** Endpoints marked with `(ADMIN)` require a token from a user with an administrator profile. Endpoints marked with `(CLIENT)` require a token from any authenticated user.

### Authentication

| Method | Endpoint                    | Description                                         |
| :----- | :-------------------------- | :-------------------------------------------------- |
| `POST` | `/auth/register`            | Registers a new user (client).                      |
| `POST` | `/auth/register-admin`      | **(ADMIN)** Registers a new administrator.          |
| `POST` | `/auth/login`               | Authenticates a user and returns a JWT.             |
| `POST` | `/auth/forgot-password`     | Initiates the password reset process.               |
| `POST` | `/auth/reset-password`      | Finalizes the password reset with a valid token.    |

### Users

| Method   | Endpoint     | Description                                      |
| :------- | :----------- | :----------------------------------------------- |
| `GET`    | `/users/me`  | **(CLIENT)** Returns the logged-in user's profile. |
| `PUT`    | `/users/me`  | **(CLIENT)** Updates the logged-in user's profile. |
| `GET`    | `/users`     | **(ADMIN)** Lists all users (paginated).          |
| `DELETE` | `/users/{id}`| **(ADMIN)** Deletes a user.                         |

### Products

| Method  | Endpoint                   | Description                                      |
| :------ | :------------------------- | :----------------------------------------------- |
| `GET`   | `/products`                | Lists all active products (paginated).           |
| `GET`   | `/products/all`            | **(ADMIN)** Lists all products, including inactive ones (paginated). |
| `GET`   | `/products/{id}`           | Returns the details of a specific product.       |
| `POST`  | `/products`                | **(ADMIN)** Creates a new product.               |
| `PUT`   | `/products/{id}`           | **(ADMIN)** Updates an existing product.         |
| `PATCH` | `/products/{id}/deactivate`| **(ADMIN)** Deactivates a product.               |
| `DELETE`| `/products/{id}`           | **(ADMIN)** Deletes a product (if not tied to an order). |

### Shopping Cart

| Method   | Endpoint          | Description                                      |
| :------- | :---------------- | :----------------------------------------------- |
| `GET`    | `/cart`           | **(CLIENT)** Returns the logged-in user's cart.  |
| `POST`   | `/cart/items`     | **(CLIENT)** Adds an item to the cart.           |
| `PUT`    | `/cart/items/{id}`| **(CLIENT)** Updates the quantity of an item.     |
| `DELETE` | `/cart/items/{id}`| **(CLIENT)** Removes an item from the cart.        |

### Orders

| Method  | Endpoint            | Description                                      |
| :------ | :------------------ | :----------------------------------------------- |
| `POST`  | `/orders/checkout`  | **(CLIENT)** Finalizes the purchase from the cart. |
| `GET`   | `/orders`           | **(CLIENT)** Lists the logged-in user's orders (paginated). An admin can use `?all=true` to view all orders. |
| `GET`   | `/orders/{id}`      | **(CLIENT)** Returns the details of a specific order. |
| `PATCH` | `/orders/{id}/status`| **(ADMIN)** Updates the status of an order.        |

### Reports

| Method | Endpoint            | Description                                      |
| :----- | :------------------ | :----------------------------------------------- |
| `GET`  | `/reports/sales`    | **(ADMIN)** Returns a sales and profit summary, grouped by `DAY`, `WEEK`, or `MONTH`. |
| `GET`  | `/reports/low-stock`| **(ADMIN)** Lists products with low inventory (paginated). |
| `GET`  | `/reports/top-products`| **(ADMIN)** Lists the best-selling products in a given period (paginated). |
| `GET`  | `/reports/top-clients`| **(ADMIN)** Lists the top-spending clients in a given period (paginated). |

---

## Configuration Properties

The `application.properties` file contains important variables that can be configured.

-   `spring.datasource.*`: Settings for connecting to the PostgreSQL database.
-   `spring.mail.*`: SMTP server settings for sending emails. This requires an "app password" if using Gmail with 2FA enabled.
-   `jwt.secret`, `jwt.expiration`: The secret key and expiration time for JWTs. The secret should be a 256-bit Base64 encoded string.
-   `app.admin.*`: Credentials for the initial admin user, which is created automatically on application startup.
-   `app.report.low-stock-threshold`: The inventory threshold for a product to be considered "low stock" in the corresponding report.