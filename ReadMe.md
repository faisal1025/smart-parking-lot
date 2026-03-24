# SmartParkingLot — Parking Lot LLD and API (Spring Boot + Spring Data JPA)

Swagger documentation: http://localhost:8080/docs

This README lists the exact properties of the entities implemented in the codebase. Fields inherited from `AbstractBase` (present on every entity) are listed first.

---

Common inherited fields (from `AbstractBase`)
- id: Long (primary key, generated)
- createdAt: LocalDateTime (audited)
- updatedAt: LocalDateTime (audited)

---

Entities and exact properties (as implemented)

1) ParkingLot
- Inherited: id, createdAt, updatedAt
- parkingFloorList: List<ParkingFloor> (OneToMany, cascade = ALL)
- entries: List<EntryGate> (OneToMany, cascade = ALL)
- exits: List<ExitGate> (OneToMany, cascade = ALL)

Sample JSON (ParkingLot instance):
{
  "id": 1,
  "parkingFloorList": [],
  "entries": [],
  "exits": [],
  "createdAt": "2026-03-24T10:00:00",
  "updatedAt": "2026-03-24T10:00:00"
}

Note: There are no `name` or `address` fields on `ParkingLot` in the code; the entity contains the collections above.

2) ParkingFloor
- Inherited: id, createdAt, updatedAt
- parkingSlotList: List<ParkingSlot> (OneToMany, cascade = ALL)
- parkingLot: ParkingLot (ManyToOne, JsonIgnored)

Sample JSON (ParkingFloor instance):
{
  "id": 10,
  "parkingSlotList": [],
  "createdAt": "2026-03-24T10:05:00",
  "updatedAt": "2026-03-24T10:05:00"
}

3) ParkingSlot
- Inherited: id, createdAt, updatedAt
- slotType: VehicleType (Enum: BIKE, CAR, TRUCK) — stored as STRING
- occupied: boolean
- floorNo: ParkingFloor (ManyToOne, JoinColumn `floor_id`, JsonIgnored, NotNull)

Sample JSON (ParkingSlot instance):
{
  "id": 100,
  "slotType": "CAR",
  "occupied": false,
  "createdAt": "2026-03-24T10:06:00",
  "updatedAt": "2026-03-24T10:06:00"
}

4) Vehicle
- Inherited: id, createdAt, updatedAt
- vehicleType: VehicleType (Enum: BIKE, CAR, TRUCK) — stored as STRING
- vehicleNo: String (unique, not-null)

Sample JSON (Vehicle instance):
{
  "id": 200,
  "vehicleType": "CAR",
  "vehicleNo": "KA-01-AB-1234",
  "createdAt": "2026-03-24T11:00:00",
  "updatedAt": "2026-03-24T11:00:00"
}

5) Ticket
- Inherited: id, createdAt, updatedAt
- slot: ParkingSlot (ManyToOne, cascade = MERGE)
- vehicle: Vehicle (ManyToOne, cascade = PERSIST)
- entryTime: LocalDateTime
- exitTime: LocalDateTime (nullable)
- bill: Bill (OneToOne mappedBy = "ticket", cascade = ALL)
- isActive: boolean (default true)

Sample JSON (Ticket instance):
{
  "id": 500,
  "slot": { "id":100 },
  "vehicle": { "id":200 },
  "entryTime": "2026-03-24T11:30:00",
  "exitTime": null,
  "bill": null,
  "isActive": true,
  "createdAt": "2026-03-24T11:30:00",
  "updatedAt": "2026-03-24T11:30:00"
}

6) Bill
- Inherited: id, createdAt, updatedAt
- ticket: Ticket (OneToOne, JoinColumn `ticket_id`, JsonIgnored)
- cost: double

Sample JSON (Bill instance):
{
  "id": 900,
  "cost": 20.0,
  "createdAt": "2026-03-24T13:45:00",
  "updatedAt": "2026-03-24T13:45:00"
}

7) EntryGate
- Inherited: id, createdAt, updatedAt
- gateNo: Long
- gateName: String
- isActive: boolean (default true)
- parkingLot: ParkingLot (ManyToOne)

Sample JSON (EntryGate instance):
{
  "id": 300,
  "gateNo": 1,
  "gateName": "Main Entry",
  "isActive": true,
  "createdAt": "2026-03-24T09:00:00",
  "updatedAt": "2026-03-24T09:00:00"
}

8) ExitGate
- Inherited: id, createdAt, updatedAt
- gateNo: Long
- gateName: String
- isActive: boolean (default true)
- parkingLot: ParkingLot (ManyToOne)

Sample JSON (ExitGate instance):
{
  "id": 400,
  "gateNo": 1,
  "gateName": "Main Exit",
  "isActive": true,
  "createdAt": "2026-03-24T09:00:00",
  "updatedAt": "2026-03-24T09:00:00"
}

