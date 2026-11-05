# PixelOS Installation Guide for Realme GT Master Edition (`lunaa`)

This guide covers the complete, official-grade installation process of **PixelOS (Android 15 / Android 16)** on the **Realme GT Master Edition** (model numbers `RMX3360`, `RMX3363`, `RMX3361`).

---

## 1. Hardware & Platform Specifications

| Specification | Detail |
| :--- | :--- |
| **Device Model** | Realme GT Master Edition (`RMX3360` / `RMX3363` / `RMX3361`) |
| **Device Codename** | `lunaa` |
| **Platform / SoC** | Qualcomm Snapdragon 778G 5G (`SM7325` / `lahaina` family) |
| **Partition Scheme** | Virtual A/B with Dynamic Partitions (`super`) |
| **Required Firmware Baseline** | Stock **Realme UI 4.0 (Android 13, F.13 or newer)** |

> [!IMPORTANT]
> Never flash PixelOS or any AOSP ROM on top of old Android 11 or Android 12 firmware. Proprietary vendor partitions (`modem`, `dsp`, `bluetooth`, `radio`) must be updated to the latest official Realme UI 4.0 baseline first.

---

## 2. Official Downloads

Always download verified official builds from the official maintainer repository on SourceForge:

| Component | Target Version | Official SourceForge Link |
| :--- | :--- | :--- |
| **PixelOS ROM ZIP** | Android 16 (`16.2`) | [Download ROM Package](https://sourceforge.net/projects/pixelos-releases/files/sixteen/lunaa/) |
| **PixelOS Recovery Images** | `boot`, `vendor_boot`, `dtbo` | [Download Recovery Partition Images](https://sourceforge.net/projects/pixelos-releases/files/sixteen/lunaa/recovery/) |
| **Alternative (Android 15)**| PixelOS Android 15 | [Download Android 15 Release](https://sourceforge.net/projects/pixelos-releases/files/fifteen/lunaa/) |

---

## 3. Preparation & Prerequisites

1. **Unlock Bootloader:**
   * Install the official **Realme Deep Testing APK** for Realme UI 4.0 on your phone.
   * Apply for in-depth test, wait for approval, and tap **Start in-depth test**.
   * The phone boots to fastboot with text: `DEVICE STATE - unlocked`.
2. **Install Platform Tools on Host:**
   * **macOS:** `brew install android-platform-tools`
   * **Linux:** `sudo apt install android-tools-adb android-tools-fastboot`
   * **Windows:** Download official Google SDK Platform-Tools.

---

## 4. Step-by-Step Flashing Procedure

### Step 4.1: Connect in Fastboot Mode
Turn off your phone, then hold **Power + Volume Down** until the Fastboot logo appears. Connect to your computer via USB.

Verify the connection:
```bash
fastboot devices
```
*(Your phone's serial number will be displayed alongside `fastboot`)*.

### Step 4.2: Flash Kernel & Recovery Partitions
Flash the matching recovery partition images:
```bash
fastboot flash boot boot-lunaa_*.img
fastboot flash vendor_boot vendor_boot-lunaa_*.img
fastboot flash dtbo dtbo-lunaa_*.img
```

### Step 4.3: Boot into PixelOS Recovery
```bash
fastboot reboot recovery
```

### Step 4.4: Format Userdata (Crucial)
1. In the recovery menu on your phone, use Volume Up/Down to navigate and Power to select:
   * Select **Factory reset** -> **Format data/factory reset** -> Confirm **Format data**.
2. Wait a few seconds until the screen prints: `Data wipe complete.`

### Step 4.5: Sideload the ROM Package
1. On the recovery main menu, select **Apply update** -> **Apply from ADB**.
2. The phone screen will say: `Now send the package you want to apply to the device with "adb sideload <filename>"...`
3. On your computer terminal, run:
```bash
adb sideload PixelOS_lunaa-*.zip
```
4. Wait 3–6 minutes until terminal reports: `Total xfer: 1.00x`.

### Step 4.6: Reboot to System
On the recovery menu, tap **Reboot system now**.

---

## 5. First Boot Expectations
* The initial boot on Android 16 takes about **2 to 3 minutes** as Android initializes encryption keys and ART runtime dex caches.
* You will be greeted by the Google Pixel setup wizard.
