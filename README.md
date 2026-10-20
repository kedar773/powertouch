# ⚡ PowerTouch — Virtual Power Button for Pixel 7a & Modern Android

<div align="center">

[![Latest Release](https://img.shields.io/github/v/release/kedar773/powertouch?style=for-the-badge&color=6750A4)](https://github.com/kedar773/powertouch/releases/latest)
[![Platform](https://img.shields.io/badge/Platform-Android%209%20to%2015%2B-brightgreen?style=for-the-badge)](https://github.com/kedar773/powertouch)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

### 📲 [👉 Click Here to Download PowerTouch.apk (Latest v1.0.0) 👈](https://github.com/kedar773/powertouch/releases/download/v1.0.0/PowerTouch.apk)

*Direct download link for Google Pixel 7a and any Android phone running Android 9 through 15+*

</div>

---

## 🎯 Why PowerTouch?

If the physical power button on your **Google Pixel 7a** is broken, unresponsive, or stuck, basic tasks like locking your screen or accessing the reboot/shutdown menu become difficult.

Most power button replacement apps on the Play Store rely on obsolete Device Admin permissions, which **disable fingerprint unlock** every time the screen is locked and force PIN entry.

**PowerTouch** solves this properly:
- Built with **native Kotlin** and Android's modern **Accessibility Service** (`GLOBAL_ACTION_LOCK_SCREEN` & `GLOBAL_ACTION_POWER_DIALOG`).
- **Keeps Biometric Fingerprint Unlock fully working** every time you lock.
- Features both a **Moveable Floating Icon** and a **Stealth Side Drawer Tile** (edge slider).
- Ultra-low battery consumption: 0% idle background drain, zero polling loops.

---

## ✨ Features Breakdown

### 1. 🔘 Moveable Floating Icon
* A circular power button that floats over any application or home screen.
* **Single Tap**: Vibrates with a crisp haptic tick and locks the screen instantly.
* **Long Press (0.5s)**: Brings up the native Android System Power Dialog (*Power off, Restart, Emergency, Lockdown*).
* **Smooth Snap to Edge**: Drag it anywhere; when released, it smoothly snaps to the nearest display edge (left or right).
* **OLED Screen Protection / Auto-fade**: After 3 seconds of inactivity, it automatically dims down to your chosen idle opacity (e.g. 30%) to eliminate any risk of OLED screen burn-in. Tapping it restores full opacity.
* **Customizable**: Real-time sliders in the app to adjust button size (40dp to 80dp), active opacity, and idle opacity.

### 2. 🪟 Side Drawer Tile (Edge Slider)
* For users who prefer a clean screen without floating bubbles.
* **Hidden Edge Handle**: Sits flush against the screen bezel as a slim, unobtrusive handle.
* **Slide Inward Gesture**: Simply swipe/slide your finger inward from the screen edge to pull out the sleek power tile.
* **Direct Interaction**: Tapping the handle locks the screen directly; holding it opens the power menu.
* **Vertically Moveable**: Drag the handle up or down along the edge so you can position it comfortably away from Pixel's system back-swipe gesture zone.
* **Side Placement**: Choose between **Right Edge** or **Left Edge** in settings.
* **Auto-Retract**: Retracts back into the screen border after 3.5 seconds of idle time.

### 3. 📳 Haptic Feedback
* Predefined tactile vibrations for tap (`EFFECT_CLICK`) and long-press (`EFFECT_HEAVY_CLICK`), toggleable in settings.

### 4. 🎛️ Pixel Quick Settings Shade Tile
* Adds a dedicated **Lock Screen** tile in your pull-down Quick Settings shade. Swipe down from top of screen anytime and tap to lock.

---

## 📥 Installation Guide

### Option A: Direct Download on Phone (Easiest)
1. On your Pixel 7a, open Chrome and download [**PowerTouch.apk**](https://github.com/kedar773/powertouch/releases/download/v1.0.0/PowerTouch.apk).
2. Tap the downloaded file in your notifications or Files app.
3. If prompted, allow "Install from Unknown Sources".
4. Tap **Install** and open **PowerTouch**.

### Option B: Install via ADB (USB Debugging)
If your phone is connected to your computer:
```bash
adb install -r PowerTouch.apk
```

---

## ⚙️ Initial Setup & Permissions (1 Minute Setup)

When launching PowerTouch for the first time, grant the two permissions directly from the in-app dashboard:

1. **Accessibility Service** *(Required for Lock & Power Menu)*:
   - Tap **"Enable"** next to Accessibility Service.
   - You will be taken to *Settings > Accessibility*.
   - Tap **PowerTouch** and toggle it **ON**.
2. **Display Over Other Apps** *(Required for Floating Button & Edge Handle)*:
   - Tap **"Grant"** next to Display Over Other Apps.
   - Toggle **Allow display over other apps** for PowerTouch.
3. *(Recommended)* **Ignore Battery Optimization**:
   - Tap **"Optimize"** to ensure Android's battery saver doesn't pause the accessibility overlay.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **Min SDK**: API 28 (Android 9.0)
- **Target SDK**: API 35 (Android 15)
- **UI Framework**: Material Design 3 (Material Components)
- **Key APIs**:
  - `AccessibilityService` (`GLOBAL_ACTION_LOCK_SCREEN`, `GLOBAL_ACTION_POWER_DIALOG`)
  - `WindowManager` with `TYPE_APPLICATION_OVERLAY`
  - `TileService` (`QS_TILE`)
  - `Vibrator` / `VibratorManager` (`VibrationEffect`)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
