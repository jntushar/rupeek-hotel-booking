# Hotel Booking Service

Rupeek hotel-booking machine-coding exercise. It uses Java 17, Maven and in-memory repositories so the domain and API can be exercised without setting up a database or an external payment gateway.

## Run

```bash
mvn test
mvn spring-boot:run
```

The service starts on `http://localhost:8080`.

## Main flows

1. Create an owner account: `POST /api/owners`.
2. Add one or more properties to that owner: `POST /api/properties`.
3. Search by location, dates, guests and optional filters: `POST /api/properties/search`.
4. Create a booking: `POST /api/bookings`.
5. Pay with `CARD`, `UPI` or `WALLET`: `POST /api/bookings/{id}/payment`.
6. Cancel a confirmed booking: `POST /api/bookings/{id}/cancellation`.
7. Read booking state: `GET /api/bookings/{id}`.

## Example requests

Create an owner:

```json
{"name":"North Star Hospitality"}
```

Add a property (use the returned owner id):

```json
{
  "ownerId":"OWNER_ID",
  "name":"North Star Agra",
  "city":"Agra",
  "locality":"Civil Lines",
  "starRating":4,
  "amenities":["WIFI","PARKING"],
  "roomTypes":[
    {"name":"Deluxe Room","capacity":2,"totalRooms":3,"pricePerNight":2800.00},
    {"name":"Family Room","capacity":4,"totalRooms":2,"pricePerNight":4200.00}
  ]
}
```

Search:

```json
{
  "city":"Agra",
  "checkIn":"2027-01-10",
  "checkOut":"2027-01-12",
  "guests":2,
  "minPrice":2000,
  "maxPrice":4000,
  "amenities":["WIFI"],
  "minStars":3
}
```

Create booking:

```json
{
  "propertyId":"PROPERTY_ID",
  "roomTypeId":"ROOM_TYPE_ID",
  "checkIn":"2027-01-10",
  "checkOut":"2027-01-12",
  "guests":2
}
```

Pay:

```json
{"method":"UPI"}
```

## Design notes and assumptions

- An owner account can own multiple properties. A standalone property is represented by an owner with one property; there is no separate single-property code path.
- A property contains room types. Inventory is counted per room type, not per physical room number.
- Date ranges are half-open: check-in is included and check-out is excluded. Adjacent stays do not overlap.
- A booking holds inventory while it is `PENDING_PAYMENT` and continues to occupy it when `CONFIRMED`. A cancelled booking releases it.
- The mock payment methods approve requests. The `PaymentMethod` interface is the seam for a real gateway or another payment method.
- The default cancellation policy refunds the full booking amount when cancelled before check-in; cancellations on or after check-in receive no refund. This is intentionally a simple policy and can be replaced through `CancellationPolicy`.
- Repeating payment for an already confirmed booking returns its current state rather than charging it again.
- In-memory repositories are behind interfaces and data is lost on restart.
- Booking creation synchronizes on the room-type inventory object to avoid two requests taking the last unit at the same time within this single application instance.

## What I would improve with more time

- Add a persisted inventory ledger / database transaction and distributed locking for multiple application instances.
- Add payment records and durable idempotency keys, plus compensation handling for gateway timeouts.
- Add configurable cancellation windows, taxes, fees and a pricing strategy.
- Add pagination, sorting, richer filter specifications and OpenAPI documentation.
- Add integration tests for REST validation and concurrent booking requests.

## API notes

All dates use ISO format (`yyyy-MM-dd`). Validation failures return HTTP 400, missing resources return 404, and unavailable inventory or invalid state transitions return 409. Authentication, authorisation, UI and production deployment are intentionally out of scope per the exercise brief.
