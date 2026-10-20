# ⚡ PowerTouch — Virtual Power Button for Pixel 7a & Modern Android

<div align="center">

[![Latest Release](https://img.shields.io/github/v/release/kedar773/powertouch?style=for-the-badge&color=BB86FC)](https://github.com/kedar773/powertouch/releases/latest)
[![Platform](https://img.shields.io/badge/Platform-Android%209%20to%2015%2B-brightgreen?style=for-the-badge)](https://github.com/kedar773/powertouch)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

### 📲 [👉 Click Here to Download PowerTouch.apk (Latest v1.0.1) 👈](https://github.com/kedar773/powertouch/releases/download/v1.0.1/PowerTouch.apk)

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
- **OLED Dark Theme**: Deep dark `#121214` background with razor-sharp high contrast typography for optimal readability and battery savings on Pixel's OLED display.
- **In-App Update Checker**: Scan this GitHub repository directly from the app and download newer releases with one tap.

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

### 3. 🔄 In-App GitHub Update Scanner
* Check for updates directly inside the app with a single tap.
* Connects to GitHub's public API, checks release tags, displays the changelog, and offers a 1-tap download link for newer versions.

### 4. 📳 Haptic Feedback
* Predefined tactile vibrations for tap (`EFFECT_CLICK`) and long-press (`EFFECT_HEAVY_CLICK`), toggleable in settings.

### 5. 🎛️ Pixel Quick Settings Shade Tile
* Adds a dedicated **Lock Screen** tile in your pull-down Quick Settings shade. Swipe down from top of screen anytime and tap to lock.

---

## 📥 Installation Guide

### Option A: Direct Download on Phone (Easiest)
1. On your Pixel 7a, download [**PowerTouch.apk (v1.0.1)**](https://github.com/kedar773/powertouch/releases/download/v1.0.1/PowerTouch.apk).
2. Tap the downloaded file in your notifications or Files app.
3. If Google Play Protect displays a warning regarding Accessibility Services (Google's Enhanced Fraud Protection for sideloaded web APKs), temporarily pause Play Protect scanning in Google Play Store settings, or install via ADB.
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
   *(If greyed out as "Restricted setting" on Android 13/14/15, go to phone Settings > Apps > PowerTouch > 3 dots ⋮ in top-right > "Allow restricted settings" and verify your PIN).*
2. **Display Over Other Apps** *(Required for Floating Button & Edge Handle)*:
   - Tap **"Grant"** next to Display Over Other Apps.
   - Toggle **Allow display over other apps** for PowerTouch.
3. *(Recommended)* **Ignore Battery Optimization**:
   - Tap **"Optimize"** to ensure Android's battery saver doesn't pause the accessibility overlay.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

<br/>

<div align="center">
<b>made by kedar and for kedar</b>
</div>
