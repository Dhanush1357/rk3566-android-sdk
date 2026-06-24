package com.ffvd.nexus.manager;

import android.os.SystemProperties;

public class CameraManager {

    // ── Back camera rotation ──────────────────────────────────────────────
    public static void setBackCameraRotation(String degrees) {
        SystemProperties.set("persist.sys.displayrot.bcam", degrees);
    }

    public static String getBackCameraRotation() {
        return SystemProperties.get("persist.sys.displayrot.bcam", "0");
    }

    // ── Back camera mirror ────────────────────────────────────────────────
    public static void setBackCameraMirror(boolean enable) {
        SystemProperties.set("persist.hal.bcam.mirror", String.valueOf(enable));
    }

    public static boolean isBackCameraMirrorEnabled() {
        return "true".equals(
            SystemProperties.get("persist.hal.bcam.mirror", "false"));
    }

    // ── Camera rotation sync with display ─────────────────────────────────
    public static void setCameraRotationSyncWithDisplay(boolean sync) {
        SystemProperties.set("persist.sys.camerarot", sync ? "1" : "0");
    }

    public static boolean isCameraRotationSyncEnabled() {
        return "1".equals(
            SystemProperties.get("persist.sys.camerarot", "0"));
    }

    // ── Camera flash ──────────────────────────────────────────────────────
    public static void setCameraFlashEnabled(boolean enable) {
        SystemProperties.set("persist.sys.camera.flash", enable ? "1" : "0");
    }

    public static boolean isCameraFlashEnabled() {
        return "1".equals(
            SystemProperties.get("persist.sys.camera.flash", "1"));
    }

    // ── Camera facing (which camera is default) ───────────────────────────
    public static void setDefaultCamera(String facing) {
        // "0" = back, "1" = front
        SystemProperties.set("persist.sys.camera.facing", facing);
    }

    public static String getDefaultCamera() {
        return SystemProperties.get("persist.sys.camera.facing", "0");
    }
}