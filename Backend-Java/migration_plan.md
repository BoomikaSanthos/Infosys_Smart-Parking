# Backend Migration Plan: Node.js to Java (Spring Boot)

## 1. Analysis phase
- [x] **Explore Existing Backend**: Node.js/Express implementation in `Backend/` directory.
- [x] **Identify Stack**: Node.js, Express, MongoDB, JWT.
- [x] **Database Schema**: MongoDB collections (users, slots, bookings, payments, companies, events).
- [ ] **API Endpoint Mapping**: Compare Node routes with Java controllers.

## 2. Java Technical Stack
- [x] **Framework**: Spring Boot 3.2.2
- [x] **Database**: Spring Data MongoDB
- [x] **Security**: Spring Security + JWT (jjwt)
- [x] **Efficiency**: Lombok (Note: Facing annotation processing issues, using explicit constructors for now).

## 3. Implementation Status
- [x] **Foundation**: Project structure, `pom.xml`, `application.properties`.
- [x] **Database Layer**: Repositories for User, Slot, Booking, Payment, Company, Event.
- [x] **Security**: `JwtUtil`, `JwtRequestFilter`, `SecurityConfig`.
- [ ] **API Layer (Gap Analysis)**:
    - [ ] `AuthController` vs `auth.js`
    - [ ] `UserController` vs `user.js`
    - [ ] `SlotController` vs `slotRoutes.js`
    - [ ] `BookingController` vs `booking.js`
    - [ ] `AdminController` vs `admin.js` (User recently requested a specific structure)
    - [ ] `PaymentController` vs `payment.js`
    - [ ] `AnalyticsController` vs `analytics.js`
    - [ ] `CompanyController` vs `company.js`
    - [ ] `EventController` vs `eventRoutes.js`

## 4. Verification & Testing
- [x] **Compilability**: Project compiles after fixing Lombok issues manually.
- [ ] **Run Status**: Application fails at runtime (investigation needed).
- [ ] **Functionality Matching**: Ensure all Node.js logic is replicated in Java services.

---

### Immediate Next Steps:
1.  **Investigate Runtime Error**: Run `mvn spring-boot:run` and capture the exception trace.
2.  **Gap Analysis**: Read Node.js route files and compare functions with Java Services.
3.  **Update AdminController**: Implement the structure provided in the previous user request.
