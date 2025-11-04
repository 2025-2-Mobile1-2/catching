package com.example.mobile2025s2_1_2.notification;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_main);

        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_notification);
    }
}