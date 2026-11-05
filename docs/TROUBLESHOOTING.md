# Troubleshooting & Recovery Guide for Realme GT Master (`lunaa`)

Common issues, root causes, and verified fixes when flashing and managing custom ROMs on the Realme GT Master Edition.

---

### Issue 1: Fastboot or ADB Doesn't Detect Phone on macOS
* **Root Cause:** Modern macOS (Ventura, Sonoma, Sequoia, Tahoe) enforces a strict USB accessory security boundary ("Allow accessories to connect").
* **Fix:**
  1. Check your Mac screen for the system notification banner: **"Allow accessory to connect to this Mac?"** -> click **Allow**.
  2. If absent, go to **macOS System Settings** -> **Privacy & Security** -> **Allow accessories to connect** -> set to **Always**.
  3. Try flipping the USB-C plug or moving to an alternative Thunderbolt/USB-C port.

---

### Issue 2: Phone Stuck at Boot Animation (Bootloop) / "Decryption Unsuccessful"
* **Root Cause:** Userdata partition was not formatted, or contains remnants of Realme UI 4.0's encryption keys that clash with PixelOS's file-based encryption (FBEv2).
* **Fix:**
  1. Boot into PixelOS Recovery (Hold `Power + Volume Down`).
  2. Select **Factory reset** -> **Format data/factory reset** -> Confirm **Format data**.
  3. Reboot system.

---

### Issue 3: "Device owner cannot be set" Error
* **Root Cause:** When running `dpm set-device-owner`, Android enforces that **zero accounts** (Google accounts, WhatsApp, etc.) exist on the device.
* **Fix:**
  1. Go to **Settings** -> **Passwords & accounts** -> remove any added accounts.
  2. Run the command again:
     ```bash
     adb shell dpm set-device-owner com.lunaa.debloater/.AdminReceiver
     ```
  3. You can add your accounts back normally after the command succeeds.

---

### Issue 4: Realme 64MP Camera Not Functioning
* **Root Cause:** Accidentally debloating `com.oplus.camera` or missing `dtbo` partition.
* **Fix:**
  1. Ensure `dtbo` was flashed during initial installation:
     ```bash
     fastboot flash dtbo dtbo-lunaa_*.img
     ```
  2. If uninstalled, restore the camera app:
     ```bash
     adb shell pm install-existing com.oplus.camera
     ```

---

### Issue 5: Missing Status Bar / Gesture Navigation after Debloat
* **Root Cause:** Debloating `com.android.systemui` or `com.google.android.apps.nexuslauncher`.
* **Fix:**
  1. Reinstall the launcher:
     ```bash
     adb shell pm install-existing com.google.android.apps.nexuslauncher
     ```
  2. Reinstall System UI:
     ```bash
     adb shell pm install-existing com.android.systemui
     ```
