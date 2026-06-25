package com.ffvd.nexus.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ffvd.nexus.MainActivity;
import com.ffvd.nexus.R;
import com.ffvd.nexus.manager.NetworkManager;

public class NetworkFragment {

    public static void build(final MainActivity activity,
                              LinearLayout container) {
        LayoutInflater inf = LayoutInflater.from(activity);

        // ── SECTION: WiFi ─────────────────────────────────────────────────
        addSectionHeader(inf, container, "WIFI");

        addSwitchRow(inf, container, "📶", "WiFi",
            "Enable or disable WiFi",
            NetworkManager.isWifiEnabled(activity),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean on) {
                    NetworkManager.setWifiEnabled(activity, on);
                }
            });

        addInfoRow(inf, container, "🔗", "Connected SSID",
            NetworkManager.getWifiSsid(activity));

        addInfoRow(inf, container, "📋", "WiFi MAC Address",
            NetworkManager.getWifiMacAddress(activity));

        addArrowRow(inf, container, "💤", "WiFi Sleep Policy",
            "When to disconnect WiFi during sleep",
            getWifiSleepLabel(NetworkManager.getWifiSleepPolicy(activity)),
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWifiSleepDialog(activity);
                }
            });

        addSwitchRow(inf, container, "🐕", "WiFi Watchdog",
            "Auto reconnect if WiFi drops",
            NetworkManager.isWifiWatchdogEnabled(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean on) {
                    NetworkManager.setWifiWatchdogEnabled(on);
                }
            });

        // ── SECTION: Ethernet ─────────────────────────────────────────────
        addSectionHeader(inf, container, "ETHERNET");

        addArrowRow(inf, container, "🌐", "Ethernet Mode",
            "DHCP = auto IP, Static = fixed IP",
            NetworkManager.getEthernetMode().toUpperCase(),
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEthernetModeDialog(activity);
                }
            });

        addSwitchRow(inf, container, "🐕", "Ethernet Watchdog",
            "Auto reconnect if Ethernet drops",
            NetworkManager.isEthernetWatchdogEnabled(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean on) {
                    NetworkManager.setEthernetWatchdog(on);
                }
            });

        // ── SECTION: General ──────────────────────────────────────────────
        addSectionHeader(inf, container, "GENERAL");

        addSwitchRow(inf, container, "✈️", "Airplane Mode",
            "Disable all wireless connections",
            NetworkManager.isAirplaneModeOn(activity),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean on) {
                    NetworkManager.setAirplaneMode(activity, on);
                }
            });

        addSwitchRow(inf, container, "🕐", "Network Time Sync",
            "Auto sync time from internet",
            NetworkManager.isAutoTimeEnabled(activity),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean on) {
                    NetworkManager.setAutoTimeEnabled(activity, on);
                }
            });
    }

    // ── Dialogs ───────────────────────────────────────────────────────────
    private static void showWifiSleepDialog(final MainActivity activity) {
        final String[] labels = {"Always keep on",
                                  "Keep on during charging",
                                  "Never keep on"};
        final int[] values = {0, 1, 2};
        int cur = NetworkManager.getWifiSleepPolicy(activity);
        new AlertDialog.Builder(activity)
            .setTitle("WiFi Sleep Policy")
            .setSingleChoiceItems(labels, cur,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        NetworkManager.setWifiSleepPolicy(activity, values[which]);
                        d.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showEthernetModeDialog(final MainActivity activity) {
        final String[] options = {"DHCP (Auto IP)", "Static (Fixed IP)"};
        final String[] values  = {"dhcp", "static"};
        String cur = NetworkManager.getEthernetMode();
        int sel = "static".equals(cur) ? 1 : 0;
        new AlertDialog.Builder(activity)
            .setTitle("Ethernet Mode")
            .setSingleChoiceItems(options, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        NetworkManager.setEthernetMode(values[which]);
                        activity.showRebootDialog();
                        d.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    // ── Label helper ──────────────────────────────────────────────────────
    private static String getWifiSleepLabel(int policy) {
        switch (policy) {
            case 0:  return "Always on";
            case 1:  return "Plugged only";
            case 2:  return "Never";
            default: return "Default";
        }
    }

    // ── View helpers ──────────────────────────────────────────────────────
    private static void addSectionHeader(LayoutInflater inf,
            LinearLayout c, String title) {
        View v = inf.inflate(R.layout.item_section_header, c, false);
        ((TextView) v.findViewById(R.id.section_title)).setText(title);
        c.addView(v);
    }

    private static void addArrowRow(LayoutInflater inf, LinearLayout c,
            String icon, String title, String summary, String value,
            View.OnClickListener l) {
        View v = inf.inflate(R.layout.item_setting_arrow, c, false);
        ((TextView) v.findViewById(R.id.setting_icon)).setText(icon);
        ((TextView) v.findViewById(R.id.setting_title)).setText(title);
        ((TextView) v.findViewById(R.id.setting_summary)).setText(summary);
        ((TextView) v.findViewById(R.id.setting_value)).setText(value);
        v.setOnClickListener(l);
        c.addView(v);
    }

    private static void addSwitchRow(LayoutInflater inf, LinearLayout c,
            String icon, String title, String summary, boolean checked,
            CompoundButton.OnCheckedChangeListener l) {
        View v = inf.inflate(R.layout.item_setting_switch, c, false);
        ((TextView) v.findViewById(R.id.setting_icon)).setText(icon);
        ((TextView) v.findViewById(R.id.setting_title)).setText(title);
        ((TextView) v.findViewById(R.id.setting_summary)).setText(summary);
        Switch sw = v.findViewById(R.id.setting_switch);
        sw.setChecked(checked);
        sw.setOnCheckedChangeListener(l);
        c.addView(v);
    }

    private static void addInfoRow(LayoutInflater inf, LinearLayout c,
            String icon, String title, String value) {
        View v = inf.inflate(R.layout.item_setting_arrow, c, false);
        ((TextView) v.findViewById(R.id.setting_icon)).setText(icon);
        ((TextView) v.findViewById(R.id.setting_title)).setText(title);
        ((TextView) v.findViewById(R.id.setting_summary)).setText("Read only");
        ((TextView) v.findViewById(R.id.setting_value)).setText(value);
        c.addView(v);
    }
}