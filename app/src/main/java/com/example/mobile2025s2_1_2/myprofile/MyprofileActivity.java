// 본인의 패KDN 패키지 이름이 맞는지 확인
package com.example.mobile2025s2_1_2.myprofile;

// --- 'import' 구문 ---
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull; // 'NonNull' import
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.mobile2025s2_1_2.R;
import android.widget.ImageButton;
import android.view.View;
import android.content.Intent;
import android.widget.LinearLayout;

// 'MyProfilePagerAdapter' import
import com.example.mobile2025s2_1_2.myprofile.MyProfilePagerAdapter;
// ------------------------------------

public class MyprofileActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    // (다른 공통 UI 변수들)
    // private TextView tvUserName;
    // private TextView tvConfirmButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_main);
// ... onCreate 메서드 안 ...
        setContentView(R.layout.myprofile_main);

// 1. 뒤로가기 버튼 찾기 (ID는 myprofile_main.xml에 있는 ID)
        ImageButton backButton = findViewById(R.id.back_button);

// 2. 뒤로가기 버튼에 클릭 리스너 설정
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 3. 현재 액티비티(MyprofileActivity) 종료
                finish();
            }
        });
// (TabLayoutMediator 코드 등은 이 아래에 계속...)

        // --- 하단 네비게이션 바 버튼 찾기 ---
        LinearLayout navHome = findViewById(R.id.nav_home);
        LinearLayout navNotification = findViewById(R.id.nav_notification);
        LinearLayout navMatching = findViewById(R.id.nav_matching);
        LinearLayout navMyProfile = findViewById(R.id.nav_myprofile);
        LinearLayout navSettings = findViewById(R.id.nav_settings);

        // --- 각 버튼에 클릭 리스너 설정 ---

        // 1. 홈 버튼
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 🚨 HomeActivity.class는 실제 홈 화면 Activity 이름으로 확인/변경!
                Intent intent = new Intent(MyprofileActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // 기존 화면 스택 정리
                startActivity(intent);
                finish(); // 현재 화면 종료
            }
        });

        // 2. 알림 버튼
        navNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 🚨 NotificationActivity.class는 실제 알림 화면 Activity 이름으로 확인/변경!
                Intent intent = new Intent(MyprofileActivity.this, NotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // 현재 화면 종료
            }
        });

        // 3. 매칭 버튼
        navMatching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 🚨 MatchingActivity.class는 실제 매칭 화면 Activity 이름으로 확인/변경!
                Intent intent = new Intent(MyprofileActivity.this, MatchingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // 현재 화면 종료
            }
        });

        // 4. 마이 버튼
        navMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미 '마이' 화면이므로 아무것도 하지 않음
            }
        });

        // 5. 설정 버튼
        navSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 🚨 SettingsActivity.class는 실제 설정 화면 Activity 이름으로 확인/변경!
                Intent intent = new Intent(MyprofileActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // 현재 화면 종료
            }
        });

        // XML 부품 찾기
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.view_pager);

        // (공통 UI 찾기 및 설정)
        // tvUserName = findViewById(R.id.tv_user_name);
        // tvConfirmButton = findViewById(R.id.edit_button);
        // tvUserName.setText("김국민");
        // tvConfirmButton.setOnClickListener(...)

        // 어댑터 생성
        MyProfilePagerAdapter adapter = new MyProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // TabLayout과 ViewPager 연결
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0: tab.setText("진로·전공 멘토"); break;
                            case 1: tab.setText("교내·교외 활동"); break;
                            case 2: tab.setText("기숙사 룸메이트"); break;
                        }
                    }
                }
        ).attach();
    }
}