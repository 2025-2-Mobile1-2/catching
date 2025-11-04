package com.example.mobile2025s2_1_2.matching;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

public class MatchingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_main);

        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        ImageView matching_nav_Icon = bottomNavBar.findViewById(R.id.nav_matching_icon);
        matching_nav_Icon.setImageResource(R.drawable.ic_matching_non);
        matching_nav_Icon.setImageResource(R.drawable.ic_matching);
    }
}