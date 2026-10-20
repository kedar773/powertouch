# PowerTouch — Virtual Power Button for Google Pixel 7a

**PowerTouch** is a lightweight native Android utility built specifically to replace a broken physical power button on the Google Pixel 7a (and other modern Android devices running Android 9 through Android 15+).

---

## 🚀 Key Features

1. **Floating Power Icon**:
   - Draggable anywhere on the screen.
   - Automatically snaps smoothly to the nearest edge (left or right) upon release.
   - **Single Tap**: Vibrates with subtle haptic click and instantly locks the screen.
     - *Crucial note*: Uses native `AccessibilityService.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)`, which **preserves biometric fingerprint unlock** (unlike old DeviceAdmin lock which forces PIN entry).
   - **Long Press (0.5s)**: Triggers the native Android Power Dialog (Restart, Power off, Emergency, Lockdown).
   - **OLED Burn-in Protection / Auto-fade**: After 3 seconds of inactivity, smoothly fades down to an idle opacity (customizable, default 30%). Touching it instantly restores active opacity.

2. **Side Drawer Tile (Edge Slider)**:
   - Discreet handle hidden against the screen edge (left or right side).
   - **Slide Inward Gesture**: Swiping inward from the screen edge draws out the power tile smoothly.
   - **Direct Tap / Hold**: Tap the handle to lock, or long-press to open the Power Menu.
   - **Vertically Moveable**: Drag the handle up or down along the edge so it never interferes with Android's system back-swipe gesture.
   - **Auto-retract**: Automatically retracts back into the screen edge after 3.5s of idle time.

3. **Haptic Feedback**:
   - Modern vibration click effect on tap and heavy click on long-press (can be toggled in settings).

4. **Quick Settings Tile**:
   - Adds a "Lock Screen" tile to your Pixel's swipe-down Quick Settings shade.

5. **Battery Friendly**:
   - Zero background polling loops.
   - Operates inside Android's native system-managed Accessibility Service, consuming negligible memory and battery.

---

## 📦 Ready-to-Install APK

The compiled APK is ready at:
- **`E:\back_button\PowerTouch.apk`**
- Or debug build output: `E:\back_button\app\build\outputs\apk\debug\app-debug.apk`

### Quick Installation:
If your Pixel 7a is connected via USB with USB Debugging enabled:
```bash
adb install -r E:\back_button\PowerTouch.apk
```
Or simply transfer `PowerTouch.apk` to your phone and install it using the Files app.

---

## ⚙️ Initial Setup on Pixel 7a

When you launch **PowerTouch** for the first time:
1. **Enable Accessibility Service**:
   - Tap **"Enable"** on the Accessibility row.
   - In Settings, select **PowerTouch** and switch the toggle **ON**.
2. **Grant Display Over Other Apps**:
   - Tap **"Grant"** on the Overlay row.
   - Toggle **"Allow display over other apps"** for PowerTouch.
3. *(Optional)* **Ignore Battery Optimization**:
   - Tap **"Optimize"** to exempt PowerTouch from Android's background app suspension.
