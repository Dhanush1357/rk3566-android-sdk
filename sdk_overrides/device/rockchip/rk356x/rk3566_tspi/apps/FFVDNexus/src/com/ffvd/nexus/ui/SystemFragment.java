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
import com.ffvd.nexus.manager.SystemManager;

public class SystemFragment {

    public static void build(final MainActivity activity,
                              LinearLayout container) {
        LayoutInflater inf = LayoutInflater.from(activity);

        // ── SECTION: Developer ────────────────────────────────────────────
        addSectionHeader(inf, container, "DEVELOPER");

        addSwitchRow(inf, container, "🐛", "ADB Debugging",
            "Enable USB ADB debugging",
            SystemManager.isAdbEnabled(activity),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean on) {
                    SystemManager.setAdbEnabled(activity, on);
                }
            });

        addSwitchRow(inf, container, "🌐", "Network ADB",
            "Enable ADB over TCP/IP (port 5555)",
            SystemManager.isNetworkAdbEnabled(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean on) {
                    SystemManager.setNetworkAdbEnabled(on);
                }
            });

        addArrowRow(inf, container, "🔐", "Root Access",
            "Control root permission level",
            getRootLabel(SystemManager.getRootAccess()),
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRootDialog(activity);
                }
            });

        // ── SECTION: USB / OTG ────────────────────────────────────────────
        addSectionHeader(inf, container, "USB / OTG");

        addArrowRow(inf, container, "🔌", "OTG Mode",
            "Set USB OTG as host or peripheral",
            getOtgLabel(SystemManager.getOtgMode()),
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOtgDialog(activity);
                }
            });

        // ── SECTION: Performance ──────────────────────────────────────────
        addSectionHeader(inf, container, "PERFORMANCE");

        addArrowRow(inf, container, "⚡", "Performance Mode",
            "CPU governor mode",
            getPerfLabel(SystemManager.getPerformanceMode()),
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPerformanceDialog(activity);
                }
            });

        // ── SECTION: Power ────────────────────────────────────────────────
        addSectionHeader(inf, container, "POWER");

        addArrowRow(inf, container, "🔄", "Reboot Device",
            "Restart the system now",
            "",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(activity)
                        .setTitle("Reboot")
                        .setMessage("Reboot the device now?")
                        .setPositiveButton("Reboot", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int w) {
                                SystemManager.reboot(activity);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            });

        addArrowRow(inf, container, "⏹️", "Shutdown",
            "Power off the device",
            "",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(activity)
                        .setTitle("Shutdown")
                        .setMessage("Shutdown the device now?")
                        .setPositiveButton("Shutdown", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int w) {
                                SystemManager.shutdown(activity);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                }
            });

        // ── SECTION: Device Info ──────────────────────────────────────────
        addSectionHeader(inf, container, "DEVICE INFO");

        addInfoRow(inf, container, "📱", "Device Name",
            SystemManager.getDeviceName());
        addInfoRow(inf, container, "🤖", "Android Version",
            SystemManager.getBuildVersion());
        addInfoRow(inf, container, "🏷️", "Build ID",
            SystemManager.getBuildId());
        addInfoRow(inf, container, "🔢", "Serial Number",
            SystemManager.getSerialNumber());
        addInfoRow(inf, container, "🔧", "Board Platform",
            SystemManager.getBoardPlatform());
    }

    // ── Dialogs ───────────────────────────────────────────────────────────
    private static void showRootDialog(final MainActivity activity) {
        final String[] labels = {
            "Disabled", "ADB Only", "Apps Only", "ADB + Apps"};
        final String[] values = {"0", "1", "2", "3"};
        String cur = SystemManager.getRootAccess();
        int sel = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(cur)) { sel = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle("Root Access")
            .setSingleChoiceItems(labels, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        SystemManager.setRootAccess(values[which]);
                        activity.showRebootDialog();
                        d.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showOtgDialog(final MainActivity activity) {
        final String[] labels = {"Host (connect USB devices)",
                                  "Peripheral (connect to PC)"};
        final String[] values = {"1", "2"};
        String cur = SystemManager.getOtgMode();
        int sel = "2".equals(cur) ? 1 : 0;
        new AlertDialog.Builder(activity)
            .setTitle("OTG Mode")
            .setSingleChoiceItems(labels, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        SystemManager.setOtgMode(values[which]);
                        activity.showRebootDialog();
                        d.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showPerformanceDialog(final MainActivity activity) {
        final String[] labels = {"Normal", "Performance", "Power Save"};
        final String[] values = {"0", "1", "2"};
        String cur = SystemManager.getPerformanceMode();
        int sel = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(cur)) { sel = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle("Performance Mode")
            .setSingleChoiceItems(labels, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        SystemManager.setPerformanceMode(values[which]);
                        activity.showRebootDialog();
                        d.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    // ── Label helpers ─────────────────────────────────────────────────────
    private static String getRootLabel(String val) {
        switch (val) {
            case "1": return "ADB Only";
            case "2": return "Apps Only";
            case "3": return "ADB + Apps";
            default:  return "Disabled";
        }
    }

    private static String getOtgLabel(String val) {
        return "2".equals(val) ? "Peripheral" : "Host";
    }

    private static String getPerfLabel(String val) {
        switch (val) {
            case "1": return "Performance";
            case "2": return "Power Save";
            default:  return "Normal";
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