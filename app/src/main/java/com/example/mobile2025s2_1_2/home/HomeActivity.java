package com.example.mobile2025s2_1_2.home;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        ImageView home_nav_Icon = bottomNavBar.findViewById(R.id.nav_home_icon);
        home_nav_Icon.setImageResource(R.drawable.ic_home_non);
        home_nav_Icon.setImageResource(R.drawable.ic_home);
        TextView home_nav_Text = bottomNavBar.findViewById(R.id.nav_home_text);
        home_nav_Text.setTextColor(Color.parseColor("#FFCCECE3"));
        home_nav_Text.setTextColor(Color.parseColor("#FF2DD7A4"));
    }
}