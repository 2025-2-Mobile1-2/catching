package com.example.mobile2025s2_1_2.myprofile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyprofileFragment extends Fragment {

    private ChipGroup chipGroupTabs;
    private ViewPager2 viewPager;
    private EditText etKakaoId;
    private ImageView iconEditKakaoId;
    private LinearLayout bottomNavBar;
    private View btnSave;

    private TextView tvUserName;
    private TextView tvUserEmail;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String myEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myprofile_main, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 현재 로그인한 유저의 이메일 가져오기 (문서 ID로 사용)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            myEmail = currentUser.getEmail();
        }

        initViews(view);
        setupViewPagerAndTabs();
        BottomNavBarHelper.setupCustomNav(requireActivity(), bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_myprofile);
        setupListeners();

        loadBasicProfileData();

        return view;
    }

    private void initViews(View view) {
        chipGroupTabs = view.findViewById(R.id.chip_group_tabs);
        viewPager = view.findViewById(R.id.view_pager);
        etKakaoId = view.findViewById(R.id.edittext_kakao_id);
        iconEditKakaoId = view.findViewById(R.id.icon_edit_kakao_id);
        bottomNavBar = view.findViewById(R.id.custom_navbar);
        btnSave = view.findViewById(R.id.edit_button);

        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
    }

    private void loadBasicProfileData() {
        if (myEmail == null) return;

        DocumentReference docRef = db.collection("Users").document(myEmail);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                if (name != null && tvUserName != null) tvUserName.setText(name);

                String email = documentSnapshot.getString("email");
                if (email != null && tvUserEmail != null) tvUserEmail.setText(email);

                String kakaoId = documentSnapshot.getString("kakaoId");
                if (kakaoId != null && etKakaoId != null) etKakaoId.setText(kakaoId);
            }
        });
    }

    private void setupViewPagerAndTabs() {
        MyProfilePagerAdapter adapter = new MyProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Chip selectedChip = (Chip) chipGroupTabs.getChildAt(position);
                if (selectedChip != null) selectedChip.setChecked(true);
            }
        });

        chipGroupTabs.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    int position = group.indexOfChild(chip);
                    if (viewPager.getCurrentItem() != position) {
                        viewPager.setCurrentItem(position, true);
                    }
                }
            }
        });

        if (chipGroupTabs.getChildCount() > 0) {
            ((Chip) chipGroupTabs.getChildAt(0)).setChecked(true);
        }
    }

    private void setupListeners() {
        iconEditKakaoId.setOnClickListener(v -> {
            etKakaoId.setEnabled(true);
            etKakaoId.requestFocus();
            etKakaoId.setSelection(etKakaoId.getText().length());
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(etKakaoId, InputMethodManager.SHOW_IMPLICIT);
        });

        btnSave.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() == 2) {
                Fragment fragment = getChildFragmentManager().findFragmentByTag("f2");
                if (fragment instanceof RoommateFragment) {
                    ((RoommateFragment) fragment).saveRoommateData();
                }
            } else {
                Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}