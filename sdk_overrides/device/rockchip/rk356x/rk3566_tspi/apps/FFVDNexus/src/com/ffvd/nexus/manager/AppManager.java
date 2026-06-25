package com.ffvd.nexus.manager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.SystemProperties;

import java.util.ArrayList;
import java.util.List;

public class AppManager {

    // ── Boot auto-start app ───────────────────────────────────────────────
    public static void setBootAutoStartApp(String packageName) {
        SystemProperties.set("persist.sys.openapp", packageName);
    }

    public static String getBootAutoStartApp() {
        return SystemProperties.get("persist.sys.openapp", "");
    }

    public static void clearBootAutoStart() {
        SystemProperties.set("persist.sys.openapp", "");
    }

    // ── Daemon app (keep alive watchdog) ──────────────────────────────────
    public static void setDaemonApp(String packageName) {
        SystemProperties.set("persist.sys.daemonsapp", packageName);
    }

    public static String getDaemonApp() {
        return SystemProperties.get("persist.sys.daemonsapp", "");
    }

    public static void setDaemonRestartDelay(String ms) {
        SystemProperties.set("persist.sys.daemonsapp_time", ms);
    }

    public static String getDaemonRestartDelay() {
        return SystemProperties.get("persist.sys.daemonsapp_time", "5000");
    }

    public static void clearDaemonApp() {
        SystemProperties.set("persist.sys.daemonsapp", "");
    }

    // ── Soft keyboard disable ─────────────────────────────────────────────
    public static void setSoftKeyboardEnabled(boolean enabled) {
        SystemProperties.set("persist.sys.softkeyboard", enabled ? "1" : "0");
    }

    public static boolean isSoftKeyboardEnabled() {
        return !"0".equals(
            SystemProperties.get("persist.sys.softkeyboard", "1"));
    }

    // ── Get installed user apps ───────────────────────────────────────────
    public static List<String> getInstalledUserApps(Context ctx) {
        List<String> packages = new ArrayList<>();
        PackageManager pm = ctx.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(
            PackageManager.GET_META_DATA);
        for (ApplicationInfo app : apps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                packages.add(app.packageName);
            }
        }
        return packages;
    }

    // ── Get app label from package name ───────────────────────────────────
    public static String getAppLabel(Context ctx, String packageName) {
        try {
            PackageManager pm = ctx.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            return pm.getApplicationLabel(info).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }
}