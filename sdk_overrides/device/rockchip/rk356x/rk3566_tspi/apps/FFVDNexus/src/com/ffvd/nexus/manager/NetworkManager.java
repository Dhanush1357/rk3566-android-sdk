package com.ffvd.nexus.manager;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.provider.Settings;

public class NetworkManager {

    // ── WiFi ──────────────────────────────────────────────────────────────
    public static void setWifiEnabled(Context ctx, boolean enabled) {
        WifiManager wm = (WifiManager)
            ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) wm.setWifiEnabled(enabled);
    }

    public static boolean isWifiEnabled(Context ctx) {
        WifiManager wm = (WifiManager)
            ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wm != null && wm.isWifiEnabled();
    }

    public static String getWifiMacAddress(Context ctx) {
        WifiManager wm = (WifiManager)
            ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            WifiInfo info = wm.getConnectionInfo();
            if (info != null) return info.getMacAddress();
        }
        return "unavailable";
    }

    public static String getWifiSsid(Context ctx) {
        WifiManager wm = (WifiManager)
            ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            WifiInfo info = wm.getConnectionInfo();
            if (info != null) {
                String ssid = info.getSSID();
                return ssid != null ? ssid.replace("\"", "") : "Not connected";
            }
        }
        return "Not connected";
    }

    // ── WiFi sleep policy ─────────────────────────────────────────────────
    public static void setWifiSleepPolicy(Context ctx, int policy) {
        // 0=always, 1=plugged, 2=never
        Settings.Global.putInt(ctx.getContentResolver(),
            Settings.Global.WIFI_SLEEP_POLICY, policy);
    }

    public static int getWifiSleepPolicy(Context ctx) {
        return Settings.Global.getInt(ctx.getContentResolver(),
            Settings.Global.WIFI_SLEEP_POLICY,
            Settings.Global.WIFI_SLEEP_POLICY_DEFAULT);
    }

    // ── WiFi watchdog ─────────────────────────────────────────────────────
    public static void setWifiWatchdogEnabled(boolean enabled) {
        SystemProperties.set("persist.sys.wifi_fix", enabled ? "1" : "0");
    }

    public static boolean isWifiWatchdogEnabled() {
        return "1".equals(SystemProperties.get("persist.sys.wifi_fix", "0"));
    }

    // ── Ethernet ──────────────────────────────────────────────────────────
    public static void setEthernetMode(String mode) {
        // "dhcp" or "static"
        SystemProperties.set("persist.sys.eth_fix",
            "static".equals(mode) ? "1" : "0");
    }

    public static String getEthernetMode() {
        return "1".equals(SystemProperties.get("persist.sys.eth_fix", "0"))
            ? "static" : "dhcp";
    }

    public static void setEthernetWatchdog(boolean enabled) {
        SystemProperties.set("persist.sys.eth_watchdog", enabled ? "1" : "0");
    }

    public static boolean isEthernetWatchdogEnabled() {
        return "1".equals(
            SystemProperties.get("persist.sys.eth_watchdog", "0"));
    }

    // ── Airplane mode ─────────────────────────────────────────────────────
    public static void setAirplaneMode(Context ctx, boolean enabled) {
        Settings.Global.putInt(ctx.getContentResolver(),
            Settings.Global.AIRPLANE_MODE_ON, enabled ? 1 : 0);
    }

    public static boolean isAirplaneModeOn(Context ctx) {
        return Settings.Global.getInt(ctx.getContentResolver(),
            Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    }

    // ── Network time sync ─────────────────────────────────────────────────
    public static void setAutoTimeEnabled(Context ctx, boolean enabled) {
        Settings.Global.putInt(ctx.getContentResolver(),
            Settings.Global.AUTO_TIME, enabled ? 1 : 0);
    }

    public static boolean isAutoTimeEnabled(Context ctx) {
        return Settings.Global.getInt(ctx.getContentResolver(),
            Settings.Global.AUTO_TIME, 1) == 1;
    }
}