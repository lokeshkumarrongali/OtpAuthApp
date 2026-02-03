# OtpAuthApp – Passwordless Email Authentication

OtpAuthApp is an Android application that demonstrates a passwordless authentication flow using Email and OTP (One-Time Password).  
The project is built using Jetpack Compose and follows a clean, state-driven architecture as required by the assignment.

The focus of this project is **state management, OTP rule enforcement, and correct separation of UI and business logic**, rather than backend integration.

---

## 🚀 Features

- Email-based passwordless login using OTP
- Local 6-digit OTP generation
- OTP expiry handling (60 seconds)
- Maximum retry limit enforcement (3 attempts)
- Session screen with live login duration tracking
- State-driven UI using Jetpack Compose
- Event logging using an external SDK (Timber)

---

## 🛠️ Mandatory Documentation

### 1. OTP Logic and Expiry Handling

OTP-related logic is implemented in the `OtpManager` class and handled completely locally.

- A 6-digit numeric OTP is generated and stored in memory against the user’s email.
- Each OTP is saved with a timestamp (`System.currentTimeMillis()`).
- During validation, the difference between the current time and the stored timestamp is checked.
- If the OTP is older than 60 seconds, it is treated as expired.
- Each OTP allows a maximum of 3 validation attempts.
- Generating a new OTP for the same email invalidates the previous OTP and resets the attempt count.

This approach simulates backend-like validation while keeping all logic local, as required.

---

### 2. Data Structures Used and Why

- **Sealed Class (`AuthState`)**  
  Used to model all possible UI states (Email input, OTP input, Session, Error).  
  This prevents invalid UI states and ensures predictable one-way data flow.

- **Map<String, OtpData>**  
  Used to store OTP data per email address.  
  This allows fast lookup and easy invalidation when a new OTP is generated.

- **`OtpData` data class**  
  Stores the OTP value, generation timestamp, and attempt count in a single immutable structure.

- **ViewModel + Compose State**  
  The `AuthViewModel` acts as the single source of truth, ensuring UI survives recompositions and configuration changes.

---

### 3. External SDK Choice and Rationale

**Timber** was selected as the external SDK for logging.

- It provides a clean abstraction over standard Android logging.
- Logs are easier to filter and read in Logcat.
- It satisfies the assignment requirement of integrating and initializing a third-party SDK.
- All important authentication events are logged:
  - OTP generated
  - OTP validation success
  - OTP validation failure
  - Logout

---

### 4. AI Assistance vs Manual Implementation

- **AI Assistance (Antigravity / GPT)**  
  Used for generating initial structural boilerplate, Compose layout suggestions, and clarifying API usage.

- **Manually Implemented & Understood**  
  - Application architecture and one-way data flow design  
  - OTP generation, expiry, and retry logic  
  - ViewModel-driven state management  
  - Session timer implementation that survives recompositions  
  - Debugging build and runtime issues during development  

All core logic and architectural decisions were reviewed, understood, and implemented intentionally.

---

## ⚙️ Setup Instructions

1. Open the project in Android Studio.
2. Allow Gradle to sync completely.
3. Run the app on an emulator or physical device (Min SDK: 24).
4. Flow to verify:
   - Enter an email address.
   - Generate OTP (visible via Logcat).
   - Validate OTP to enter session screen.
   - Observe live session duration.
   - Logout to return to email screen.

---

## 📌 Notes

- Session state is maintained in memory using ViewModel and survives recompositions and configuration changes.
- Session persistence across full app restarts is intentionally **not implemented**, as it is not required by the assignment.
