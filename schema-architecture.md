# Smart Clinic Management System - Architecture Design

## Section 1: Architecture Summary
This Spring Boot application follows a three-tier web architecture that combines server-side MVC rendering with scalable RESTful APIs. The presentation tier provides server-rendered HTML views via Spring MVC and Thymeleaf for internal interfaces (Admin Dashboard and Doctor Dashboard), while exposing JSON-based REST endpoints for modular features such as Appointments, Patient Dashboards, and Patient Records. 

All client requests pass through a common Service Layer containing business logic, transaction management, and validation workflows. The application leverages a dual-database persistence tier to accommodate diverse data models:
- **MySQL (Relational)**: Accessed via Spring Data JPA entities to manage structured records, including Patients, Doctors, Appointments, and Admin credentials.
- **MongoDB (Document-based)**: Accessed via Spring Data MongoDB document repositories to store dynamic, unstructured prescription records.

By decoupling controllers, services, and repositories, the architecture ensures modularity, high maintainability, and seamless containerization for modern CI/CD deployment pipelines.

## Section 2: Flow of Data and Control
1. **User Interface Interaction**: A user accesses the application through either a browser-based dashboard (Admin/Doctor views) or client-side/mobile modules (Appointments and Patient records).
2. **Controller Routing**: HTTP requests are mapped to their respective controllers: Thymeleaf Controllers handle requests for dynamic HTML templates, while REST Controllers receive requests expecting structured JSON responses.
3. **Service Layer Processing**: The controllers delegate operations to the Service Layer, which validates input parameters, coordinates multi-entity workflows, and executes core business rules.
4. **Repository Delegation**: The Service Layer interacts with the Repository Layer—directing relational operations to Spring Data JPA repositories and flexible document operations to Spring Data MongoDB repositories.
5. **Database Access**: JPA repositories communicate with the MySQL database engine for structured relational queries, while the MongoDB repository queries the MongoDB database for prescription documents.
6. **Model Binding**: Retrieved database records are converted into Java objects—relational records are mapped to `@Entity` classes and document records are loaded into `@Document` model objects.
7. **Application Models in Use**: The bound model objects or DTOs return up the chain: Thymeleaf controllers inject them into templates to render HTML to the browser, while REST controllers serialize them into JSON payloads returned to the client.
