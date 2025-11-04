package com.example.mobile2025s2_1_2.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        ImageView settings_nav_Icon = bottomNavBar.findViewById(R.id.nav_settings_icon);
        settings_nav_Icon.setImageResource(R.drawable.ic_settings_non);
        settings_nav_Icon.setImageResource(R.drawable.ic_settings);
        TextView settings_nav_Text = bottomNavBar.findViewById(R.id.nav_settings_text);
        settings_nav_Text.setTextColor(Color.parseColor("#FFCCECE3"));
        settings_nav_Text.setTextColor(Color.parseColor("#FF2DD7A4"));
    }
}