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
import com.ffvd.nexus.manager.AppManager;

import java.util.List;

public class AppsFragment {

    public static void build(final MainActivity activity,
                              LinearLayout container) {
        LayoutInflater inf = LayoutInflater.from(activity);

        // ── SECTION: Boot ─────────────────────────────────────────────────
        addSectionHeader(inf, container, "BOOT AUTO START");

        String currentAutoStart = AppManager.getBootAutoStartApp();
        addArrowRow(inf, container, "🚀", "Auto Start App",
            currentAutoStart.isEmpty() ? "No app selected" : currentAutoStart,
            currentAutoStart.isEmpty() ? "None" : "Set",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAppPickerDialog(activity, "Select Auto Start App",
                        new OnAppSelected() {
                            @Override
                            public void onSelected(String pkg) {
                                AppManager.setBootAutoStartApp(pkg);
                            }
                        });
                }
            });

        addArrowRow(inf, container, "🗑️", "Clear Auto Start",
            "Remove boot auto launch",
            "",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.clearBootAutoStart();
                    showToast(activity, "Auto start cleared");
                }
            });

        // ── SECTION: Daemon ───────────────────────────────────────────────
        addSectionHeader(inf, container, "APP DAEMON (KEEP ALIVE)");

        String currentDaemon = AppManager.getDaemonApp();
        addArrowRow(inf, container, "👁️", "Daemon App",
            currentDaemon.isEmpty() ? "No app selected" : currentDaemon,
            currentDaemon.isEmpty() ? "None" : "Set",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAppPickerDialog(activity, "Select Daemon App",
                        new OnAppSelected() {
                            @Override
                            public void onSelected(String pkg) {
                                AppManager.setDaemonApp(pkg);
                            }
                        });
                }
            });

        addArrowRow(inf, container, "⏱️", "Daemon Restart Delay",
            "How long to wait before restarting crashed app",
            AppManager.getDaemonRestartDelay() + "ms",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRestartDelayDialog(activity);
                }
            });

        addArrowRow(inf, container, "🗑️", "Clear Daemon App",
            "Remove app daemon",
            "",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppManager.clearDaemonApp();
                    showToast(activity, "Daemon app cleared");
                }
            });

        // ── SECTION: Keyboard ─────────────────────────────────────────────
        addSectionHeader(inf, container, "INPUT");

        addSwitchRow(inf, container, "⌨️", "Soft Keyboard",
            "Show / hide on-screen keyboard",
            AppManager.isSoftKeyboardEnabled(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean on) {
                    AppManager.setSoftKeyboardEnabled(on);
                    activity.showRebootDialog();
                }
            });
    }

    // ── App picker dialog ─────────────────────────────────────────────────
    private static void showAppPickerDialog(final MainActivity activity,
                                             String title,
                                             final OnAppSelected callback) {
        List<String> pkgs = AppManager.getInstalledUserApps(activity);
        final String[] packages = pkgs.toArray(new String[0]);
        final String[] labels   = new String[packages.length];
        for (int i = 0; i < packages.length; i++) {
            labels[i] = AppManager.getAppLabel(activity, packages[i])
                + "\n" + packages[i];
        }
        new AlertDialog.Builder(activity)
            .setTitle(title)
            .setItems(labels, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface d, int which) {
                    callback.onSelected(packages[which]);
                    d.dismiss();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    // ── Restart delay dialog ──────────────────────────────────────────────
    private static void showRestartDelayDialog(final MainActivity activity) {
        final String[] labels = {"1 second", "3 seconds",
                                  "5 seconds", "10 seconds", "30 seconds"};
        final String[] values = {"1000", "3000", "5000", "10000", "30000"};
        String cur = AppManager.getDaemonRestartDelay();
        int sel = 2;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(cur)) { sel = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle("Restart Delay")
            .setSingleChoiceItems(labels, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        AppManager.setDaemonRestartDelay(values[which]);
                        d.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showToast(MainActivity activity, String msg) {
        android.widget.Toast.makeText(activity, msg,
            android.widget.Toast.LENGTH_SHORT).show();
    }

    interface OnAppSelected {
        void onSelected(String packageName);
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
}