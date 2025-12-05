package com.example.mobile2025s2_1_2.myprofile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mobile2025s2_1_2.myprofile.MentorFragment;
import com.example.mobile2025s2_1_2.myprofile.ActivityFragment;
import com.example.mobile2025s2_1_2.myprofile.RoommateFragment;

public class MyProfilePagerAdapter extends FragmentStateAdapter {

    // [수정된 부분] 생성자가 FragmentActivity 대신 Fragment를 받도록 변경
    public MyProfilePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // 기본으로 뜨는 Fragment → 룸메이트
                return new RoommateFragment();
            case 1:
                return new MentorFragment();     // 1번
            case 2:
                return new ActivityFragment();   // 2번
            default:
                return new RoommateFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}