---

Endpoints (controllers)

ParkingLotController (`/parkingLots`)
- POST /parkingLots
  - Body: `ParkingLot` JSON (entity with collections)
  - Behavior: create a new `ParkingLot` entity. Returns 201 Created with the persisted `ParkingLot`.

- GET /parkingLots
  - Behavior: return all `ParkingLot` entities. Returns 200 OK with a list.

- GET /parkingLots/{id}
  - Behavior: return the `ParkingLot` with the given id. Returns 200 OK or 404 if not found.

- PUT /parkingLots/{id}
  - Body: `ParkingLot` JSON (updated fields)
  - Behavior: update the `ParkingLot` with the given id. Returns 200 OK with the updated entity.

- DELETE /parkingLots/{id}
  - Behavior: delete the `ParkingLot` with the given id. Returns 204 No Content.

ParkingFloorController (`/parkingFloors`)
- POST /parkingFloors?parkingLotId={parkingLotId}
  - Query param: `parkingLotId` (Long) — parent lot id
  - Body: `ParkingFloor` JSON (entity)
  - Behavior: create a `ParkingFloor` associated with the `ParkingLot` identified by `parkingLotId`. Returns 201 Created with the persisted `ParkingFloor`.

- GET /parkingFloors
  - Behavior: list all floors. Returns 200 OK with a list.

- GET /parkingFloors/{id}
  - Behavior: return a single floor by id. Returns 200 OK or 404 if not found.

- PUT /parkingFloors/{id}
  - Body: `ParkingFloor` JSON (updated fields)
  - Behavior: update the specified floor. Returns 200 OK with updated entity.

- DELETE /parkingFloors/{id}
  - Behavior: delete the specified floor. Returns 204 No Content.

ParkingSlotController (`/parkingSlots`)
- POST /parkingSlots?floorId={floorId}&slotType={VehicleType}&occupied={boolean}
  - Query params:
    - `floorId` (Long) — required: the parent floor id
    - `slotType` (VehicleType) — required: one of BIKE, CAR, TRUCK
    - `occupied` (boolean) — optional, default false
  - Behavior: create a `ParkingSlot` for the specified floor with given vehicle type and occupancy state. Returns 201 Created with the persisted `ParkingSlot`.

- GET /parkingSlots
  - Behavior: return all parking slots. Returns 200 OK with a list.

- GET /parkingSlots/{id}
  - Behavior: return parking slot by id. Returns 200 OK or 404 if not found.

- PATCH /parkingSlots/{id}?slotType={slotType}&occupied={occupied}
  - Query params (optional): `slotType` (String), `occupied` (Boolean)
  - Behavior: update slotType and/or occupied state for the given slot id. Returns 200 OK with updated `ParkingSlot`.

- DELETE /parkingSlots/{id}
  - Behavior: delete the specified slot. Returns 204 No Content.

EntryController (`/entry`)
- POST /entry?strategy={strategy}
  - Query param: `strategy` (String) — allocation strategy; default `NEAREST`. Supported values in code: `NEAREST`, `FIRST_AVAILABLE`.
  - Body: `VehicleDto` JSON:
    {
      "vehicleType": "CAR",
      "vehicleNo": "KA-01-AB-1234"
    }
  - Behavior: generates a `Ticket` for the incoming vehicle using the requested allocation strategy. The ticket generation uses DB locking to ensure thread-safe allocation. Returns 201 Created with the `Ticket` entity (includes reference to allocated `slot` and `vehicle`).

ExitController (`/exit`)
- POST /exit?strategy={strategy}
  - Query param: `strategy` (String) — billing strategy; default `HOURLY`.
  - Body: `VehicleDto` JSON (same as entry)
  - Behavior: calculates cost and generates a `Bill` for the vehicle's active ticket using the chosen billing strategy (e.g., `HOURLY` maps to `HourlyPriceCalculation`). The service closes the ticket, persists the `Bill`, and frees the associated `ParkingSlot` (sets `occupied = false`). Returns 202 Accepted with the `Bill` entity.

HomeController
- (If present) the `HomeController` exposes application-level endpoints (e.g., root) — check the controller for specifics. If not used for API flows, it typically returns basic info or health check.

---

Notes
- All controllers use constructor or field injection as implemented in code (`ParkingLotController`, `ParkingFloorController`, `ParkingSlotController` use constructor injection; `EntryController` and `ExitController` use `@Autowired` field injection for `TicketService`).
- Validation and exceptions: controllers rely on service-layer validations and throw appropriate exceptions (e.g., `NoSlotFoundException`) which should be handled by global exception handlers (see `org.airtribe.exception`).
- For exact JSON shapes, example payloads in the earlier sections reflect the entity fields as persisted by the controllers.

---

Endpoint details (expanded)

