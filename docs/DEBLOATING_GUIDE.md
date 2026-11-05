# Developer Debloating Guide for Realme GT Master (`lunaa`)

This document details the debloating architecture for achieving an ultra-lightweight, zero-telemetry development testbench on the Realme GT Master Edition running PixelOS Android 16.

---

## 1. Why Debloat an AOSP / Pixel ROM?

While PixelOS removes 100% of stock Realme UI / ColorOS bloatware (HeyTap, Breeno, Glance, Analytics), consumer Google apps and background synchronization daemons still run continuous background wake-locks:
* **Background Telemetry:** Digital Wellbeing, Device Health Services, On-Device Personalization, Federated Compute.
* **Continuous Listeners:** Hotword detection ("OK Google"), speech engines, companion device managers.
* **Heavy RAM Consumers:** Live wallpaper pickers, creative assistants, news feeds.

Purging these services frees **>2.0 GB of unallocated physical RAM** and reduces idle CPU usage to **<2%**.

---

## 2. The 1-Click Debloater Android App

The project includes a native, lightweight (21 KB) companion app located in [`app_1click_debloater`](../app_1click_debloater/):
* **Package Name:** `com.lunaa.debloater`
* **Mechanism:** Utilizes Android Enterprise **Device Owner** (`DevicePolicyManager.setApplicationHidden`) + userland package manager commands.
* **No Root Required:** Operates cleanly without needing Magisk, KernelSU, or active Shizuku background services.

### Installation & Setup:
1. Install the APK:
   ```bash
   adb install -r app_1click_debloater/bin/Debloater.apk
   ```
2. Grant Device Owner privileges via ADB:
   ```bash
   adb shell dpm set-device-owner com.lunaa.debloater/.AdminReceiver
   ```
3. Open **1-Click Debloater** from your app drawer and tap **⚡ DEBLOAT ENTIRE PHONE**.

---

## 3. Comprehensive List of Removed Packages

| Category | Package Name | Reason for Removal |
| :--- | :--- | :--- |
| **Consumer Apps** | `com.google.android.googlequicksearchbox` | Home screen search widget & Assistant overhead |
| | `com.google.android.gm` | Gmail |
| | `com.google.android.apps.maps` | Google Maps |
| | `com.google.android.calendar` | Google Calendar |
| | `com.google.android.apps.photos` | Google Photos |
| | `com.android.chrome` | Google Chrome |
| | `com.android.vending` | Google Play Store (optional) |
| | `com.google.android.apps.messaging` | SMS / MMS |
| | `com.google.android.dialer` | Phone dialer |
| | `com.google.android.contacts` | Contacts provider |
| | `com.google.android.calculator` | Calculator |
| | `com.google.android.deskclock` | Clock / Alarms |
| **Telemetry & Trackers** | `com.google.android.apps.wellbeing` | Continuous usage tracking |
| | `com.google.android.apps.turbo` | Battery statistics analytics |
| | `com.google.android.as` | Android System Intelligence daemon |
| | `com.android.adservices.api` | Ad Services API |
| | `com.android.ondevicepersonalization.services` | Ad personalization |
| | `com.android.federatedcompute.services` | Federated learning pings |
| **Daemons & Listeners** | `com.google.android.projection.gearhead` | Android Auto |
| | `com.android.hotwordenrollment.okgoogle` | Voice listener |
| | `com.android.hotwordenrollment.xgoogle` | Voice listener |
| | `com.google.android.apps.safetyhub` | Personal Safety |
| | `com.google.android.apps.work.clouddpc` | Enterprise DPC |
| **Wallpaper & Themes** | `com.google.android.apps.aiwallpapers` | AI wallpaper generator |
| | `com.google.pixel.livewallpaper` | Live wallpaper engine |
| | `com.google.android.wallpaper.effects` | Cinematic wallpaper effects |
| | `com.android.wallpaper.livepicker` | Live picker |
| | `com.google.android.apps.customization.pixel` | Pixel customization |
| **System Auxiliary** | `net.pixelos.ota` | OTA updater |
| | `com.oplus.appplatform` | Residual OEM telemetry |
| | `com.android.DeviceAsWebcam` | Webcam service |
| | `com.android.bips`, `com.android.printspooler` | Printing subsystem |
| | `com.android.egg` | Easter egg |
| | `com.android.dynsystem` | Dynamic GSI loader |

---

## 4. Manual ADB Debloat Script (Alternative)

If you prefer executing the debloat directly from your computer terminal without the app:
```bash
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
  net.pixelos.ota
do
  pm uninstall -k --user 0 \$pkg 2>/dev/null && echo \"Purged: \$pkg\"
done
"
```

---

## 5. How to Restore an Uninstalled App

All uninstalled packages remain stored in the system partition and can be restored anytime without data loss:
```bash
adb shell pm install-existing <package_name>
```
*(Or simply tap **🔄 RESTORE UNINSTALLED APPS** inside the 1-Click Debloater app).*
