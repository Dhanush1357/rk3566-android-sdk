package com.ffvd.nexus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.ffvd.nexus.manager.DisplayManager;
import com.ffvd.nexus.ui.DisplayFragment;

public class MainActivity extends Activity {

    private ListView categoryList;
    private LinearLayout settingsContainer;
    private TextView headerCategory;

    private final String[] categories = {
        "📺  Display",
        "📷  Camera",
        "👆  Touch",
        "🌐  Network",
        "📱  Apps",
        "⚙️  System"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoryList     = findViewById(R.id.category_list);
        settingsContainer = findViewById(R.id.settings_container);
        headerCategory   = findViewById(R.id.header_category);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_category,
                R.id.category_name,
                categories);
        categoryList.setAdapter(adapter);

        // Load Display by default
        loadCategory(0);
        categoryList.setItemChecked(0, true);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                loadCategory(position);
            }
        });
    }

    private void loadCategory(int index) {
        settingsContainer.removeAllViews();
        switch (index) {
            case 0:
                headerCategory.setText("Display");
                DisplayFragment.build(this, settingsContainer);
                break;
            case 1:
                headerCategory.setText("Camera");
                addComingSoon("Camera settings coming soon");
                break;
            case 2:
                headerCategory.setText("Touch");
                addComingSoon("Touch settings coming soon");
                break;
            case 3:
                headerCategory.setText("Network");
                addComingSoon("Network settings coming soon");
                break;
            case 4:
                headerCategory.setText("Apps");
                addComingSoon("App management coming soon");
                break;
            case 5:
                headerCategory.setText("System");
                addComingSoon("System settings coming soon");
                break;
        }
    }

    private void addComingSoon(String message) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(0xFF9E9EC8);
        tv.setTextSize(15);
        tv.setPadding(24, 48, 24, 24);
        settingsContainer.addView(tv);
    }

    public void showRebootDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.reboot_required_title)
            .setMessage(R.string.reboot_required_message)
            .setPositiveButton(R.string.reboot_now, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                    pm.reboot(null);
                }
            })
            .setNegativeButton(R.string.reboot_later, null)
            .show();
    }
}
