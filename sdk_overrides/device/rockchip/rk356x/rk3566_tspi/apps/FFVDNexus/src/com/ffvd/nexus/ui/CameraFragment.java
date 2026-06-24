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
import com.ffvd.nexus.manager.CameraManager;

public class CameraFragment {

    public static void build(final MainActivity activity, LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(activity);

        // ── SECTION: Back Camera ──────────────────────────────────────────
        addSectionHeader(inflater, container, "BACK CAMERA");

        // Back camera rotation
        addArrowRow(inflater, container,
            "🔄",
            "Back Camera Rotation",
            "Current: " + CameraManager.getBackCameraRotation() + "°",
            CameraManager.getBackCameraRotation() + "°",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRotationDialog(activity,
                        "Back Camera Rotation",
                        CameraManager.getBackCameraRotation(),
                        new OnValueSelected() {
                            @Override
                            public void onSelected(String value) {
                                CameraManager.setBackCameraRotation(value);
                                activity.showRebootDialog();
                            }
                        });
                }
            });

        // Back camera mirror
        addSwitchRow(inflater, container,
            "🪞",
            "Back Camera Mirror",
            "Flip camera preview horizontally",
            CameraManager.isBackCameraMirrorEnabled(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    CameraManager.setBackCameraMirror(checked);
                    activity.showRebootDialog();
                }
            });

        // ── SECTION: Sync Settings ────────────────────────────────────────
        addSectionHeader(inflater, container, "SYNC SETTINGS");

        // Camera rotation sync with display
        addSwitchRow(inflater, container,
            "🔗",
            "Sync Rotation with Display",
            "Camera angle follows main screen rotation",
            CameraManager.isCameraRotationSyncEnabled(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    CameraManager.setCameraRotationSyncWithDisplay(checked);
                    activity.showRebootDialog();
                }
            });

        // ── SECTION: Camera Options ───────────────────────────────────────
        addSectionHeader(inflater, container, "CAMERA OPTIONS");

        // Flash
        addSwitchRow(inflater, container,
            "⚡",
            "Camera Flash",
            "Enable / disable camera flash",
            CameraManager.isCameraFlashEnabled(),
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton btn, boolean checked) {
                    CameraManager.setCameraFlashEnabled(checked);
                }
            });

        // ── SECTION: Info ─────────────────────────────────────────────────
        addSectionHeader(inflater, container, "DEVICE INFO");

        // Show current camera properties as read-only info
        addInfoRow(inflater, container,
            "📷", "Back Camera Rotation",
            CameraManager.getBackCameraRotation() + "°");

        addInfoRow(inflater, container,
            "🪞", "Mirror Status",
            CameraManager.isBackCameraMirrorEnabled() ? "Enabled" : "Disabled");

        addInfoRow(inflater, container,
            "🔗", "Sync with Display",
            CameraManager.isCameraRotationSyncEnabled() ? "Enabled" : "Disabled");

        // Note about reboot
        addNoteRow(inflater, container,
            "⚠️ Camera rotation and mirror changes require a reboot to take effect.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static void addSectionHeader(LayoutInflater inf,
                                          LinearLayout container, String title) {
        View v = inf.inflate(R.layout.item_section_header, container, false);
        ((TextView) v.findViewById(R.id.section_title)).setText(title);
        container.addView(v);
    }

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

    private static void addInfoRow(LayoutInflater inf, LinearLayout container,
                                    String icon, String title, String value) {
        View v = inf.inflate(R.layout.item_setting_arrow, container, false);
        ((TextView) v.findViewById(R.id.setting_icon)).setText(icon);
        ((TextView) v.findViewById(R.id.setting_title)).setText(title);
        ((TextView) v.findViewById(R.id.setting_summary)).setText("Read only");
        ((TextView) v.findViewById(R.id.setting_value)).setText(value);
        // no click listener — read only
        container.addView(v);
    }

    private static void addNoteRow(LayoutInflater inf,
                                    LinearLayout container, String note) {
        TextView tv = new TextView(inf.getContext());
        tv.setText(note);
        tv.setTextColor(0xFFFF9800);
        tv.setTextSize(12f);
        tv.setPadding(16, 24, 16, 8);
        container.addView(tv);
    }

    private static void showRotationDialog(final MainActivity activity,
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

    interface OnValueSelected {
        void onSelected(String value);
    }
}