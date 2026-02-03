# OtpAuthApp - Passwordless Email Authentication

A sophisticated, secure, and visually stunning Android application implementing a passwordless authentication flow using Email and OTP (One-Time Password). This project follows a "Modern Dark" aesthetic with custom animations and robust local state management.

## 🚀 Features

- **Passwordless Auth**: Secure email-based login without the need for traditional passwords.
- **Local OTP System**: 6-digit OTP generation with 60-second expiry and adaptive retry logic.
- **Persistent Sessions**: Real-time session duration tracking that survives app restarts.
- **Premium UI/UX**: "Modern Dark" theme, glassmorphism info cards, and a dynamic bubbly canvas background.
- **High-Visibility Logging**: Integrated Timber SDK with distinct tags for easy audit and evaluation.

---

## 🛠️ Mandatory Documentation

### 1. OTP Logic and Expiry Handling
The OTP logic is encapsulated in the `OtpManager` class, simulating a secure backend service locally.
- **Generation**: Uses a cryptographically secure random number generator to produce a 6-digit numeric string.
- **Storage**: OTPs are stored in a thread-safe `HashMap` keyed by the user's email.
- **Expiry Implementation**: Every OTP is stored with a precise millisecond timestamp. During validation, the manager calculates the time difference. If $> 60,000$ ms, the OTP is instantly rejected as expired.
- **Invalidation**: Generating a new OTP for the same email automatically replaces the existing record, resetting both the code and the attempt counter.

### 2. Data Structures Used and Why
- **`MutableStateFlow` & `State`**: Used within the `AuthViewModel` to manage a single source of truth for the UI (`AuthState`). This ensures predictable transitions between Login, OTP entry, and Session screens.
- **`Sealed Class (AuthState)`**: Employed to represent the entire UI state space. This prevents invalid states (like showing a session timer before login) and makes the code highly readable and robust against errors.
- **`HashMap`**: Used for `otpStorage` to provide $O(1)$ lookup time for verifying codes against email addresses, ensuring high performance even if scaled.
- **`SharedPreferences`**: Chosen for session persistence due to its lightweight nature and native support for persisting simple key-value pairs (like session start time) across app launches.

### 3. External SDK Choice and Rationale
I chose **Timber** as the external SDK.
- **Rationale**: Standard Android `Log` calls can be scattered and hard to manage. Timber provides a powerful abstraction that allows us to:
    - Centralize logging logic (e.g., debug-only logging).
    - Use distinctive tags like `[AUTH_EVENT]` and emojis to make key lifecycle events (OTP generated, Login success, etc.) instantly identifiable in Logcat.
    - Improve code readability by removing the need for repeating TAG strings in every log call.

### 4. AI Assistance vs. Manual Implementation
- **AI (Antigravity) Assistance**: Used for generating the initial boilerplate for Jetpack Compose layouts, designing the math behind the `Canvas` bubbly background, and quickly identifying missing dependency versions.
- **Manual Understanding & Implementation**:
    - **Architecture**: I manually designed the state-driven architecture ensuring strict separation between the `ViewModel` (state/logic) and `MainActivity` (navigation/composition).
    - **Logic Debugging**: I personally resolved the "BuildConfig" visibility issues and the "Resend OTP" state management bugs during execution.
    - **Theme Normalization**: I manually overrode the Material 3 dynamic color system to ensure a consistent premium dark look across all devices and OS modes.

---

## ⚙️ Setup Instructions

1. **Clone the project** into Android Studio.
2. **Build the Project**: Wait for Gradle sync to complete. The project uses standard Material 3 and Jetpack Compose dependencies.
3. **Run**: Deploy to an emulator or physical device (Min SDK: 24).
4. **Verification**:
    - Enter any valid email.
    - Check **Logcat** (filter by `[AUTH_EVENT]`) to see the generated OTP.
    - Enter the OTP to view the session tracker.
    - Try closing the app and reopening it—your session will persist!

---

## 🏆 Bonus Features Included
- ✅ **Visual Countdown Timer**: Real-time feedback in the OTP screen showing remaining seconds before expiry.
- ✅ **Sealed UI States**: Robust state management using `AuthState`.
- ✅ **Session Persistence**: Survives app restarts and device rotations.
- ✅ **Premium Aesthetics**: Custom-built bubbly background and glassmorphism UI.
