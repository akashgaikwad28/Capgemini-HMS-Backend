# Capgemini HMS Backend

A **Spring Boot Monolith** for the **Capgemini Training Evaluation Exceller 2026** sprint project.

This project is a **Hospital Management System (HMS)** designed to manage hospital operations such as patient records, physician details, appointments, prescriptions, room stays, nurse assignments, procedures, and department affiliations using a structured relational database.

---

## Project Overview

The goal of this application is to provide a clean, maintainable, and scalable backend for hospital workflows in a **single Spring Boot monolith**. The system is designed around the provided database schema and follows a layered architecture with clear separation of concerns.

The application will support:

* Patient management
* Physician and department management
* Appointment scheduling
* Medication and prescription tracking
* Room and stay management
* Procedure and treatment records
* Nurse and on-call tracking
* Secure API design with future-ready structure

---

## Why Monolith Architecture

This project is intentionally designed as a **monolith** because:

* It is easier to build and deliver within the evaluation timeline
* It reduces deployment and debugging complexity
* It is ideal for a 14-day sprint project
* It keeps all modules in one codebase while still maintaining modular package structure

The codebase is organized as a **modular monolith**, meaning each domain has its own package and responsibilities, but everything is deployed as one application.

---

## Database-Driven Domain Model

The project is based on the following major database entities from the shared HMS schema:

* `Physician`
* `Department`
* `Affiliated_With`
* `Procedures`
* `Trained_In`
* `Patient`
* `Nurse`
* `Appointment`
* `Medication`
* `Prescribes`
* `Block`
* `Room`
* `On_Call`
* `Stay`
* `Undergoes`

These tables represent the core hospital workflow and will be mapped into Spring Boot entities, repositories, services, DTOs, and controllers.

---

## Suggested Folder Structure

Below is the recommended **end-to-end professional folder structure** for the monolith.

```bash
capgemini-hms-backend/
├── README.md
├── pom.xml
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── capgemini
│   │   │           └── hms
│   │   │               ├── CapgeminiHmsBackendApplication.java
│   │   │               │
│   │   │               ├── config
│   │   │               │   ├── SwaggerConfig.java
│   │   │               │   ├── JpaConfig.java
│   │   │               │   └── WebConfig.java
│   │   │               │
│   │   │               ├── security
│   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │               │   ├── JwtService.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   └── CustomUserDetailsService.java
│   │   │               │
│   │   │               ├── exception
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   ├── DuplicateResourceException.java
│   │   │               │   └── BadRequestException.java
│   │   │               │
│   │   │               ├── common
│   │   │               │   ├── constants
│   │   │               │   ├── enums
│   │   │               │   ├── response
│   │   │               │   └── util
│   │   │               │
│   │   │               ├── auth
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── model
│   │   │               │
│   │   │               ├── physician
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── department
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── patient
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── nurse
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── appointment
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── medication
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── procedure
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── room
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── stay
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               ├── prescription
│   │   │               │   ├── controller
│   │   │               │   ├── dto
│   │   │               │   ├── entity
│   │   │               │   ├── repository
│   │   │               │   ├── service
│   │   │               │   ├── serviceImpl
│   │   │               │   └── mapper
│   │   │               │
│   │   │               └── oncall
│   │   │                   ├── controller
│   │   │                   ├── dto
│   │   │                   ├── entity
│   │   │                   ├── repository
│   │   │                   ├── service
│   │   │                   ├── serviceImpl
│   │   │                   └── mapper
│   │   │
│   │   └── resources
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── db
│   │       │   ├── schema.sql
│   │       │   └── data.sql
│   │       ├── static
│   │       └── templates
│   │
│   └── test
│       └── java
│           └── com
│               └── capgemini
│                   └── hms
│                       ├── physician
│                       ├── patient
│                       ├── appointment
│                       └── ...
```

---

## Package Responsibility

### `config`

Contains framework-level configuration like Swagger, JPA, CORS, and application setup.

### `security`

Contains JWT-based authentication and authorization setup.

### `exception`

Contains custom exceptions and a global exception handler for clean API responses.

### `common`

Contains shared utilities, constants, enums, and response wrappers used across all modules.

### Domain Packages

Each domain package represents one business area of the HMS and follows the same internal structure:

* `controller` → REST endpoints
* `dto` → request/response objects
* `entity` → JPA entity classes
* `repository` → database access layer
* `service` → business interface
* `serviceImpl` → business logic implementation
* `mapper` → DTO/entity conversion

---

## Domain Mapping Based on Schema

### 1. Physician Module

Handles:

* Physician registration and management
* Doctor profiles
* Specialization and position tracking
* Head of department references

