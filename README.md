# ScamBust🛡️

**ScamBust** is an AI-powered digital guardian designed specifically to protect senior citizens from the epidemic of SMS and digital scams in India. By utilizing a **Dual-Layer Classification** system, it intercepts malicious messages in real-time and provides a simplified, high-contrast interface for maximum accessibility.

## 🚀 The Mission
Elderly users are often targeted by "Urgency Scams" (KYC updates, electricity bill threats). ScamBust removes the guesswork by providing a clear **Green/Yellow/Red** safety status, ensuring they never have to face a suspicious link alone.

---

## 🛠️ Technical Architecture

### 1. Dual-Layer Classification
* **Layer 1 (On-Device):** Fast, pattern-based matching for known Indian scam keywords and short-codes.
* **Layer 2 (Cloud - FastAPI):** Heuristic and NLP-driven analysis to determine the intent and risk score of unknown senders.

### 2. Tech Stack
* **Android:** Jetpack Compose (UI), Retrofit (Networking), BroadcastReceiver (SMS Listening).
* **Backend:** FastAPI (Python), Pydantic for data validation.
* **UX:** Senior-optimized design with 60dp+ touch targets and high-contrast semantic colors.

---

## 📂 Project Structure

### `/android_app`
Built with **Jetpack Compose**, this module handles:
- **`SmsReceiver.kt`**: Background listener for incoming SMS.
- **`ScamBustUI.kt`**: The reactive, color-coded dashboard.
- **`OverlayService.kt`**: Full-screen emergency alerts for high-risk scams.

### `/fastapi_server`
The brain of the operation:
- **`main.py`**: The API handling analysis requests.
- **`logic.py`**: The scoring algorithm and pattern matching.

---

## 🛡️ Key Features
- **Real-time Interception:** Detects threats before the notification is even opened.
- **Guardian Alerts:** Full-screen red warnings that block accidental link clicks.
- **Minimalist UX:** No complex menus; just one screen that tells you if you are safe.

## 📝 Permissions Required
- `RECEIVE_SMS` & `READ_SMS`: To scan incoming threats.
- `SYSTEM_ALERT_WINDOW`: To show the emergency overlay on top of other apps.
- `INTERNET`: To communicate with the AI analysis engine.

---
*Built with ❤️ for digital safety.*
