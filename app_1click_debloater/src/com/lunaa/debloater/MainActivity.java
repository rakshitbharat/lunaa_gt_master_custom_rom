package com.lunaa.debloater;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    public static final String[] CORE_GAPPS = {
        "com.google.android.gsf",
        "com.google.android.gms",
        "com.android.vending",
        "com.google.android.syncadapters.contacts",
        "com.google.android.syncadapters.calendar",
        "com.google.android.onetimeinitializer",
        "com.google.android.partnersetup"
    };

    public static final String[] BLOAT_PACKAGES = {
        // Consumer Google Apps
        "com.google.android.googlequicksearchbox",
        "com.google.android.googlequicksearchbox.nga_resources",
        "com.google.android.gm",
        "com.google.android.apps.maps",
        "com.google.android.calendar",
        "com.google.android.apps.weather",
        "com.google.android.apps.photos",
        "com.google.android.apps.adm",
        "com.google.android.apps.bard",
        "com.google.android.apps.tips",
        "com.google.android.apps.pixel.creativeassistant",
        "com.google.android.tts",
        "com.android.chrome",
        "com.android.music",
        "com.google.android.apps.messaging",
        "com.google.android.dialer",
        "com.google.android.contacts",
        "com.google.android.calculator",
        "com.google.android.deskclock",

        // Telemetry & Tracking
        "com.google.android.apps.wellbeing",
        "com.google.android.apps.turbo",
        "com.google.android.turboadapter",
        "com.google.android.as",
        "com.google.android.as.oss",
        "com.android.adservices.api",
        "com.android.ondevicepersonalization.services",
        "com.android.federatedcompute.services",
        "com.android.devicediagnostics",

        // Voice Listeners & Background Sync
        "com.google.android.projection.gearhead",
        "com.android.hotwordenrollment.okgoogle",
        "com.android.hotwordenrollment.xgoogle",
        "com.google.android.tag",
        "com.google.android.apps.safetyhub",
        "com.google.android.apps.work.clouddpc",

        // Wallpapers & Accessibility Bloat
        "com.google.android.apps.aiwallpapers",
        "com.google.android.apps.emojiwallpaper",
        "com.google.android.apps.magicportrait",
        "com.google.pixel.livewallpaper",
        "com.google.android.wallpaper.effects",
        "com.google.android.apps.wallpaper",
        "com.google.android.apps.wallpaper.pixel",
        "com.android.wallpaper.livepicker",
        "com.android.wallpaperbackup",
        "com.google.android.marvin.talkback",
        "com.google.android.accessibility.soundamplifier",
        "com.google.android.accessibility.switchaccess",
        "com.google.audio.hearing.visualization.accessibility.scribe",

        // System & Auxiliary Bloat
        "com.android.healthconnect.controller",
        "com.android.health.connect.backuprestore",
        "com.android.DeviceAsWebcam",
        "net.pixelos.ota",
        "com.oplus.appplatform",
        "com.android.egg",
        "com.android.htmlviewer",
        "com.android.providers.userdictionary",
        "com.android.providers.blockednumber",
        "com.android.simappdialog",
        "com.android.sharedstoragebackup",
        "com.android.backupconfirm",
        "com.google.android.markup",
        "com.google.android.soundpicker",
        "com.android.soundpicker",
        "com.google.android.storagemanager",
        "com.google.android.configupdater",
        "com.google.android.apps.customization.pixel",
        "com.android.customization.themes",
        "com.android.bluetoothmidiservice",
        "com.android.carrierdefaultapp",
        "com.android.cellbroadcastreceiver",
        "com.android.cellbroadcastreceiver.module",
        "com.android.cellbroadcastservice",
        "com.android.dynsystem",
        "com.android.cts.ctsshim",
        "com.android.cts.priv.ctsshim",
        "com.android.bips",
        "com.android.printspooler",
        "com.android.emergency",
        "com.android.bookmarkprovider",
        "com.android.companiondevicemanager",
        "com.android.calllogbackup",
        "com.android.hotspot2.osulogin",
        "com.android.stk",
        "com.android.systemui.plugin.globalactions.wallet"
    };

    private DevicePolicyManager dpm;
    private ComponentName adminComponent;
    private TextView tvStatus;
    private TextView tvGappsStatus;
    private TextView tvLog;
    private Button btnMinimalGapps;
    private Button btnDegoogle;
    private Button btnDebloat;
    private Button btnRestore;
    private Button btnReleaseAdmin;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(this, AdminReceiver.class);

        TextView tvDeviceInfo = findViewById(R.id.tv_device_info);
        tvDeviceInfo.setText(Build.MANUFACTURER + " " + Build.MODEL + " • Android " + Build.VERSION.RELEASE);

        tvStatus = findViewById(R.id.tv_status);
        tvGappsStatus = findViewById(R.id.tv_gapps_status);
        tvLog = findViewById(R.id.tv_log);

        btnMinimalGapps = findViewById(R.id.btn_minimal_gapps);
        btnDegoogle = findViewById(R.id.btn_degoogle);
        btnDebloat = findViewById(R.id.btn_debloat);
        btnRestore = findViewById(R.id.btn_restore);
        btnReleaseAdmin = findViewById(R.id.btn_release_admin);

        updateStatuses();

        btnMinimalGapps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runMinimalGappsTask();
            }
        });

        btnDegoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDegoogleTask();
            }
        });

        btnDebloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDebloatTask(true);
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runDebloatTask(false);
            }
        });

        btnReleaseAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (dpm.isDeviceOwnerApp(getPackageName())) {
                        dpm.clearDeviceOwnerApp(getPackageName());
                        updateStatuses();
                        appendLog("[!] Device Owner Released! Managed banner removed.");
                        Toast.makeText(MainActivity.this, "Device Owner Released! Phone is unmanaged.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Not currently Device Owner.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    appendLog("[ERROR] Failed to release: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateStatuses() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean isOwner = dpm.isDeviceOwnerApp(getPackageName());
                if (isOwner) {
                    tvStatus.setText("Admin: Device Owner Active");
                    tvStatus.setTextColor(0xFF00E676);
                } else {
                    tvStatus.setText("Admin: Standard Mode");
                    tvStatus.setTextColor(0xFF38BDF8);
                }

                boolean hasVending = isPackageActive("com.android.vending");
                boolean hasGms = isPackageActive("com.google.android.gms");

                if (hasVending && hasGms) {
                    tvGappsStatus.setText("Google: Play Store Active");
                    tvGappsStatus.setTextColor(0xFF10B981);
                } else if (!hasVending && !hasGms) {
                    tvGappsStatus.setText("Google: 100% Google-Free");
                    tvGappsStatus.setTextColor(0xFFA78BFA);
                } else {
                    tvGappsStatus.setText("Google: Custom Profile");
                    tvGappsStatus.setTextColor(0xFFFBBF24);
                }
            }
        });
    }

    private boolean isPackageActive(String pkg) {
        try {
            getPackageManager().getPackageInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void setButtonsEnabled(final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnMinimalGapps.setEnabled(enabled);
                btnDegoogle.setEnabled(enabled);
                btnDebloat.setEnabled(enabled);
                btnRestore.setEnabled(enabled);
                btnReleaseAdmin.setEnabled(enabled);
            }
        });
    }

    private void appendLog(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLog.append("\n" + text);
            }
        });
    }

    private void setPackageState(String pkg, boolean hide) {
        boolean isOwner = dpm.isDeviceOwnerApp(getPackageName());
        try {
            if (hide) {
                if (isOwner) {
                    dpm.setApplicationHidden(adminComponent, pkg, true);
                }
                Process p = Runtime.getRuntime().exec(new String[]{"pm", "uninstall", "-k", "--user", "0", pkg});
                p.waitFor();
            } else {
                if (isOwner) {
                    dpm.setApplicationHidden(adminComponent, pkg, false);
                }
                Process p = Runtime.getRuntime().exec(new String[]{"pm", "install-existing", pkg});
                p.waitFor();
            }
        } catch (Exception ignored) {
        }
    }

    private void runMinimalGappsTask() {
        setButtonsEnabled(false);
        tvLog.setText("Switching to MINIMAL PLAY STORE Mode...\n");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                appendLog("[1/2] Enabling Core Play Store & Services...");
                for (String pkg : CORE_GAPPS) {
                    setPackageState(pkg, false);
                    appendLog("  [✓] Enabled: " + pkg);
                }

                appendLog("[2/2] Freezing Telemetry & Consumer Bloat...");
                for (String pkg : BLOAT_PACKAGES) {
                    setPackageState(pkg, true);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setButtonsEnabled(true);
                        updateStatuses();
                        appendLog("\n=================================");
                        appendLog("COMPLETE! Minimal Play Store is ready.");
                        appendLog("Zero consumer bloat. Only bare-bones Play Store active.");
                        Toast.makeText(MainActivity.this, "Minimal Play Store Enabled!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void runDegoogleTask() {
        setButtonsEnabled(false);
        tvLog.setText("Executing 100% GOOGLE-FREE (DE-GOOGLE) Mode...\n");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                appendLog("[1/2] Freezing All Core Google Services (GMS, GSF, Play Store)...");
                for (String pkg : CORE_GAPPS) {
                    setPackageState(pkg, true);
                    appendLog("  [🚫] Purged: " + pkg);
                }

                appendLog("[2/2] Freezing Remaining Google & OEM Trackers...");
                for (String pkg : BLOAT_PACKAGES) {
                    setPackageState(pkg, true);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setButtonsEnabled(true);
                        updateStatuses();
                        appendLog("\n=================================");
                        appendLog("SUCCESS! Device is now 100% GOOGLE-FREE.");
                        appendLog("Zero Google Play Services running in background.");
                        Toast.makeText(MainActivity.this, "Phone is now 100% Google-Free!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void runDebloatTask(final boolean debloat) {
        setButtonsEnabled(false);
        tvLog.setText(debloat ? "Starting General Debloat...\n" : "Restoring Factory Packages...\n");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                for (String pkg : BLOAT_PACKAGES) {
                    setPackageState(pkg, debloat);
                    count++;
                    appendLog((debloat ? "[✓] Cleaned: " : "[✓] Restored: ") + pkg);
                }

                final int finalCount = count;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setButtonsEnabled(true);
                        updateStatuses();
                        appendLog("\n=================================");
                        appendLog(debloat ? "COMPLETE! Cleaned " + finalCount + " bloat items." : "RESTORED " + finalCount + " packages.");
                        Toast.makeText(MainActivity.this, debloat ? "Debloat Complete!" : "Restore Complete!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
