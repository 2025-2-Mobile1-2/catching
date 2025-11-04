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
        ImageView notification_nav_Icon = bottomNavBar.findViewById(R.id.nav_notification_icon);
        notification_nav_Icon.setImageResource(R.drawable.ic_notification_non);
        notification_nav_Icon.setImageResource(R.drawable.ic_notification);
        TextView notification_nav_Text = bottomNavBar.findViewById(R.id.nav_notification_text);
        notification_nav_Text.setTextColor(Color.parseColor("#FFCCECE3"));
        notification_nav_Text.setTextColor(Color.parseColor("#FF2DD7A4"));
    }
}