# Realme GT Master Edition (`lunaa`) — Custom ROM & Developer Clean Suite

[![Android Version](https://img.shields.io/badge/Android-16%20%7C%2015-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://pixelos.net)
[![Device Codename](https://img.shields.io/badge/Device-lunaa%20(RMX3360%2F63)-0284C7?style=for-the-badge&logo=qualcomm&logoColor=white)](https://xdaforums.com/c/realme-gt-master-edition.12353/)
[![SoC](https://img.shields.io/badge/SoC-Snapdragon%20778G%205G-E11D48?style=for-the-badge)](https://www.qualcomm.com/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

An official-grade, end-to-end repository for running modern Android (Android 15 / 16 via PixelOS and crDroid) on the **Realme GT Master Edition** (`lunaa`), bundled with:
1. **Comprehensive Installation Documentation:** Verified flashing protocols from macOS and Linux.
2. **1-Click Debloater Native App:** Complete Java source code + pre-built signed APK to strip 65+ telemetry, Google background sync, and bloatware services with one tap.
3. **AI Agent Skills:** Production `.agent` / Antigravity skill definitions enabling AI coding assistants to autonomously detect, flash, recover, and benchmark `lunaa` devices.

---

## 📂 Project Architecture

```
lunaa_gt_master_custom_rom/
├── .gitignore
├── README.md
├── LICENSE
├── docs/
│   ├── PIXELOS_INSTALLATION_GUIDE.md  # Step-by-step flashing instructions
│   ├── DEBLOATING_GUIDE.md            # In-depth package purge matrix & benchmarks
│   └── TROUBLESHOOTING.md             # Recovery, fastboot & bootloop fixes
├── app_1click_debloater/               # Native Android 1-Click Debloater Companion
│   ├── AndroidManifest.xml
│   ├── build.gradle                   # Android Studio Gradle configuration
│   ├── build_apk.sh                   # 1-second CLI build tool (aapt2 + d8)
│   ├── bin/
│   │   └── Debloater.apk              # Pre-compiled, signed production APK (21 KB)
│   ├── res/                           # Minimalist dark-mode UI
│   └── src/com/lunaa/debloater/
│       ├── AdminReceiver.java         # Enterprise Device Admin Receiver
│       └── MainActivity.java          # 65+ package purge engine
└── skills/
    └── lunaa-rom-management/
        └── SKILL.md                   # AI Agent autonomous flashing & management skill
```

---

## ⚡ Quick Start: Flashing PixelOS

### 1. Download Official Releases
* **PixelOS Android 16 (or 15):** [SourceForge Releases Mirror](https://sourceforge.net/projects/pixelos-releases/files/sixteen/lunaa/)
* **Recovery Partition Images:** `boot.img`, `vendor_boot.img`, and `dtbo.img` from the matching [`recovery/` folder](https://sourceforge.net/projects/pixelos-releases/files/sixteen/lunaa/recovery/).

### 2. Flash Recovery via Fastboot
Put your device in Fastboot Mode (`Power + Volume Down`) and connect via USB:
```bash
fastboot flash boot boot-lunaa_*.img
fastboot flash vendor_boot vendor_boot-lunaa_*.img
fastboot flash dtbo dtbo-lunaa_*.img
fastboot reboot recovery
```

### 3. Format Data & Sideload
1. In Recovery: **Factory reset** -> **Format data/factory reset** -> **Format data**.
2. In Recovery: **Apply update** -> **Apply from ADB**.
3. On host computer:
   ```bash
   adb sideload PixelOS_lunaa-*.zip
   ```
4. Once finished (`Total xfer: 1.00x`), select **Reboot system now**.

👉 *Read the full [PixelOS Installation Guide](docs/PIXELOS_INSTALLATION_GUIDE.md) for detailed prerequisites.*

---

## 📱 1-Click Debloater Companion App

The project includes a 21 KB native app designed for developer testbenches.

### How It Works
* Utilizes Android Enterprise **Device Owner** (`DevicePolicyManager.setApplicationHidden`) combined with userland package manager commands.
* Freezes and removes all 65+ background trackers, telemetry daemons, and unneeded consumer Google apps **without needing root or Shizuku**.
* Features a one-tap **🔄 RESTORE** function to instantly un-hide any app if needed.

### Installation
```bash
# 1. Install APK
adb install -r app_1click_debloater/bin/Debloater.apk

# 2. Grant Enterprise Device Owner
adb shell dpm set-device-owner com.lunaa.debloater/.AdminReceiver
```

### Building From Source
Build the APK in under 2 seconds without launching Android Studio:
```bash
./app_1click_debloater/build_apk.sh
```

👉 *Read the [Debloating Guide](docs/DEBLOATING_GUIDE.md) for the complete list of purged packages and battery benchmarks.*

---

## 🤖 AI Agent Skill Integration

This repository includes an autonomous agent skill definition in [`skills/lunaa-rom-management/SKILL.md`](skills/lunaa-rom-management/SKILL.md).

AI coding assistants (Google Antigravity, Claude, Cursor, Windsurf) can read this skill to autonomously:
* Inspect connected hardware via Fastboot and ADB.
* Flash partition sets and verify slot integrity.
* Perform automated testbench debloats and RAM profiling.

---

## 📊 Post-Debloat System Benchmarks

| Metric | Stock Realme UI 4.0 | PixelOS 16 (Post-Debloat) | Improvement |
| :--- | :--- | :--- | :--- |
| **Available Free RAM** | ~2.1 GB / 8 GB | **> 4.5 GB / 8 GB** | **+114% Available Memory** |
| **Active Background Processes** | ~110+ processes | **< 28 core services** | **-75% Process Overhead** |
| **CPU Idle Activity** | 78% – 85% | **> 98% Idle** | **Near-zero background draw** |
| **Launcher Apps** | 60+ apps & store bloat | **4 core apps** (Settings, Files, Camera, Dolby) | **Clean slate** |

---

## ⚖️ License & Acknowledgements

* **License:** [MIT License](LICENSE)
* **Acknowledgements:**
  * **PixelOS Team & Muhammad Nauman** for official `lunaa` maintenance.
  * **crDroid Team & Quallenauge** for the unified OPlus `sm8350-common` tree foundation.
  * **LineageOS Project** for upstream Android hardware HAL abstractions.
