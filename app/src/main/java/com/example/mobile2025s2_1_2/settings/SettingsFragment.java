package com.example.mobile2025s2_1_2.settings;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsFragment extends Fragment {
    public static View touchBlocker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_main, container, false);
        touchBlocker = view.findViewById(R.id.touch_blocker);

        // 하단바
        LinearLayout bottomNavBar = view.findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(requireActivity(), bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_settings);

        // ───────── 인앱 알림 스위치 (settings_email_switch 하나만 사용) ─────────
        MaterialSwitch inappSwitch = view.findViewById(R.id.settings_email_switch);

        // SharedPreferences에서 현재 설정값 읽기 (기본값: true = 켜짐)
        SharedPreferences prefs =
                requireActivity().getSharedPreferences("app_settings", requireActivity().MODE_PRIVATE);
        boolean inappEnabled = prefs.getBoolean("inapp_enabled", true);

        // 스위치 초기 상태 반영
        inappSwitch.setChecked(inappEnabled);
        if (inappEnabled) {
            inappSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FF2DD7A4")));
        } else {
            inappSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FFE5E5E5")));
        }

        // 변경 시 SharedPreferences에 저장
        inappSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 색상 변경
            if (isChecked) {
                inappSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FF2DD7A4")));
            } else {
                inappSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FFE5E5E5")));
            }

            // 설정 저장
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("inapp_enabled", isChecked);
            editor.apply();
        });

        // 문의하기
        LinearLayout questionLayout = view.findViewById(R.id.question_layout);
        ImageView questionIcon = view.findViewById(R.id.question_icon);
        TextView questionText = view.findViewById(R.id.question);
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

        // 로그아웃
        View logout = view.findViewById(R.id.settings_logout);
        logout.setOnClickListener(v -> {
            touchBlocker.setVisibility(View.VISIBLE);
            LogoutDialogFragment fragment = new LogoutDialogFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.fragment_container, fragment, "LogoutDialogFragment")
                    .addToBackStack(null)
                    .commit();
        });

        // 계정삭제
        View deleteAccount = view.findViewById(R.id.settings_delete_account);
        deleteAccount.setOnClickListener(v -> {
            touchBlocker.setVisibility(View.VISIBLE);
            DeleteAccountDialogFragment fragment = new DeleteAccountDialogFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.fragment_container, fragment, "DeleteAccountDialogFragment")
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
