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
                return new MentorFragment();
            case 1:
                return new ActivityFragment();
            case 2:
                return new RoommateFragment();
            default:
                return new MentorFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}