This section expands the short descriptions above with explicit parameter details, possible responses (success and error), and example cURL requests you can run from a Windows `cmd.exe` shell (using `curl`).

1) ParkingLotController (`/parkingLots`)
- POST /parkingLots
  - Body: full `ParkingLot` JSON (collections may be empty on create)
  - Success: 201 Created + persisted `ParkingLot` JSON
  - Errors: 400 Bad Request for invalid JSON or validation failures
  - Example:
    curl -X POST "http://localhost:8080/parkingLots" -H "Content-Type: application/json" -d "{\"parkingFloorList\":[],\"entries\":[],\"exits\":[]}"

- GET /parkingLots
  - Success: 200 OK + list of `ParkingLot`

- GET /parkingLots/{id}
  - Success: 200 OK + `ParkingLot` JSON
  - Errors: 404 Not Found if id not present (handled by `EntityNotFoundException` mapping)

- PUT /parkingLots/{id}
  - Body: `ParkingLot` JSON with updated fields
  - Success: 200 OK + updated `ParkingLot`
  - Errors: 404 Not Found / 400 Bad Request

- DELETE /parkingLots/{id}
  - Success: 204 No Content
  - Errors: 404 Not Found

2) ParkingFloorController (`/parkingFloors`)
- POST /parkingFloors?parkingLotId={parkingLotId}
  - Query: `parkingLotId` required
  - Body: `ParkingFloor` JSON
  - Success: 201 Created + persisted `ParkingFloor`
  - Errors: 404 Not Found if parent lot missing, 400 Bad Request for invalid payload
  - Example:
    curl -X POST "http://localhost:8080/parkingFloors?parkingLotId=1" -H "Content-Type: application/json" -d "{\"parkingSlotList\":[]}"

- GET /parkingFloors
  - Success: 200 OK + list of floors

- GET /parkingFloors/{id}
  - Success: 200 OK + `ParkingFloor` JSON
  - Errors: 404 Not Found

- PUT /parkingFloors/{id}
  - Success: 200 OK + updated `ParkingFloor`

- DELETE /parkingFloors/{id}
  - Success: 204 No Content

3) ParkingSlotController (`/parkingSlots`)
- POST /parkingSlots?floorId={floorId}&slotType={VehicleType}&occupied={boolean}
  - Query params:
    - `floorId` (Long) required
    - `slotType` (VehicleType) required: BIKE | CAR | TRUCK
    - `occupied` (boolean) optional, default false
  - Success: 201 Created + persisted `ParkingSlot` JSON
  - Errors: 404 Not Found if `floorId` not found; 400 Bad Request for invalid `slotType`
  - Example:
    curl -X POST "http://localhost:8080/parkingSlots?floorId=10&slotType=CAR&occupied=false"

- GET /parkingSlots
  - Success: 200 OK + list of slots

- GET /parkingSlots/{id}
  - Success: 200 OK + `ParkingSlot` JSON
  - Errors: 404 Not Found

- PATCH /parkingSlots/{id}?slotType={slotType}&occupied={occupied}
  - Use to set `slotType` and/or `occupied` state.
  - Success: 200 OK + updated `ParkingSlot`
  - Errors: 400 Bad Request if values invalid, 404 Not Found if id missing
  - Example:
    curl -X PATCH "http://localhost:8080/parkingSlots/100?occupied=true"

- DELETE /parkingSlots/{id}
  - Success: 204 No Content

4) EntryController (`/entry`)
- POST /entry?strategy={strategy}
  - Query: `strategy` optional, default `NEAREST`. Supported values: `NEAREST`, `FIRST_AVAILABLE`.
  - Body: `VehicleDto` JSON:
    { "vehicleType": "CAR", "vehicleNo": "KA-01-AB-1234" }
  - Success: 201 Created + `Ticket` JSON. The returned ticket contains references to the allocated `slot` and the `vehicle` entity.
  - Errors / special cases:
    - If no slot can be allocated for the vehicle, `TicketService.generateTicket` throws `NoSlotFoundException`. The global exception handler maps this to HTTP 202 Accepted with a response body containing `timestamp`, `status`, `error`, and `message` describing the conflict. (This is how the current code reports allocation conflicts.)
    - 400 Bad Request for invalid input.
  - Example:
    curl -X POST "http://localhost:8080/entry?strategy=FIRST_AVAILABLE" -H "Content-Type: application/json" -d "{\"vehicleType\":\"CAR\",\"vehicleNo\":\"KA-01-AB-1234\"}"

Notes on allocation behavior:
- Allocation runs inside a transactional service method and uses database-level locking to ensure thread-safe allocation in multi-threaded scenarios. That prevents two concurrent entry requests from receiving the same slot.

