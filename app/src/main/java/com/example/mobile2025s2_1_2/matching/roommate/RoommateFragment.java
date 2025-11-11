package com.example.mobile2025s2_1_2.matching.roommate;

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
import com.google.android.material.button.MaterialButton;

public class RoommateFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.matching_roommate_main, container, false);

        //뒤로가기
        ImageView roommateBack = view.findViewById(R.id.matching_roommate_back);
        roommateBack.setOnClickListener(v->{
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 프로필 카드 리스트
        RecyclerView recyclerView = view.findViewById(R.id.profile_roommate);
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


        //메칭 신청하기 버튼
        MaterialButton matchRoomButton = view.findViewById(R.id.match_roommate_button);
        matchRoomButton.setOnClickListener(v -> {
            View centerRoomCard = snapHelper.findSnapView(layoutManager);
            if (centerRoomCard != null) {
                int position = layoutManager.getPosition(centerRoomCard);
                RoommateCardData.RoommateData selectedRoommate = roommateList.get(position);
                String name = selectedRoommate.getName();

                //프로필 이름과 함께 신처응로 값 넘기기
                RoommateMatchingFragment fragment = new RoommateMatchingFragment();
                Bundle args = new Bundle();
                args.putString("name", name);
                fragment.setArguments(args);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(R.id.fragment_container, fragment, "RoommateMatchingDialogFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}