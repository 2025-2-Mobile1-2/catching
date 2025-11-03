package com.example.mobile2025s2_1_2.home;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
    }
}