5) ExitController (`/exit`)
- POST /exit?strategy={strategy}
  - Query: `strategy` optional, default `HOURLY`.
  - Body: `VehicleDto` JSON (same shape as Entry)
  - Success: 202 Accepted + `Bill` JSON. Behavior:
    - Server finds the active `Ticket` for the vehicle, sets `exitTime`, calculates cost via the chosen billing strategy (e.g., `HourlyPriceCalculation`), persists a `Bill`, marks the ticket inactive/closed, and sets the associated `ParkingSlot` `occupied = false`.
  - Errors:
    - 404 Not Found if no active ticket exists (handled by `EntityNotFoundException`).
    - 400 Bad Request for invalid input.
  - Example:
    curl -X POST "http://localhost:8080/exit?strategy=HOURLY" -H "Content-Type: application/json" -d "{\"vehicleType\":\"CAR\",\"vehicleNo\":\"KA-01-AB-1234\"}"

6) HomeController (`/api`)
- GET /api/
  - Success: 202 Accepted + simple welcome string: "Welcome to Smart Parking Lot"
  - Example:
    curl -X GET "http://localhost:8080/api/"

Global error mapping (implemented in `org.airtribe.exception.GlobalExceptionHandler`)
- `NoSlotFoundException` => HTTP 202 Accepted with JSON body: { timestamp, status, error: "Conflict", message }
- `EntityNotFoundException` => HTTP 404 Not Found with JSON body: { timestamp, status, error: "Resource Not Found", message }
- `IllegalArgumentException` => HTTP 400 Bad Request with JSON body: { timestamp, status, error: "Argument you have passed is invalid", message }

---

Concrete response examples and quick cURL flow

Below are concrete response JSON examples for common endpoints based on the entity shapes in this codebase, and a small sequence of cURL commands (for Windows `cmd.exe`) to exercise a full entry -> exit flow.

A. Concrete response examples

1) Create ParkingLot (POST /parkingLots) — Response 201
{
  "id": 1,
  "parkingFloorList": [],
  "entries": [],
  "exits": [],
  "createdAt": "2026-03-25T09:00:00",
  "updatedAt": "2026-03-25T09:00:00"
}

2) Create ParkingFloor (POST /parkingFloors?parkingLotId=1) — Response 201
{
  "id": 10,
  "parkingSlotList": [],
  "createdAt": "2026-03-25T09:05:00",
  "updatedAt": "2026-03-25T09:05:00"
}

3) Create ParkingSlot (POST /parkingSlots?floorId=10&slotType=CAR&occupied=false) — Response 201
{
  "id": 100,
  "slotType": "CAR",
  "occupied": false,
  "createdAt": "2026-03-25T09:06:00",
  "updatedAt": "2026-03-25T09:06:00"
}

4) Entry (POST /entry?strategy=FIRST_AVAILABLE) — Response 201
{
  "id": 500,
  "slot": { "id": 100, "slotType": "CAR", "occupied": true },
  "vehicle": { "id": 200, "vehicleType": "CAR", "vehicleNo": "KA-01-AB-1234" },
  "entryTime": "2026-03-25T09:10:00",
  "exitTime": null,
  "bill": null,
  "isActive": true,
  "createdAt": "2026-03-25T09:10:00",
  "updatedAt": "2026-03-25T09:10:00"
}

5) Exit (POST /exit?strategy=HOURLY) — Response 202
{
  "id": 900,
  "cost": 20.0,
  "createdAt": "2026-03-25T11:10:00",
  "updatedAt": "2026-03-25T11:10:00"
}

Note: After exit completes, the `ParkingSlot` with id 100 will be persisted with `occupied: false`.

B. Quick cURL flow (Windows `cmd.exe`)

1) Create a parking lot
curl -X POST "http://localhost:8080/parkingLots" -H "Content-Type: application/json" -d "{\"parkingFloorList\":[],\"entries\":[],\"exits\":[]}"

2) Create a floor for the lot (assume returned lot id is 1)
curl -X POST "http://localhost:8080/parkingFloors?parkingLotId=1" -H "Content-Type: application/json" -d "{\"parkingSlotList\":[]}"

3) Create a CAR slot on floor 10 (assume returned floor id is 10)
curl -X POST "http://localhost:8080/parkingSlots?floorId=10&slotType=CAR&occupied=false"

4) Vehicle entry (allocates a slot and creates a ticket)
curl -X POST "http://localhost:8080/entry?strategy=FIRST_AVAILABLE" -H "Content-Type: application/json" -d "{\"vehicleType\":\"CAR\",\"vehicleNo\":\"KA-01-AB-1234\"}"

5) Vehicle exit (calculates bill, frees slot)
curl -X POST "http://localhost:8080/exit?strategy=HOURLY" -H "Content-Type: application/json" -d "{\"vehicleType\":\"CAR\",\"vehicleNo\":\"KA-01-AB-1234\"}"

---

