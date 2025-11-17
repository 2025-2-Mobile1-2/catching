package com.example.mobile2025s2_1_2.settings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);

        //하단 navBar
        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_settings);

        //알림 설정
        MaterialSwitch pushSwitch = findViewById(R.id.settings_push_switch);
        // 상태 리스너 등록
        pushSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                pushSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FF2DD7A4")));
            } else {
                pushSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FFE5E5E5")));
            }
        });
        MaterialSwitch emailSwitch = findViewById(R.id.settings_email_switch);
        emailSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                emailSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FF2DD7A4")));
            } else {
                emailSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FFE5E5E5")));
            }
        });


        //문의사항 설명
        LinearLayout questionLayout = findViewById(R.id.question_layout);
        ImageView questionIcon = findViewById(R.id.question_icon);
        TextView questionText = findViewById(R.id.question);
        final boolean[] isExpanded = {false};
        questionLayout.setOnClickListener(v -> {
            isExpanded[0] = !isExpanded[0];

            if (isExpanded[0]) {
                questionText.setVisibility(View.VISIBLE);
                questionIcon.setImageResource(R.drawable.ic_arrow_down);
            } else {
                questionText.setVisibility(View.GONE);
                questionIcon.setImageResource(R.drawable.ic_arrow_right_mint);
            }
        });

        //로그아웃 알림창
        ImageView logout = findViewById(R.id.settings_logout_icon);
        logout.setOnClickListener(v -> {
            LogoutDialogFragment fragment = new LogoutDialogFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment, "LogoutDialogFragment")
                    .addToBackStack(null)
                    .commit();
        });

        //계정 삭제 알림창
        ImageView deleteAccount = findViewById(R.id.settings_delete_account_icon);
        deleteAccount.setOnClickListener(v -> {
            DeleteAccountDialogFragment fragment = new DeleteAccountDialogFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment, "DeleteAccountDialogFragment")
                    .addToBackStack(null)
                    .commit();
        });

    }
}