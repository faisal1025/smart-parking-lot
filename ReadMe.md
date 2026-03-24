# SmartParkingLot — Parking Lot LLD and API (Spring Boot + Spring Data JPA)

This repository contains a Low-Level Design (LLD) implementation and REST API for a Parking Lot system built with Spring Boot and Spring Data JPA. The code demonstrates data modeling and schema design, layered architecture (controllers/services/repositories), DTO mapping, validation, exception handling, and integration testing.

This README summarizes the main concepts and functions you learned while implementing the project and documents the APIs and how to run and test the application.

---

## Table of Contents

- Project overview
- Key concepts & patterns learned
- Architecture & package structure
- Domain model & schema design
- CRUD APIs (ParkingLot, ParkingFloor, ParkingSlot)
- DTOs, Controllers, Services, Repositories
- Transactions, concurrency & occupancy logic
- Testing strategy (unit + integration)
- Running the application and tests
- Troubleshooting & common errors
- Notes and next steps

---

## Project overview

This project models a parking lot consisting of:
- ParkingLot: top-level container (name, address, floors)
- ParkingFloor: floor within a parking lot (name, level, slots)
- ParkingSlot: individual parking slot (type, occupied status, identifiers)

Main features implemented:
- CRUD operations for ParkingLot, ParkingFloor and ParkingSlot
- REST API endpoints (Spring Web)
- Persistence with Spring Data JPA and Hibernate
- DTO-based request/response mapping
- Integration tests for controllers
- H2 in-memory DB for tests / local development

---

## Key concepts & patterns learned

- Spring Boot application structure and boot lifecycle
- Spring MVC and REST controllers (`@RestController`, routing, status codes)
- Dependency injection: constructor injection (Spring will auto-wire a single constructor without `@Autowired`)
- Spring Data JPA repositories (`JpaRepository`) for CRUD
- JPA entity relationships: `@OneToMany`, `@ManyToOne`, owning side, cascade rules, `fetch` strategies
- Schema design and normalization to model hierarchical data (lot -> floors -> slots)
- DTOs for API boundary (decouple entity from API contract)
- Service layer for business logic, transactions, and validations
- Exception handling with `@ControllerAdvice` and custom exceptions for meaningful HTTP responses
- Integration testing with Spring Boot Test (`@SpringBootTest` + `@AutoConfigureMockMvc`), including H2 DB
- Writing tests that verify state transitions (e.g., setting slot occupied/unoccupied when issuing/closing ticket)
- Basic concurrency considerations (optimistic locking or transactional semantics can be added later)

---

## Architecture & package structure (high-level)

- `org.airtribe` (root package)
  - `controllers` — REST controllers for ParkingLot, ParkingFloor, ParkingSlot, Ticket endpoints
  - `services` — business logic and transaction boundaries
  - `repository` — Spring Data JPA repositories
  - `entities` — JPA entity classes and relationships
  - `dto` — request/response DTOs and mapping helpers
  - `exception` — custom exceptions and global exception handler
  - `factory` / `strategy` — supporting patterns used for ticket generation / pricing (if present)

This separation enforces single responsibility and makes controllers thin while services hold core logic.

---

## Domain model & schema design

Primary entities and relationships:

- ParkingLot (id, name, address, List<ParkingFloor>)
  - One-to-Many relationship to `ParkingFloor`
  - Cascade type depends on whether you want floors removed with the lot; typically `CascadeType.ALL` for full ownership.

- ParkingFloor (id, name, level, parkingLot, List<ParkingSlot>)
  - Many-to-One to `ParkingLot`
  - One-to-Many to `ParkingSlot`

- ParkingSlot (id, code, type, occupied, parkingFloor)
  - Many-to-One to `ParkingFloor`
  - Fields: `occupied` boolean, `slotType` enum (e.g., COMPACT, LARGE, HANDICAPPED), `identifier` or `code`

Important schema decisions:
- Use surrogate primary keys (Long `id`) for simplicity.
- Model ownership so deleting a floor may cascade to slots (choose cascade appropriately).
- Consider constraints: unique slot code per floor, non-nullable `occupied` default `false`.

---

## API: CRUD endpoints (summary)

This project exposes RESTful CRUD endpoints for the three core resources. Example routes (conventions used in this project):

ParkingLot
- GET /api/parking-lots — list all parking lots
- GET /api/parking-lots/{id} — get a parking lot
- POST /api/parking-lots — create a parking lot
- PUT /api/parking-lots/{id} — update a parking lot
- DELETE /api/parking-lots/{id} — delete a parking lot

ParkingFloor
- GET /api/parking-floors — list all floors
- GET /api/parking-floors/{id} — get a floor
- POST /api/parking-lots/{lotId}/parking-floors — create a floor inside a lot
- PUT /api/parking-floors/{id} — update a floor
- DELETE /api/parking-floors/{id} — delete a floor

ParkingSlot
- GET /api/parking-slots — list slots (query params for floorId or type)
- GET /api/parking-slots/{id} — get slot by id
- POST /api/parking-floors/{floorId}/parking-slots — create a slot on a floor
- PUT /api/parking-slots/{id} — update a slot (e.g., mark occupied/unoccupied)
- DELETE /api/parking-slots/{id} — delete a slot

