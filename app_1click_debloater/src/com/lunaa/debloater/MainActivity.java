package com.lunaa.debloater;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

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
        "com.android.vending",

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
        "com.google.android.onetimeinitializer",
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
        "com.android.systemui.plugin.globalactions.wallet",
        "com.google.android.partnersetup"
    };

    private DevicePolicyManager dpm;
    private ComponentName adminComponent;
    private TextView tvStatus;
    private TextView tvLog;
    private Button btnDebloat;
    private Button btnRestore;
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
        tvLog = findViewById(R.id.tv_log);
        btnDebloat = findViewById(R.id.btn_debloat);
        btnRestore = findViewById(R.id.btn_restore);

        updateAdminStatus();

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
    }

    private void updateAdminStatus() {
        boolean isOwner = dpm.isDeviceOwnerApp(getPackageName());
        if (isOwner) {
            tvStatus.setText("Status: Enterprise Device Owner Active (Full Access)");
            tvStatus.setTextColor(0xFF00E676);
        } else {
            tvStatus.setText("Status: Standard Mode Active");
            tvStatus.setTextColor(0xFF38BDF8);
        }
    }

    private void appendLog(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLog.append("\n" + text);
            }
        });
    }

    private void runDebloatTask(final boolean debloat) {
        btnDebloat.setEnabled(false);
        btnRestore.setEnabled(false);
        tvLog.setText(debloat ? "Starting 1-Click Debloat...\n" : "Restoring Packages...\n");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                boolean isOwner = dpm.isDeviceOwnerApp(getPackageName());

                for (String pkg : BLOAT_PACKAGES) {
                    boolean success = false;
                    try {
                        if (debloat) {
                            if (isOwner) {
                                dpm.setApplicationHidden(adminComponent, pkg, true);
                                success = true;
                            }
                            Process p = Runtime.getRuntime().exec(new String[]{"pm", "uninstall", "-k", "--user", "0", pkg});
                            p.waitFor();
                            if (p.exitValue() == 0) success = true;
                        } else {
                            if (isOwner) {
                                dpm.setApplicationHidden(adminComponent, pkg, false);
                                success = true;
                            }
                            Process p = Runtime.getRuntime().exec(new String[]{"pm", "install-existing", pkg});
                            p.waitFor();
                            if (p.exitValue() == 0) success = true;
                        }
                    } catch (Exception ignored) {
                    }

                    if (success) {
                        count++;
                        appendLog((debloat ? "[✓] Cleaned: " : "[✓] Restored: ") + pkg);
                    }
                }

                final int finalCount = count;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnDebloat.setEnabled(true);
                        btnRestore.setEnabled(true);
                        appendLog("\n=================================");
                        appendLog(debloat ? "COMPLETE! Total items purged: " + finalCount : "RESTORE FINISHED! Restored: " + finalCount);
                        Toast.makeText(MainActivity.this, debloat ? "Device Cleaned! " + finalCount + " items removed." : "Restored " + finalCount + " packages.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
