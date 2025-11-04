package com.example.mobile2025s2_1_2.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

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

//        navMatching.setOnClickListener(v -> {
//            if (!(activity instanceof MatchingActivity)) {
//                activity.startActivity(new Intent(activity, MatchingActivity.class));
//                activity.overridePendingTransition(0, 0);
//            }
//        });
        // BottomNavBarHelper.java (activity를 인자로 받는 유틸)
        navMatching.setOnClickListener(v -> {
            if (activity instanceof FragmentActivity) {
                FragmentActivity fa = (FragmentActivity) activity;

                // container가 있는 액티비티인지 확인 (안전장치)
                View container = fa.findViewById(R.id.fragment_container);
                if (container == null) {
                    // TODO: 로그/토스트로 알려주기 (해당 Activity 레이아웃에 FrameLayout 추가 필요)
                    return;
                }

                final String TAG = "MatchingCategoryFragment";
                if (fa.getSupportFragmentManager().findFragmentByTag(TAG) != null) return;

                fa.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out,
                                android.R.anim.fade_in,
                                android.R.anim.fade_out)
                        .add(R.id.fragment_container,
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