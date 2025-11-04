package com.example.mobile2025s2_1_2.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.home.HomeActivity;
import com.example.mobile2025s2_1_2.matching.MatchingActivity;
import com.example.mobile2025s2_1_2.myprofile.MyprofileActivity;
import com.example.mobile2025s2_1_2.notification.NotificationActivity;
import com.example.mobile2025s2_1_2.settings.SettingsActivity;


public class BottomNavBarHelper {
    // 현재 선택된 탭을 관리하는 헬퍼 메서드 추가
    private static void updateSelectedTab(LinearLayout bottomNavBar, int selectedId) {
        ImageView home_nav_Icon = bottomNavBar.findViewById(R.id.nav_home_icon);
        ImageView notification_nav_Icon = bottomNavBar.findViewById(R.id.nav_notification_icon);
        ImageView matching_nav_Icon = bottomNavBar.findViewById(R.id.nav_matching_icon);
        ImageView myprofile_nav_Icon = bottomNavBar.findViewById(R.id.nav_myprofile_icon);
        ImageView settings_nav_Icon = bottomNavBar.findViewById(R.id.nav_settings_icon);

        TextView home_nav_Text = bottomNavBar.findViewById(R.id.nav_home_text);
        TextView myprofile_nav_Text = bottomNavBar.findViewById(R.id.nav_myprofile_text);
        TextView notification_nav_Text = bottomNavBar.findViewById(R.id.nav_notification_text);
        TextView settings_nav_Text = bottomNavBar.findViewById(R.id.nav_settings_text);

        // 전부 기본 상태로 되돌림
        home_nav_Icon.setImageResource(R.drawable.ic_home_non);
        home_nav_Text.setTextColor(Color.parseColor("#FFCCECE3"));
        notification_nav_Icon.setImageResource(R.drawable.ic_notification_non);
        notification_nav_Text.setTextColor(Color.parseColor("#FFCCECE3"));
        matching_nav_Icon.setImageResource(R.drawable.ic_matching);
        myprofile_nav_Icon.setImageResource(R.drawable.ic_profile_non);
        myprofile_nav_Text.setTextColor(Color.parseColor("#FFCCECE3"));
        settings_nav_Icon.setImageResource(R.drawable.ic_settings_non);
        settings_nav_Text.setTextColor(Color.parseColor("#FFCCECE3"));

        // 선택된 탭만 활성 상태로 변경
        if (selectedId == R.id.nav_home) {
            home_nav_Icon.setImageResource(R.drawable.ic_home);
            home_nav_Text.setTextColor(Color.parseColor("#FF2DD7A4"));
            matching_nav_Icon.setImageResource(R.drawable.ic_matching_non);
        } else if (selectedId == R.id.nav_notification) {
            notification_nav_Icon.setImageResource(R.drawable.ic_notification);
            notification_nav_Text.setTextColor(Color.parseColor("#FF2DD7A4"));
            matching_nav_Icon.setImageResource(R.drawable.ic_matching_non);
        }
//        else if (selectedId == R.id.nav_matching) {
//            matching_nav_Icon.setImageResource(R.drawable.ic_matching);
//        }
        else if (selectedId == R.id.nav_myprofile) {
            myprofile_nav_Icon.setImageResource(R.drawable.ic_profile);
            myprofile_nav_Text.setTextColor(Color.parseColor("#FF2DD7A4"));
            matching_nav_Icon.setImageResource(R.drawable.ic_matching_non);
        } else if (selectedId == R.id.nav_settings) {
            settings_nav_Icon.setImageResource(R.drawable.ic_settings);
            settings_nav_Text.setTextColor(Color.parseColor("#FF2DD7A4"));
            matching_nav_Icon.setImageResource(R.drawable.ic_matching_non);
        }
    }
    public static void setActiveTab(LinearLayout bottomNavBar, int activeId) {
        updateSelectedTab(bottomNavBar, activeId);
    }

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

         //BottomNavBarHelper.java (activity를 인자로 받는 유틸)
        navMatching.setOnClickListener(v -> {
            updateSelectedTab(bottomNavBar, R.id.nav_matching);
            if (activity instanceof FragmentActivity) {
                FragmentActivity fa = (FragmentActivity) activity;
                final String TAG = "MatchingCategoryFragment";
                if (fa.getSupportFragmentManager().findFragmentByTag(TAG) != null) return;

                fa.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out)
                        .replace(R.id.fragment_container,
                                new com.example.mobile2025s2_1_2.matching.MatchingCategoryFragment(),
                                TAG)
                        .addToBackStack(null)
                        .commit();
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