Notes:
- The service layer should validate that parent entities exist when creating children (e.g., ensure `ParkingFloor` belongs to an existing `ParkingLot`).
- For ticketing flows, marking a `ParkingSlot.occupied` to `true` on entry and `false` on exit is required.

---

## DTOs, Controllers, Services, Repositories

- DTOs: Accept incoming JSON via request DTOs and return response DTOs. Map to/from entities in services or using mapper utilities.
  - Example: `ParkingSlotRequestDto { String code; String slotType; }` and `ParkingSlotResponseDto { Long id; String code; boolean occupied; }`.

- Controllers (`@RestController`): Keep controllers thin — map requests to DTOs, call service methods, and return appropriate HTTP responses.
  - Use `@Valid` for request validation and `@ResponseStatus` for explicit status codes.

- Services (`@Service`): Contain business operations and transaction boundaries. Use constructor injection:
  - e.g., `public class ParkingLotService { private final ParkingLotRepository repo; public ParkingLotService(ParkingLotRepository repo) { this.repo = repo; } }`
  - Spring auto-injects the single constructor; you don't need `@Autowired` (Spring 4.3+).

- Repositories (`@Repository` + extends `JpaRepository<Entity, Long>`): Provide CRUD and query methods (e.g., findByFloorIdAndOccupiedFalse).

---

## Transactions, concurrency & occupancy logic

- Mark service methods that mutate multiple entities as `@Transactional` to ensure ACID semantics.
- When allocating a slot for entry, ensure you find and update an available slot atomically.
  - Strategies: Pessimistic locking (SELECT ... FOR UPDATE) or optimistic locking (`@Version`), depending on load.
- Example exit flow: retrieve ticket, set `ticket.getSlot().setOccupied(false)`, and save the slot and ticket state within the same transaction.

---

## Testing strategy

- Unit tests for services and repositories where applicable.
- Integration tests for controllers using `@SpringBootTest` or `@WebMvcTest` + `MockMvc`.
  - Tests cover happy paths and edge cases: creating entities, validation failures (400), not found (404), and state changes such as freeing a slot on exit.
- Example integration test to check exit flow:
  - Create a lot, floor, slot, create a ticket that occupies the slot, then call exit endpoint and assert `ticket.getSlot().isOccupied() == false` after the flow completes.

---

## Running the application and tests

Prerequisites: Java 17+ (or the JDK configured for the project), Gradle wrapper is included.

From Windows `cmd.exe` in repo root:

- Build and run tests:

    gradlew.bat test

- Run the application locally:

    gradlew.bat bootRun

- Or build a runnable jar:

    gradlew.bat clean bootJar
    java -jar build/libs/SmartParkingLot-1.0-SMARTPARKINGLOT.jar

The application runs on port 8080 by default. You can configure properties in `src/main/resources/application.properties`.

---

## H2 console & troubleshooting

- To enable H2 console, ensure your `application.properties` contains:

    spring.h2.console.enabled=true
    spring.h2.console.path=/h2-console

- If you see a 404 when visiting `http://localhost:8080/h2-console`:
  - Verify the application is running and bound to port 8080.
  - Confirm `spring.h2.console.enabled=true` in the active profile's properties.
  - Ensure no web security configuration blocks the `/h2-console` path (Spring Security may block it by default).

- Hibernate dialect issues (example error: `Unable to load class [org.hibernate.dialect.MySQL8Dialect]`):
  - This usually means the version of Hibernate in the classpath differs from the dialect class name you're using. Fixes:
    - Use a dialect class appropriate to your Hibernate version (e.g., `org.hibernate.dialect.MySQL8Dialect` is available in modern Hibernate versions). If using an older Hibernate, try `org.hibernate.dialect.MySQLDialect`.
    - Ensure the Hibernate/JPA dependencies are present and compatible (check `build.gradle` dependencies).

---

## Common HTTP status responses you saw and why

- 400 Bad Request: Usually caused by failing validation on request DTOs (`@Valid`) or malformed JSON. Check controller method signatures and validation annotations.
- 404 Not Found: When a resource or endpoint is missing (e.g., trying to hit `/h2-console` when disabled or resource id not present).
- 500 / non-zero exit from Gradle JavaExec: The application threw an exception on startup; check logs for root cause.

---

## Git / repo hygiene

- A `.gitignore` has been added/updated to exclude build artifacts, IDE files, DB files and secrets. If you already have secrets or generated files tracked, untrack them locally:

    git rm -r --cached .
    git add .
    git commit -m "Apply updated .gitignore"

- If secrets were committed historically, consider removing them from history with BFG or `git filter-branch` (requires coordination for force-pushes).

---

## Next steps / suggestions

- Add explicit DTO mappers (MapStruct or manual mappers) to keep mapping consistent.
- Add optimistic locking with `@Version` to `ParkingSlot` to handle concurrent allocations.
- Add pagination, filters, and health endpoints.
- Harden security for H2 console if using Spring Security (limit to dev profile).
- Add more integration tests for concurrency scenarios and failure modes.

---

If you want, I can:
- Generate an API Postman collection for the CRUD endpoints,
- Add example cURL requests to this README,
- Create or improve integration tests to validate the `ticket.getSlot().setOccupied(false)` exit flow and validate H2 console configuration.

Tell me which of those you'd like me to do next and I'll implement it.
