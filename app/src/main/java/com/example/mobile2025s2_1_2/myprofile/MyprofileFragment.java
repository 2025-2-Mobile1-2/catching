package com.example.mobile2025s2_1_2.myprofile;

// --- 'import' 구문 ---
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.mobile2025s2_1_2.R;

// 'MyProfilePagerAdapter' import

// 뒤로가기 버튼용 import
import android.view.View;

// 하단 네비게이션 바용 import
import android.widget.LinearLayout;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

// ChipGroup 관련 import (TabLayout 대체)
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import android.view.LayoutInflater;
import android.view.ViewGroup;

public class MyprofileFragment extends Fragment {

    private ChipGroup chipGroupTabs; // TabLayout 대신 ChipGroup 사용
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myprofile_main, container, false);

        chipGroupTabs = view.findViewById(R.id.chip_group_tabs);
        viewPager = view.findViewById(R.id.view_pager);

        MyProfilePagerAdapter adapter = new MyProfilePagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Chip selectedChip = (Chip) chipGroupTabs.getChildAt(position);
                if (selectedChip != null) selectedChip.setChecked(true);
            }
        });

        chipGroupTabs.setOnCheckedStateChangeListener((chipGroup, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                Chip chip = chipGroup.findViewById(checkedId);
                if (chip != null) {
                    int position = chipGroup.indexOfChild(chip);
                    if (viewPager.getCurrentItem() != position)
                        viewPager.setCurrentItem(position, true);
                }
            }
        });

        Chip initialChip = (Chip) chipGroupTabs.getChildAt(0);
        if (initialChip != null) initialChip.setChecked(true);

        LinearLayout bottomNavBar = view.findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(requireActivity(), bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_myprofile);

        final EditText etKakaoId = view.findViewById(R.id.edittext_kakao_id);
        ImageView iconEditKakaoId = view.findViewById(R.id.icon_edit_kakao_id);

        iconEditKakaoId.setOnClickListener(v -> {
            etKakaoId.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etKakaoId, InputMethodManager.SHOW_IMPLICIT);
            etKakaoId.setSelection(etKakaoId.getText().length());
        });

        return view;
    }
}