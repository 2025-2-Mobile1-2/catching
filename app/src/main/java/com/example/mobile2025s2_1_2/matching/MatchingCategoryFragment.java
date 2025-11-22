package com.example.mobile2025s2_1_2.matching;

import android.os.Bundle;
import android.text.Html;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.matching.roommate.RoommateFragment;
import com.example.mobile2025s2_1_2.matching.activity.ActivityFragment;


public class MatchingCategoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.matching_category,container,false);

        //카테고리 선택 텍스트 색상 다르게 하기
        TextView textView = view.findViewById(R.id.match_cate_title);
        String text = "<font color='#2DD7A4'>카테고리</font> <font color='#FFFFFF'>선택</font>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }

        //진로, 전공 멘토 매칭 버튼
        View mentorshipView = view.findViewById(R.id.match_cate_mentorship);
        mentorshipView.setOnClickListener(v -> {
            ActivityFragment activityFragment = new ActivityFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.mentorship_container, activityFragment)
                    .addToBackStack(null)
                    .commit();
        });

        //교내, 교외 활동 매칭 버튼
        View activityView = view.findViewById(R.id.match_cate_activity);

        activityView.setOnClickListener(v -> {
            ActivityFragment activityFragment = new ActivityFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_container, activityFragment)
                    .addToBackStack(null)
                    .commit();
        });

        //기숙사 룸메이트 매칭 버튼
        View roomateView = view.findViewById(R.id.match_cate_roommate);

        roomateView.setOnClickListener(v -> {
            RoommateFragment roommateFragment = new RoommateFragment();
            getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.roommate_container, roommateFragment)
                .addToBackStack(null)
                .commit();
        });

        return view;
    }


}
