package com.example.mobile2025s2_1_2.utils;

import android.app.Activity;
import android.content.Intent;
import android.widget.LinearLayout;

import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.home.HomeActivity;
import com.example.mobile2025s2_1_2.matching.MatchingActivity;
import com.example.mobile2025s2_1_2.myprofile.MyprofileActivity;
import com.example.mobile2025s2_1_2.notification.NotificationActivity;
import com.example.mobile2025s2_1_2.settings.SettingsActivity;


public class BottomNavBarHelper {

    public static void setupCustomNav(Activity activity, LinearLayout bottomNavBar) {
        // id별로 자식 View 찾아서 클릭 이벤트 연결
        LinearLayout navHome = bottomNavBar.findViewById(R.id.nav_home);
        LinearLayout navNotification = bottomNavBar.findViewById(R.id.nav_notification);
        LinearLayout navMatching = bottomNavBar.findViewById(R.id.nav_matching);
        LinearLayout navMyprofile = bottomNavBar.findViewById(R.id.nav_myprofile);
        LinearLayout navSettings = bottomNavBar.findViewById(R.id.nav_settings);

        navHome.setOnClickListener(v -> {
            if (!(activity instanceof HomeActivity)) {
                activity.startActivity(new Intent(activity, HomeActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        navNotification.setOnClickListener(v -> {
            if (!(activity instanceof NotificationActivity)) {
                activity.startActivity(new Intent(activity, NotificationActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        navMatching.setOnClickListener(v -> {
            if (!(activity instanceof MatchingActivity)) {
                activity.startActivity(new Intent(activity, MatchingActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        navMyprofile.setOnClickListener(v -> {
            if (!(activity instanceof MyprofileActivity)) {
                activity.startActivity(new Intent(activity, MyprofileActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        navSettings.setOnClickListener(v -> {
            if (!(activity instanceof SettingsActivity)) {
                activity.startActivity(new Intent(activity, SettingsActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });
    }
}