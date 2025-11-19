package com.example.mobile2025s2_1_2.myprofile;

// --- 'import' 구문 ---
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.mobile2025s2_1_2.R;

// 'MyProfilePagerAdapter' import
import com.example.mobile2025s2_1_2.myprofile.MyProfilePagerAdapter;

// 뒤로가기 버튼용 import
import android.widget.ImageButton;
import android.view.View;

// 하단 네비게이션 바용 import
import android.widget.LinearLayout;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

// ChipGroup 관련 import (TabLayout 대체)
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

// ▼▼▼▼▼ 카카오 ID 수정을 위해 4줄 추가 ▼▼▼▼▼
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
// ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲


public class MyprofileActivity extends AppCompatActivity {

    private ChipGroup chipGroupTabs; // TabLayout 대신 ChipGroup 사용
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_main);



        // --- 2. 상단 탭 (ChipGroup) 기능 ---
        // XML에서 ChipGroup ID와 ViewPager2 ID를 찾습니다.
        chipGroupTabs = findViewById(R.id.chip_group_tabs);
        viewPager = findViewById(R.id.view_pager);

        // 어댑터 연결
        MyProfilePagerAdapter adapter = new MyProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // ViewPager2 페이지 변경 시 ChipGroup 상태 업데이트 (스와이프 시 탭 변경)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 해당 위치의 Chip을 찾아 선택(checked) 상태로 변경
                Chip selectedChip = (Chip) chipGroupTabs.getChildAt(position);
                if (selectedChip != null) {
                    selectedChip.setChecked(true);
                }
            }
        });

        // Chip 클릭 시 ViewPager2 페이지 변경 (탭 클릭 시 화면 변경)
        // 람다 표현식 사용으로 오류 해결 (훨씬 간결함)
        chipGroupTabs.setOnCheckedStateChangeListener((chipGroup, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                Chip chip = chipGroup.findViewById(checkedId);
                if (chip != null) {
                    int position = chipGroup.indexOfChild(chip);
                    // 이미 현재 페이지라면 동작 안 함
                    if (viewPager.getCurrentItem() != position) {
                        viewPager.setCurrentItem(position, true); // smooth scroll
                    }
                }
            }
        });

        // 초기 선택 (앱 실행 시 첫 번째 칩을 선택)
        Chip initialChip = (Chip) chipGroupTabs.getChildAt(0);
        if (initialChip != null) {
            initialChip.setChecked(true);
        }


        // --- 3. 하단 네비게이션 바 기능 ---
        // XML의 <include> 태그 ID (@id/custom_navbar)를 사용
        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);

        // BottomNavBarHelper에게 하단 바 기능 설정을 위임
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);

        // 현재 '마이' 탭이 활성화된 상태임을 Helper에 알림 (아이콘 및 텍스트 색상 변경)
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_myprofile);


        // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
        // --- 4. 카카오톡 ID 입력란 기능 추가 ---
        // (XML의 ID가 'edittext_kakao_id', 'icon_edit_kakao_id'라고 가정)

        final EditText etKakaoId = findViewById(R.id.edittext_kakao_id);
        ImageView iconEditKakaoId = findViewById(R.id.icon_edit_kakao_id);

        // 연필 아이콘을 클릭했을 때의 동작
        iconEditKakaoId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. EditText에 강제로 포커스를 줍니다.
                etKakaoId.requestFocus();

                // 2. 키보드를 수동으로 올립니다.
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etKakaoId, InputMethodManager.SHOW_IMPLICIT);
                }

                // 3. 커서를 텍스트 맨 뒤로 이동시킵니다.
                etKakaoId.setSelection(etKakaoId.getText().length());
            }
        });
        // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
    }
}