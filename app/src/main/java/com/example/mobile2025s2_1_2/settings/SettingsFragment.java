package com.example.mobile2025s2_1_2.settings;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_main, container, false);

        LinearLayout bottomNavBar = view.findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(requireActivity(), bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_settings);

        MaterialSwitch pushSwitch = view.findViewById(R.id.settings_push_switch);
        pushSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                pushSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FF2DD7A4")));
            } else {
                pushSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FFE5E5E5")));
            }
        });

        MaterialSwitch emailSwitch = view.findViewById(R.id.settings_email_switch);
        emailSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                emailSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FF2DD7A4")));
            } else {
                emailSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#FFE5E5E5")));
            }
        });

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

        ImageView logout = view.findViewById(R.id.settings_logout_icon);
        logout.setOnClickListener(v -> {
            LogoutDialogFragment fragment = new LogoutDialogFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment, "LogoutDialogFragment")
                    .addToBackStack(null)
                    .commit();
        });

        ImageView deleteAccount = view.findViewById(R.id.settings_delete_account_icon);
        deleteAccount.setOnClickListener(v -> {
            DeleteAccountDialogFragment fragment = new DeleteAccountDialogFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment, "DeleteAccountDialogFragment")
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}