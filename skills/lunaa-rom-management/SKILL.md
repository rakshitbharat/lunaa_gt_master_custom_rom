---
name: lunaa-rom-management
description: Automated flashing, diagnostics, recovery, and 1-click debloating for the Realme GT Master Edition (lunaa / RMX3360 / RMX3363) running PixelOS, crDroid, or AOSP.
---

# Lunaa ROM Management & Flashing Skill

Use this skill whenever an AI agent needs to interact with, diagnose, flash, or debloat a **Realme GT Master Edition** (codename: `lunaa`, models `RMX3360`, `RMX3363`, `RMX3361`).

---

## 1. Hardware & Platform Profile

* **Device:** Realme GT Master Edition
* **Codename:** `lunaa`
* **SoC:** Qualcomm Snapdragon 778G 5G (`SM7325` / `lahaina` family)
* **Partitions:** Virtual A/B (`boot`, `vendor_boot`, `dtbo`, `super`, `userdata`)
* **Firmware Base:** Stock Realme UI 4.0 (Android 13, F-series firmware)

---

## 2. Autonomous Workflow Instructions

### Phase 1: Device State Detection
Always verify connection and active mode first:

```bash
# Check if device is in Fastboot mode
fastboot devices

# Check if device is in ADB / Recovery / Sideload mode
adb devices
```

If the device is in **Fastboot Mode**, query hardware variables:
```bash
fastboot getvar product         # Must return: lahaina
fastboot getvar unlocked        # Must return: yes
fastboot getvar current-slot    # Returns: a or b
```

---

### Phase 2: Partition Flashing Sequence (PixelOS / crDroid)
When flashing a new ROM partition set:

1. Flash the three recovery images to the active slot:
   ```bash
   fastboot flash boot boot-lunaa_*.img
   fastboot flash vendor_boot vendor_boot-lunaa_*.img
   fastboot flash dtbo dtbo-lunaa_*.img
   ```
2. Reboot into recovery:
   ```bash
   fastboot reboot recovery
   ```
3. Instruct user or verify that **Factory Reset -> Format Data** is performed in recovery to clear old FBE encryption keys.
4. When `adb devices` reports `<serial> sideload`:
   ```bash
   adb sideload <ROM_PACKAGE_NAME>.zip
   ```
5. Reboot to system once sideload completes with `Total xfer: 1.00x`.

---

### Phase 3: Automated Developer Debloat
Once the phone boots into the system and `adb devices` reports `<serial> device`:

```bash
# 1. Run the safe batch debloat sequence
adb shell "
for pkg in \
  com.google.android.googlequicksearchbox \
  com.google.android.gm \
  com.google.android.apps.maps \
  com.google.android.calendar \
  com.google.android.apps.photos \
  com.android.chrome \
  com.android.vending \
  com.google.android.apps.messaging \
  com.google.android.dialer \
  com.google.android.contacts \
  com.google.android.calculator \
  com.google.android.deskclock \
  com.google.android.apps.wellbeing \
  com.google.android.apps.turbo \
  com.google.android.as \
  com.google.android.projection.gearhead \
  com.android.hotwordenrollment.okgoogle \
  com.android.hotwordenrollment.xgoogle \
  com.google.android.apps.aiwallpapers \
  com.google.pixel.livewallpaper \
  net.pixelos.ota \
  com.oplus.appplatform
do
  pm uninstall -k --user 0 \$pkg 2>/dev/null && echo \"Purged: \$pkg\"
done
"

# 2. Verify remaining activities in launcher
adb shell "cmd package query-activities --brief -a android.intent.action.MAIN -c android.intent.category.LAUNCHER"
```

---

### Phase 4: Deploying 1-Click Debloater Companion App

1. Install the APK:
   ```bash
   adb install -r app_1click_debloater/bin/Debloater.apk
   ```
2. Grant Device Owner permissions:
   ```bash
   adb shell dpm set-device-owner com.lunaa.debloater/.AdminReceiver
   ```
3. Launch the app:
   ```bash
   adb shell am start -n com.lunaa.debloater/.MainActivity
   ```

---

### Phase 5: Hardware Health Verification
Verify core hardware subsystems after any update or debloat:

```bash
# Check available memory (> 4 GB free buffer is ideal)
adb shell "cat /proc/meminfo | head -n 5"

# Verify OPlus 64MP Camera package is present
adb shell "pm list packages | grep oplus.camera"

# Verify Dolby Atmos audio engine
adb shell "pm list packages | grep dolby"
```
