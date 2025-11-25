package com.example.mobile2025s2_1_2.myprofile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

    // 상단 프로필 텍스트뷰
    private TextView tvUserName;
    private TextView tvUserEmail;

    // 파이어베이스 관련 변수
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String myUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myprofile_main, container, false);

        // 1. 파이어베이스 초기화 (실제 유저 UID 획득)
        initFirebase();

        // 2. 뷰 연결
        initViews(view);

        // 3. 어댑터 및 탭 설정
        setupViewPagerAndTabs();

        // 4. 하단바 설정
        BottomNavBarHelper.setupCustomNav(requireActivity(), bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_myprofile);

        // 5. 이벤트 리스너
        setupListeners();

        // 6. [핵심] 기본 정보(이름, 이메일) 불러오기 - 카카오ID 제외함
        loadBasicProfileData();

        return view;
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            myUid = currentUser.getUid(); // 실제 로그인된 UID
        } else {
            myUid = null;
            // 로그인이 안 된 상태 처리
        }
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

    // ⭐ 수정됨: 카카오 ID 가져오는 부분 삭제, 이름/이메일만 로드
    private void loadBasicProfileData() {
        if (myUid == null) return;

        DocumentReference docRef = db.collection("Users").document(myUid);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // 1. 이름 가져오기
                String name = documentSnapshot.getString("name");
                if (name != null && tvUserName != null) {
                    tvUserName.setText(name);
                }

                // 2. 이메일 가져오기
                String email = documentSnapshot.getString("email");
                if (email == null && mAuth.getCurrentUser() != null) {
                    email = mAuth.getCurrentUser().getEmail(); // DB에 없으면 인증정보 사용
                }
                if (email != null && tvUserEmail != null) {
                    tvUserEmail.setText(email);
                }

                // 카카오 ID는 가져오지 않음.
            }
        }).addOnFailureListener(e -> Log.e("MyProfile", "데이터 로드 실패", e));
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
            Chip initialChip = (Chip) chipGroupTabs.getChildAt(0);
            if (initialChip != null) initialChip.setChecked(true);
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