Maps to:

* `Physician`
* `Affiliated_With`
* `Trained_In`

### 2. Department Module

Handles:

* Department creation
* Department head assignment
* Department-wise physician mapping

Maps to:

* `Department`
* `Affiliated_With`

### 3. Patient Module

Handles:

* Patient registration
* Personal details
* PCP assignment

Maps to:

* `Patient`

### 4. Nurse Module

Handles:

* Nurse records
* Registration status
* Nurse workforce management

Maps to:

* `Nurse`
* `On_Call`

### 5. Appointment Module

Handles:

* Patient appointments
* Prep nurse assignment
* Physician assignment
* Appointment timing and room details

Maps to:

* `Appointment`

### 6. Medication Module

Handles:

* Medicine catalog
* Brand and description details
* Prescription linkage

Maps to:

* `Medication`

### 7. Prescription Module

Handles:

* Which physician prescribed which medication
* Patient-medication relation
* Dose and prescription date

Maps to:

* `Prescribes`

### 8. Procedure Module

Handles:

* Hospital procedures
* Procedure cost and name
* Training and treatment mapping

Maps to:

* `Procedures`
* `Trained_In`

### 9. Room Module

Handles:

* Room details
* Room type
* Availability
* Block mapping

Maps to:

* `Room`
* `Block`

### 10. Stay Module

Handles:

* Patient admission
* Room stay duration
* Admission and discharge tracking

Maps to:

* `Stay`

### 11. On-Call Module

Handles:

* Nurse on-call schedule
* Block assignment
* Start/end duty timings

Maps to:

* `On_Call`

### 12. Undergoes Module

Handles:

* Procedure performed during a patient stay
* Physician and assisting nurse details

Maps to:

* `Undergoes`

---

## Suggested API Layer Flow

```text
Controller → Service → Repository → Database
```

### Example Flow

1. Client sends request to controller
2. Controller validates and forwards to service
3. Service applies business logic
4. Repository handles database interaction
5. Response is returned in a standard format

---

## Standard Response Structure

All APIs should ideally return a consistent response format such as:

```json
{
  "success": true,
  "message": "Patient created successfully",
  "data": {}
}
```

This makes the backend cleaner and easier to consume by frontend or testing tools.

---

## Recommended Development Approach

### Phase 1: Base Setup

* Create Spring Boot project
* Configure database connection
* Add dependencies
* Setup Swagger and global exception handling

### Phase 2: Core Modules

* Patient
* Physician
* Department
* Appointment
* Nurse

### Phase 3: Supporting Modules

* Medication
* Prescription
* Room
* Stay
* Procedure
* On-call
* Undergoes

### Phase 4: Finalization

* Validation
* Logging
* Error handling
* Testing
* Deployment

---

## Team Collaboration Plan

Since this is a group project, use a clean GitHub workflow:

* `main` → stable final code
* `develop` → active integration branch
* `feature/<module-name>` → individual task branches

Example feature branches:

* `feature/patient-module`
* `feature/physician-module`
* `feature/appointment-module`
* `feature/room-module`
* `feature/prescription-module`

Each team member should work on one feature branch and create a Pull Request into `develop`.

---

## Branch Protection Recommendation

For the `main` branch:

* Require pull request before merge
* Require at least 1 approval
* Restrict direct pushes
* Optionally require passing checks before merge

This ensures no one commits directly to production-ready code.

---

## Tech Stack

* **Java 17+**
* **Spring Boot**
* **Spring Web**
* **Spring Data JPA**
* **Spring Security**
* **MySQL**
* **Lombok**
* **Maven**
* **Swagger / OpenAPI**
* **Docker** for deployment

---

## Suggested Naming Convention

To keep the project professional, use consistent naming rules:

* Package name: `com.capgemini.hms`
* Entity names: singular form
* Repository names: `EntityRepository`
* Service names: `EntityService`
* Controller names: `EntityController`
* DTO names: `EntityRequestDto`, `EntityResponseDto`

---

## Notes for the Team

* Keep the schema-driven development approach
* Follow the exact column names carefully while mapping entities
* Do not overcomplicate with microservices
* Build reusable code for validation and exception handling
* Keep APIs clean and easy to test in Postman or Swagger

---

## Project Goal

The final goal is to deliver a well-structured, production-style **Hospital Management System backend** that demonstrates:

* Good architecture
* Team collaboration
* Clean coding standards
* Real-world Spring Boot practices
* Strong database understanding

---

## License

This project is developed for **Capgemini Training Evaluation Exceller 2026** and is intended for academic and evaluation purposes.
