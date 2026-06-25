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
import com.ffvd.nexus.manager.TouchManager;

public class TouchFragment {

    public static void build(final MainActivity activity,
                              LinearLayout container) {
        LayoutInflater inf = LayoutInflater.from(activity);

        // ── SECTION: Touch Rotation ───────────────────────────────────────
        addSectionHeader(inf, container, "TOUCH ROTATION");

        addArrowRow(inf, container, "🔄", "Touch Panel Rotation",
            "Current: " + TouchManager.getTouchRotation() + "°",
            TouchManager.getTouchRotation() + "°",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRotationDialog(activity);
                }
            });

        // ── SECTION: Axis Calibration ─────────────────────────────────────
        addSectionHeader(inf, container, "AXIS CALIBRATION");

        addSwitchRow(inf, container, "↔️", "Swap X/Y Axis",
            "Exchange horizontal and vertical touch axis",
            TouchManager.isTouchSwapXY(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean checked) {
                    TouchManager.setTouchSwapXY(checked);
                    activity.showRebootDialog();
                }
            });

        addSwitchRow(inf, container, "⬅️", "Invert X Axis",
            "Mirror touch input on horizontal axis",
            TouchManager.isTouchInvertX(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean checked) {
                    TouchManager.setTouchInvertX(checked);
                    activity.showRebootDialog();
                }
            });

        addSwitchRow(inf, container, "⬆️", "Invert Y Axis",
            "Mirror touch input on vertical axis",
            TouchManager.isTouchInvertY(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean checked) {
                    TouchManager.setTouchInvertY(checked);
                    activity.showRebootDialog();
                }
            });

        // ── SECTION: Touch Panel ──────────────────────────────────────────
        addSectionHeader(inf, container, "TOUCH PANEL");

        addSwitchRow(inf, container, "👆", "Touch Panel Enabled",
            "Enable or disable touch input entirely",
            TouchManager.isTouchEnabled(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton b, boolean checked) {
                    TouchManager.setTouchEnabled(checked);
                    activity.showRebootDialog();
                }
            });

        // ── SECTION: Info ─────────────────────────────────────────────────
        addSectionHeader(inf, container, "DEVICE INFO");

        addInfoRow(inf, container, "ℹ️", "Touch Device",
            TouchManager.getTouchDeviceInfo());

        addNoteRow(container,
            "⚠️ Touch calibration changes require a reboot.");
    }

    // ── Rotation dialog ───────────────────────────────────────────────────
    private static void showRotationDialog(final MainActivity activity) {
        final String[] options = {"0°", "90°", "180°", "270°"};
        final String[] values  = {"0",  "90",  "180",  "270"};
        String cur = TouchManager.getTouchRotation();
        int sel = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(cur)) { sel = i; break; }
        }
        new AlertDialog.Builder(activity)
            .setTitle("Touch Panel Rotation")
            .setSingleChoiceItems(options, sel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        TouchManager.setTouchRotation(values[which]);
                        activity.showRebootDialog();
                        d.dismiss();
                    }
                })
            .setNegativeButton("Cancel", null)
            .show();
    }

    // ── Helpers ───────────────────────────────────────────────────────────
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

    private static void addNoteRow(LinearLayout c, String note) {
        TextView tv = new TextView(c.getContext());
        tv.setText(note);
        tv.setTextColor(0xFFFF9800);
        tv.setTextSize(12f);
        tv.setPadding(16, 24, 16, 8);
        c.addView(tv);
    }
}