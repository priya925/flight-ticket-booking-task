# Flight Ticket Booking Task

A REST API for flight ticket booking built with Spring Boot and Java.

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Web + Spring Validation
- In-memory storage (ConcurrentHashMap)
- Gradle

---

## How to Run

### Prerequisites
- Java 17+
- Gradle 8.7+ (or use the included `./gradlew` wrapper)

### Steps

```bash
git clone https://github.com/YOUR_USERNAME/flight-ticket-booking-task.git
cd flight-ticket-booking-task
./gradlew bootRun
```

Or build a fat jar and run it:

```bash
./gradlew clean build
java -jar build/libs/flight-booking-api-0.0.1-SNAPSHOT.jar
```

Or run the project in intellij
```Intellij
Load the gradle project in intellij. Build it and then open FlightBookingApplication.java file
And run this java file and the server will start at port 8080.
```

The API will be available at `http://localhost:8080`.

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/flights` | Register a new flight |
| `GET` | `/api/v1/flights` | List all flights |
| `GET` | `/api/v1/flights/{flightNumber}` | Get flight details & availability |
| `POST` | `/api/v1/bookings` | Book seats on a flight |
| `GET` | `/api/v1/bookings/{bookingReference}` | Retrieve a booking |
| `DELETE` | `/api/v1/bookings/{bookingReference}` | Cancel a booking |

---

## Example Requests

### 1. Register a Flight

```bash
curl -X POST http://localhost:8080/api/v1/flights \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "AI101",
    "origin": "BLR",
    "destination": "DEL",
    "departureTime": "2026-12-01T08:30:00",
    "totalSeats": 3
  }'
```

**Response 201:**
```json
{
  "flightNumber": "AI101",
  "origin": "BLR",
  "destination": "DEL",
  "departureTime": "2026-12-01T08:30:00",
  "totalSeats": 3,
  "availableSeats": 3,
  "bookedSeats": 0
}
```

---

### 2. Book Seats

```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "AI101",
    "passengers": [
      {
        "firstName": "Priya",
        "lastName": "Sharma",
        "passportNumber": "P1234567"
      },
      {
        "firstName": "Rohan",
        "lastName": "Mehta",
        "passportNumber": "P7654321"
      }
    ]
  }'
```

**Response 201:**
```json
{
  "bookingReference": "A3F9C12B",
  "flightNumber": "AI101",
  "status": "CONFIRMED",
  "passengers": [
    { "firstName": "Priya", "lastName": "Sharma", "passportNumber": "P1234567" },
    { "firstName": "Rohan", "lastName": "Mehta", "passportNumber": "P7654321" }
  ],
  "seatCount": 2,
  "bookedAt": "2026-05-16T10:15:30"
}
```

---

### 3. Check Flight Availability

```bash
curl http://localhost:8080/api/v1/flights/AI101
```

**Response 200:**
```json
{
  "flightNumber": "AI101",
  "origin": "BLR",
  "destination": "DEL",
  "departureTime": "2026-12-01T08:30:00",
  "totalSeats": 3,
  "availableSeats": 1,
  "bookedSeats": 2
}
```

---

### 4. Cancel a Booking

```bash
curl -X DELETE http://localhost:8080/api/v1/bookings/A3F9C12B
```

**Response 200:**
```json
{
  "bookingReference": "A3F9C12B",
  "flightNumber": "AI101",
  "status": "CANCELLED",
  "passengers": [...],
  "seatCount": 2,
  "bookedAt": "2026-05-16T10:15:30"
}
```

---

### 5. Overbooking Attempt (rejected)

If a flight has 1 seat left and you try to book 2:

```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "flightNumber": "AI101",
    "passengers": [
      {"firstName": "A", "lastName": "B", "passportNumber": "P0000001"},
      {"firstName": "C", "lastName": "D", "passportNumber": "P0000002"}
    ]
  }'
```

**Response 409 Conflict:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Cannot book 2 seat(s) on flight AI101 — only 1 seat(s) available",
  "timestamp": "2026-05-16T10:20:00"
}
```

---

## Key Design Decisions

### Overbooking Prevention
Seat reservation uses an `AtomicInteger` with a compare-and-set (CAS) loop in `Flight.reserveSeats()`. This guarantees correctness under concurrent requests without locks — if two requests race for the last seat, exactly one wins.

### REST Semantics
- `POST /bookings` — create a booking
- `DELETE /bookings/{ref}` — cancel (not a custom `/cancel` action verb)
- `GET /flights/{number}` — always returns live seat counts

### Booking Reference
Short 8-character uppercase alphanumeric reference generated from a UUID substring. Sufficient for in-memory scale.

---

## What I Would Improve With More Time

1. **Idempotency keys** — currently a double-submit creates duplicate bookings; an `Idempotency-Key` header would prevent this
2. **Flight status** — cancelled/closed flights should reject new bookings
3. **Passenger deduplication** — detect same passport number booked twice on same flight
4. **Pagination** — `GET /flights` would need pagination for large datasets
5. **Persistent storage** — swap `ConcurrentHashMap` for JPA + H2/PostgreSQL with minimal code changes (repository interfaces are already isolated)
6. **Structured logging** — correlation IDs per request for traceability
7. **OpenAPI/Swagger** — add `springdoc-openapi` for interactive API docs
8. **Price and fare class** — real bookings need fare tiers (economy/business) and pricing
9. **Booking reference collision handling** — extremely unlikely but not handled; a retry loop or UUID-based reference would fix this
10. **Integration test isolation** — tests currently share application context; each test class should reset state via `@BeforeEach`
11. **Unit test coverage** — Have to add unit test cases 
