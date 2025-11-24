package com.example.mobile2025s2_1_2.matching.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import java.util.List;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.matching.roommate.RoommateCardAdapter;
import com.example.mobile2025s2_1_2.matching.roommate.RoommateCardData;
import com.example.mobile2025s2_1_2.matching.roommate.RoommateMatchingFragment;
import com.google.android.material.button.MaterialButton;

public class ActivityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.matching_activity_main, container, false);

        //뒤로가기
        ImageView roommateBack = view.findViewById(R.id.matching_activity_back);
        roommateBack.setOnClickListener(v->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 프로필 카드 리스트
        RecyclerView recyclerView = view.findViewById(R.id.profile_activity);
        // 가로 스크롤
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        // 카드 데이터 연결
        List<RoommateCardData.RoommateData> roommateList = RoommateCardData.loadRoommates(requireContext());
        RoommateCardAdapter adapter = new RoommateCardAdapter(requireContext(), roommateList);
        recyclerView.setAdapter(adapter);
        // 스냅 효과: 중앙 카드에 자동 정렬
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // 스크롤 시 중앙 카드 제외 투명도 + 크기 축소
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                View centerView = snapHelper.findSnapView(layoutManager);

                for (int i = 0; i < rv.getChildCount(); i++) {
                    View child = rv.getChildAt(i);

                    if (child == centerView) {
                        child.setAlpha(1f);
                        child.setScaleX(1f);
                        child.setScaleY(1f);
                    } else {
                        child.setAlpha(0.6f);
                        child.setScaleX(0.9f);   // 원하는 크기로 조절
                        child.setScaleY(0.9f);
                    }
                }
            }
        });


        //메칭 신청하기 버튼
        MaterialButton matchRoomButton = view.findViewById(R.id.match_activity_button);
        matchRoomButton.setOnClickListener(v -> {
            View centerRoomCard = snapHelper.findSnapView(layoutManager);
            if (centerRoomCard != null) {
                int position = layoutManager.getPosition(centerRoomCard);
                RoommateCardData.RoommateData selectedRoommate = roommateList.get(position);
                String name = selectedRoommate.getName();

                //프로필 이름과 함께 신청으로 값 넘기기
                ActivityMatchingFragment fragment = new ActivityMatchingFragment();
                Bundle args = new Bundle();
                args.putString("name", name);
                fragment.setArguments(args);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(R.id.fragment_container, fragment, "ActivityMatchingFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}