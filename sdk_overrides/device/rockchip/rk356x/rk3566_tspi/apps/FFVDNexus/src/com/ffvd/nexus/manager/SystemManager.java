package com.ffvd.nexus.manager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.provider.Settings;

import java.util.Locale;

public class SystemManager {

    // ── Root access ───────────────────────────────────────────────────────
    public static void setRootAccess(String mode) {
        // "0"=disabled, "1"=adb only, "2"=apps only, "3"=both
        SystemProperties.set("persist.sys.root_access", mode);
    }

    public static String getRootAccess() {
        return SystemProperties.get("persist.sys.root_access", "0");
    }

    // ── ADB ───────────────────────────────────────────────────────────────
    public static void setAdbEnabled(Context ctx, boolean enabled) {
        Settings.Global.putInt(ctx.getContentResolver(),
            Settings.Global.ADB_ENABLED, enabled ? 1 : 0);
    }

    public static boolean isAdbEnabled(Context ctx) {
        return Settings.Global.getInt(ctx.getContentResolver(),
            Settings.Global.ADB_ENABLED, 0) == 1;
    }

    // ── Network ADB ───────────────────────────────────────────────────────
    public static void setNetworkAdbEnabled(boolean enabled) {
        SystemProperties.set("persist.internet.adb.enable",
            enabled ? "1" : "0");
    }

    public static boolean isNetworkAdbEnabled() {
        return "1".equals(
            SystemProperties.get("persist.internet.adb.enable", "0"));
    }

    // ── OTG mode ──────────────────────────────────────────────────────────
    public static void setOtgMode(String mode) {
        // "1" = host, "2" = peripheral/device
        SystemProperties.set("persist.sys.usb.otg.mode", mode);
        try {
            String sysfsMode = "1".equals(mode) ? "host" : "peripheral";
            Runtime.getRuntime().exec(new String[]{
                "sh", "-c",
                "echo " + sysfsMode +
                " > /sys/devices/platform/fe8a0000.usb2-phy/otg_mode"
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getOtgMode() {
        return SystemProperties.get("persist.sys.usb.otg.mode", "1");
    }

    // ── Performance mode ──────────────────────────────────────────────────
    public static void setPerformanceMode(String mode) {
        // "0"=normal, "1"=performance, "2"=powersave
        SystemProperties.set("persist.sys.performance", mode);
    }

    public static String getPerformanceMode() {
        return SystemProperties.get("persist.sys.performance", "0");
    }

    // ── Language ──────────────────────────────────────────────────────────
    public static void setSystemLanguage(Context ctx, String language,
                                          String country) {
        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        ctx.getResources().updateConfiguration(config,
            ctx.getResources().getDisplayMetrics());
    }

    // ── Reboot / Shutdown ─────────────────────────────────────────────────
    public static void reboot(Context ctx) {
        PowerManager pm = (PowerManager)
            ctx.getSystemService(Context.POWER_SERVICE);
        pm.reboot(null);
    }

    public static void shutdown(Context ctx) {
        PowerManager pm = (PowerManager)
            ctx.getSystemService(Context.POWER_SERVICE);
        pm.shutdown(false, "userrequested", false);
    }

    // ── Device info ───────────────────────────────────────────────────────
    public static String getDeviceName() {
        return SystemProperties.get("persist.sys.device_name",
            Build.MODEL);
    }

    public static String getBuildVersion() {
        return Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")";
    }

    public static String getBuildId() {
        return Build.DISPLAY;
    }

    public static String getSerialNumber() {
        return SystemProperties.get("ro.serialno", "unknown");
    }

    public static String getBoardPlatform() {
        return SystemProperties.get("ro.board.platform", "unknown");
    }
}