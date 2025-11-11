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