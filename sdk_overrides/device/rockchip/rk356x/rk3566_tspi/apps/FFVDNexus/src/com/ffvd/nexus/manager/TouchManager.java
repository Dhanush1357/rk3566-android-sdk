package com.ffvd.nexus.manager;

import android.os.SystemProperties;

public class TouchManager {

    // ── Touch rotation ────────────────────────────────────────────────────
    public static void setTouchRotation(String angle) {
        // null = default, 90, 180, 270
        SystemProperties.set("persist.sys.touch.angle", angle);
    }

    public static String getTouchRotation() {
        return SystemProperties.get("persist.sys.touch.angle", "0");
    }

    // ── Touch swap XY axis ────────────────────────────────────────────────
    public static void setTouchSwapXY(boolean swap) {
        SystemProperties.set("persist.sys.touch.swap_xy", swap ? "1" : "0");
    }

    public static boolean isTouchSwapXY() {
        return "1".equals(SystemProperties.get("persist.sys.touch.swap_xy", "0"));
    }

    // ── Touch invert X ────────────────────────────────────────────────────
    public static void setTouchInvertX(boolean invert) {
        SystemProperties.set("persist.sys.touch.invert_x", invert ? "1" : "0");
    }

    public static boolean isTouchInvertX() {
        return "1".equals(SystemProperties.get("persist.sys.touch.invert_x", "0"));
    }

    // ── Touch invert Y ────────────────────────────────────────────────────
    public static void setTouchInvertY(boolean invert) {
        SystemProperties.set("persist.sys.touch.invert_y", invert ? "1" : "0");
    }

    public static boolean isTouchInvertY() {
        return "1".equals(SystemProperties.get("persist.sys.touch.invert_y", "0"));
    }

    // ── Touch sensitivity ─────────────────────────────────────────────────
    public static void setTouchEnabled(boolean enabled) {
        SystemProperties.set("persist.sys.touch.enabled", enabled ? "1" : "0");
    }

    public static boolean isTouchEnabled() {
        return !"0".equals(SystemProperties.get("persist.sys.touch.enabled", "1"));
    }

    // ── Read current touch device info ────────────────────────────────────
    public static String getTouchDeviceInfo() {
        return SystemProperties.get("ro.hardware.touch", "unknown");
    }
}