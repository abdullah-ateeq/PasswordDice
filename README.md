# PasswordDice 🎲🔐

**PasswordDice** is a modern, feature-rich, and cryptographically secure password and passphrase generator built for Android using **Jetpack Compose** and **Material 3**. 

Designed with privacy and security as top priorities, PasswordDice ensures that every generated secret is truly random, auditable, and customizable without relying on weak pseudorandom number generators or external servers.

---

## ✨ Key Features

### 🔒 Cryptographically Secure Generation
- **`SecureRandom` Engine:** Exclusively utilizes Java's cryptographically secure pseudo-random number generator (`SecureRandom`).
- **Fisher-Yates Shuffle:** Implements unbiased position randomization to ensure guaranteed character sets (e.g., mandatory uppercase, numbers, or symbols) are distributed uniformly throughout the password.
- **Shannon Entropy Analysis:** Calculates mathematical entropy (in bits) and strength classifications (Weak, Fair, Good, Strong, Excellent) in real-time.

### ⚙️ Granular Customization
- **Character Sets:** Enable/disable Uppercase (`A-Z`), Lowercase (`a-z`), Numbers (`0-9`), and Symbols (`!@#$%^&*...`).
- **Ambiguous Character Exclusion:** Option to filter out easily confused characters (e.g., `O`, `0`, `l`, `I`, `1`) to prevent transcription errors.
- **Custom Character Pools:** Add your own custom symbols or allowed characters.
- **Mandatory Set Constraints:** Guarantee at least one character from each selected set is included.
- **Passphrase Mode:** Generate memorable, highly secure multi-word passphrases (Diceware-style).

### 🎨 Modern Material 3 UI / UX
- **Dynamic Theming:** Built entirely with Jetpack Compose and Material 3 design guidelines.
- **Theme Modes:** Supports Light, Dark, and System default theme preferences.
- **Instant Actions:** One-tap copy to clipboard with haptic feedback and visual confirmation toasts.
- **Password History & Management:** Securely store and review recent generated passwords with quick export/copy options.
- **Auto-Clear Security:** Optional setting to automatically wipe sensitive generated passwords from memory when leaving the app (`onStop`).

---

## 🛠️ Tech Stack & Architecture

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
- **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
- **State Management:** Kotlin Coroutines & `StateFlow`
- **Randomness:** `java.security.SecureRandom`
- **Build System:** Gradle with Kotlin DSL (`build.gradle.kts`)
- **Testing:** JUnit 5 / JUnit 4 unit tests for cryptographic generators and entropy calculations.

---

## 📁 Project Structure

```tree
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/passworddice/
│   │   │   ├── data/
│   │   │   │   ├── generator/     # SecurePasswordGenerator & Passphrase logic
│   │   │   │   ├── model/         # PasswordConfig, PasswordResult, CharacterSet, Strength
│   │   │   │   └── preferences/   # AppPreferences & SharedPreferences repository
│   │   │   ├── ui/
│   │   │   │   ├── about/         # About Screen & App Info
│   │   │   │   ├── components/    # Reusable Compose components (PasswordCard, StrengthMeter, etc.)
│   │   │   │   ├── home/          # Main Generator Screen & ViewModel
│   │   │   │   ├── navigation/    # App Navigation graph (NavRoutes)
│   │   │   │   ├── settings/      # Settings Screen & ViewModel
│   │   │   │   ├── splash/        # Splash Screen
│   │   │   │   └── theme/         # Material 3 Colors, Typography, Theme
│   │   │   └── MainActivity.kt    # Entry point & Lifecycle handler
│   │   └── res/                   # Drawables, mipmaps, values
│   └── test/                      # Unit tests (SecurePasswordGeneratorTest)
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Koala / Ladybug or newer recommended)
- **Android SDK** API level 24 or higher (Target API 34+)
- **JDK 17** or higher

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/PasswordDice.git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync dependencies.
4. Run unit tests to verify generator correctness:
   ```bash
   ./gradlew test
   ```
5. Run the app on an emulator or physical Android device (`Run 'app'`).

---

## 🧪 Testing

PasswordDice includes robust unit tests verifying cryptographic constraints, entropy formulas, character set filtering, and edge cases. Run tests via Gradle:

```bash
./gradlew testDebugUnitTest
```

---

## 👤 Author

**Abdullah Ateeq**
- GitHub: [@abdullah-ateeq](https://github.com/abdullah-ateeq)

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
