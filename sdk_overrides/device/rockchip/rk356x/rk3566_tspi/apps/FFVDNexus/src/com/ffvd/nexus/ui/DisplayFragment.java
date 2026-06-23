package com.ffvd.nexus.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.ffvd.nexus.MainActivity;
import com.ffvd.nexus.R;
import com.ffvd.nexus.manager.DisplayManager;

public class DisplayFragment {

    public static void build(final MainActivity activity, LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(activity);

        // ── SECTION: Rotation ──────────────────────────────────────
        addSectionHeader(inflater, container, "ROTATION");

        // Main screen rotation
        addArrowRow(inflater, container, "🔄", "Main Screen Rotation",
                "Current: " + DisplayManager.getMainScreenRotation() + "°",
                DisplayManager.getMainScreenRotation() + "°",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRotationDialog(activity, container,
                            "Main Screen Rotation",
                            DisplayManager.getMainScreenRotation(),
                            new OnValueSelected() {
                                @Override
                                public void onSelected(String value) {
                                    DisplayManager.setMainScreenRotation(value);
                                    activity.showRebootDialog();
                                }
                            });
                    }
                });

        // Secondary screen rotation
        addArrowRow(inflater, container, "🔃", "Secondary Screen Rotation",
                "Sub-display angle",
                DisplayManager.getSecondaryScreenRotation() + "°",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRotationDialog(activity, container,
                            "Secondary Screen Rotation",
                            DisplayManager.getSecondaryScreenRotation(),
                            new OnValueSelected() {
                                @Override
                                public void onSelected(String value) {
                                    DisplayManager.setSecondaryScreenRotation(value);
                                    activity.showRebootDialog();
                                }
                            });
                    }
                });

        // ── SECTION: Brightness & Display ─────────────────────────
        addSectionHeader(inflater, container, "BRIGHTNESS & DISPLAY");

        // Backlight seekbar row
        addBrightnessRow(inflater, container, activity);

        // Screen DPI
        addArrowRow(inflater, container, "📐", "Screen DPI",
                "Display density",
                DisplayManager.getScreenDensity(activity),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDpiDialog(activity, container);
                    }
                });

        // Screen timeout
        addArrowRow(inflater, container, "⏱", "Screen Timeout",
                "Auto sleep timer",
                getTimeoutLabel(DisplayManager.getScreenTimeout(activity)),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimeoutDialog(activity, container);
                    }
                });

        // Font size
        addArrowRow(inflater, container, "🔤", "Font Size",
                "Adjust system text size",
                getFontLabel(DisplayManager.getFontScale(activity)),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFontDialog(activity, container);
                    }
                });

        // ── SECTION: Interface ────────────────────────────────────
        addSectionHeader(inflater, container, "INTERFACE");

        // Hide nav bar
        addSwitchRow(inflater, container, "🧭", "Hide Navigation Bar",
                "Remove bottom navigation bar",
                DisplayManager.isNavBarHidden(),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton btn, boolean checked) {
                        DisplayManager.setNavBarHidden(checked);
                    }
                });

        // Hide status bar
        addSwitchRow(inflater, container, "📶", "Hide Status Bar",
                "Remove top status bar",
                DisplayManager.isStatusBarHidden(),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton btn, boolean checked) {
                        DisplayManager.setStatusBarHidden(checked);
                    }
                });

        // Disable notification pulldown
        addSwitchRow(inflater, container, "🔔", "Disable Notification Pulldown",
                "Lock the drop-down bar",
                DisplayManager.isNotificationDisabled(),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton btn, boolean checked) {
                        DisplayManager.setNotificationDisabled(checked);
                    }
                });

        // ── SECTION: HDMI ─────────────────────────────────────────
        addSectionHeader(inflater, container, "HDMI OUTPUT");

        addArrowRow(inflater, container, "🖥", "HDMI Resolution",
                "External display resolution",
                DisplayManager.getHdmiResolution(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showHdmiDialog(activity, container);
                    }
                });

        // ── SECTION: Screensaver ──────────────────────────────────
        addSectionHeader(inflater, container, "SCREENSAVER");

        addSwitchRow(inflater, container, "🌙", "Screensaver",
                "Enable idle screensaver",
                DisplayManager.isScreensaverEnabled(),
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton btn, boolean checked) {
                        DisplayManager.setScreensaverEnabled(checked);
                    }
                });
    }

    // ── Helper: section header ────────────────────────────────────────────
    private static void addSectionHeader(LayoutInflater inf,
                                          LinearLayout container, String title) {
        View v = inf.inflate(R.layout.item_section_header, container, false);
        ((TextView) v.findViewById(R.id.section_title)).setText(title);
        container.addView(v);
    }

    // ── Helper: arrow row (tap to choose) ────────────────────────────────
    private static void addArrowRow(LayoutInflater inf, LinearLayout container,
                                     String icon, String title, String summary,
                                     String value, View.OnClickListener listener) {
        View v = inf.inflate(R.layout.item_setting_arrow, container, false);
        ((TextView) v.findViewById(R.id.setting_icon)).setText(icon);
        ((TextView) v.findViewById(R.id.setting_title)).setText(title);
        ((TextView) v.findViewById(R.id.setting_summary)).setText(summary);
        ((TextView) v.findViewById(R.id.setting_value)).setText(value);
        v.setOnClickListener(listener);
        container.addView(v);
    }

    // ── Helper: switch row ────────────────────────────────────────────────
    private static void addSwitchRow(LayoutInflater inf, LinearLayout container,
                                      String icon, String title, String summary,
                                      boolean checked,
                                      CompoundButton.OnCheckedChangeListener listener) {
        View v = inf.inflate(R.layout.item_setting_switch, container, false);
        ((TextView) v.findViewById(R.id.setting_icon)).setText(icon);
        ((TextView) v.findViewById(R.id.setting_title)).setText(title);
        ((TextView) v.findViewById(R.id.setting_summary)).setText(summary);
        Switch sw = v.findViewById(R.id.setting_switch);
        sw.setChecked(checked);
        sw.setOnCheckedChangeListener(listener);
        container.addView(v);
    }

    // ── Helper: brightness seekbar row ────────────────────────────────────
    private static void addBrightnessRow(LayoutInflater inf,
                                          LinearLayout container,
                                          final MainActivity activity) {
        View v = inf.inflate(R.layout.item_setting_arrow, container, false);
        ((TextView) v.findViewById(R.id.setting_icon)).setText("☀️");
        ((TextView) v.findViewById(R.id.setting_title)).setText("Backlight");
        int cur = DisplayManager.getBrightness(activity);
        final TextView summary = v.findViewById(R.id.setting_summary);
        summary.setText("Brightness: " + Math.round(cur / 2.55f) + "%");
        ((TextView) v.findViewById(R.id.setting_value)).setText(cur + "");
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBrightnessDialog(activity);
            }
        });
        container.addView(v);
    }

    // ── Dialogs ───────────────────────────────────────────────────────────

    private static void showRotationDialog(final MainActivity activity,
                                            final LinearLayout container,
                                            String title,
                                            String current,
                                            final OnValueSelected callback) {
        final String[] options = {"0°", "90°", "180°", "270°"};
        final String[] values  = {"0",  "90",  "180",  "270"};
        int selected = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(current)) { selected = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle(title)
            .setSingleChoiceItems(options, selected,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onSelected(values[which]);
                        dialog.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showDpiDialog(final MainActivity activity,
                                       final LinearLayout container) {
        final String[] labels = {"120  (small)", "160  (normal)", "213  (large)",
                                  "240  (xlarge)", "320  (xxlarge)"};
        final String[] values = {"120", "160", "213", "240", "320"};
        String cur = DisplayManager.getScreenDensity(activity);
        int sel = 1;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(cur)) { sel = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle("Screen DPI")
            .setSingleChoiceItems(labels, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DisplayManager.setScreenDensity(values[which]);
                        activity.showRebootDialog();
                        dialog.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showTimeoutDialog(final MainActivity activity,
                                           final LinearLayout container) {
        final String[] labels = {"15 seconds", "30 seconds", "1 minute",
                                  "2 minutes", "5 minutes", "Never"};
        final int[]    values = {15000, 30000, 60000, 120000, 300000, Integer.MAX_VALUE};
        int cur = DisplayManager.getScreenTimeout(activity);
        int sel = 1;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == cur) { sel = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle("Screen Timeout")
            .setSingleChoiceItems(labels, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DisplayManager.setScreenTimeout(activity, values[which]);
                        dialog.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showFontDialog(final MainActivity activity,
                                        final LinearLayout container) {
        final String[] labels = {"Small (0.85x)", "Normal (1.0x)",
                                  "Large (1.15x)", "Largest (1.3x)"};
        final float[]  values = {0.85f, 1.0f, 1.15f, 1.3f};
        float cur = DisplayManager.getFontScale(activity);
        int sel = 1;
        for (int i = 0; i < values.length; i++) {
            if (Math.abs(values[i] - cur) < 0.05f) { sel = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle("Font Size")
            .setSingleChoiceItems(labels, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DisplayManager.setFontScale(activity, values[which]);
                        dialog.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showHdmiDialog(final MainActivity activity,
                                        final LinearLayout container) {
        final String[] options = {
            "1280x720@60", "1920x1080@30", "1920x1080@60",
            "3840x2160@30", "3840x2160@60"
        };
        String cur = DisplayManager.getHdmiResolution();
        int sel = 2;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(cur)) { sel = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle("HDMI Resolution")
            .setSingleChoiceItems(options, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DisplayManager.setHdmiResolution(options[which]);
                        activity.showRebootDialog();
                        dialog.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private static void showBrightnessDialog(final MainActivity activity) {
        View layout = LayoutInflater.from(activity)
                .inflate(android.R.layout.activity_list_item, null);
        // Simple seekbar dialog
        final SeekBar seekBar = new SeekBar(activity);
        seekBar.setMax(255);
        seekBar.setProgress(DisplayManager.getBrightness(activity));
        LinearLayout ll = new LinearLayout(activity);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(48, 32, 48, 16);
        final TextView label = new TextView(activity);
        label.setTextColor(0xFF9E9EC8);
        label.setText("Brightness: " + Math.round(seekBar.getProgress() / 2.55f) + "%");
        label.setPadding(0, 0, 0, 16);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean u) {
                label.setText("Brightness: " + Math.round(p / 2.55f) + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
        ll.addView(label);
        ll.addView(seekBar);
        new AlertDialog.Builder(activity)
            .setTitle("Backlight")
            .setView(ll)
            .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DisplayManager.setBrightness(activity, seekBar.getProgress());
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    // ── Label helpers ─────────────────────────────────────────────────────
    private static String getTimeoutLabel(int ms) {
        if (ms <= 15000)  return "15 sec";
        if (ms <= 30000)  return "30 sec";
        if (ms <= 60000)  return "1 min";
        if (ms <= 120000) return "2 min";
        if (ms <= 300000) return "5 min";
        return "Never";
    }

    private static String getFontLabel(float scale) {
        if (scale <= 0.9f) return "Small";
        if (scale <= 1.05f) return "Normal";
        if (scale <= 1.2f) return "Large";
        return "Largest";
    }

    interface OnValueSelected {
        void onSelected(String value);
    }
}
