// 1. 패키지 이름 확인
package com.example.mobile2025s2_1_2.myprofile;

// --- (필요한 import 구문) ---
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// (다른 프래그먼트들도 import해야 합니다)
// import com.example.mobile2025s2_1_2.myprofile.MentorFragment;
// import com.example.mobile2025s2_1_2.myprofile.ActivityFragment;
// import com.example.mobile2025s2_1_2.myprofile.RoommateFragment;

// 2. FragmentStateAdapter를 상속(extends)하는지 확인
public class MyProfilePagerAdapter extends FragmentStateAdapter {

    // 3. 생성자
    public MyProfilePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    // 4. 각 탭의 위치(position)에 맞는 프래그먼트(페이지)를 반환
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MentorFragment(); // 0번 탭
            case 1:
                return new ActivityFragment(); // 1번 탭
            case 2:
                return new RoommateFragment(); // 2번 탭
            default:
                // 혹시 모를 오류 방지를 위해 기본 프래그먼트 반환
                return new MentorFragment();
        }
    }

    // 5. 전체 탭의 개수
    @Override
    public int getItemCount() {
        return 3;
    }
}