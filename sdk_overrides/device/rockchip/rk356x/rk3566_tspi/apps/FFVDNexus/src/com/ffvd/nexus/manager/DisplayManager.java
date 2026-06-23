package com.ffvd.nexus.manager;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DisplayManager {

    // ── Main screen rotation ──────────────────────────────────────────────
    public static void setMainScreenRotation(String degrees) {
        SystemProperties.set("persist.sys.displayrot", degrees);
    }

    public static String getMainScreenRotation() {
        return SystemProperties.get("persist.sys.displayrot", "0");
    }

    // ── Secondary screen rotation ─────────────────────────────────────────
    public static void setSecondaryScreenRotation(String degrees) {
        SystemProperties.set("persist.sys.rotation.einit", degrees);
    }

    public static String getSecondaryScreenRotation() {
        return SystemProperties.get("persist.sys.rotation.einit", "0");
    }

    // ── Backlight / Brightness ────────────────────────────────────────────
    public static void setBrightness(Context ctx, int value) {
        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, value);
    }

    public static int getBrightness(Context ctx) {
        try {
            return Settings.System.getInt(ctx.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return 128;
        }
    }

    // ── Screen DPI ────────────────────────────────────────────────────────
    public static void setScreenDensity(String dpi) {
        SystemProperties.set("persist.sys.screen.density", dpi);
    }

    public static String getScreenDensity(Context ctx) {
        String saved = SystemProperties.get("persist.sys.screen.density", "");
        if (!saved.isEmpty()) return saved;
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRealMetrics(dm);
        return String.valueOf(dm.densityDpi);
    }

    // ── Screen timeout ────────────────────────────────────────────────────
    public static void setScreenTimeout(Context ctx, int ms) {
        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, ms);
    }

    public static int getScreenTimeout(Context ctx) {
        try {
            return Settings.System.getInt(ctx.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            return 30000;
        }
    }

    // ── Navigation bar ────────────────────────────────────────────────────
    public static void setNavBarHidden(boolean hide) {
        SystemProperties.set("persist.sys.statebarstate", hide ? "0" : "1");
    }

    public static boolean isNavBarHidden() {
        return "0".equals(SystemProperties.get("persist.sys.statebarstate", "1"));
    }

    // ── Status bar ────────────────────────────────────────────────────────
    public static void setStatusBarHidden(boolean hide) {
        SystemProperties.set("persist.sys.disstatusbar", hide ? "1" : "0");
    }

    public static boolean isStatusBarHidden() {
        return "1".equals(SystemProperties.get("persist.sys.disstatusbar", "0"));
    }

    // ── Notification pulldown ─────────────────────────────────────────────
    public static void setNotificationDisabled(boolean disable) {
        SystemProperties.set("persist.sys.disexpandbar", disable ? "0" : "1");
    }

    public static boolean isNotificationDisabled() {
        return "0".equals(SystemProperties.get("persist.sys.disexpandbar", "1"));
    }

    // ── HDMI resolution ───────────────────────────────────────────────────
    public static void setHdmiResolution(String resolution) {
        SystemProperties.set("persist.sys.hdmimode", resolution);
        SystemProperties.set("persist.vendor.resolution.HDMI-A-0", resolution);
    }

    public static String getHdmiResolution() {
        return SystemProperties.get("persist.sys.hdmimode", "1920x1080@60");
    }

    // ── Screensaver ───────────────────────────────────────────────────────
    public static void setScreensaverEnabled(boolean enabled) {
        SystemProperties.set("persist.sys.screensaver", enabled ? "1" : "0");
    }

    public static boolean isScreensaverEnabled() {
        return "1".equals(SystemProperties.get("persist.sys.screensaver", "0"));
    }

    // ── Font scale ────────────────────────────────────────────────────────
    public static void setFontScale(Context ctx, float scale) {
        Settings.System.putFloat(ctx.getContentResolver(),
                Settings.System.FONT_SCALE, scale);
    }

    public static float getFontScale(Context ctx) {
        return Settings.System.getFloat(ctx.getContentResolver(),
                Settings.System.FONT_SCALE, 1.0f);
    }
}