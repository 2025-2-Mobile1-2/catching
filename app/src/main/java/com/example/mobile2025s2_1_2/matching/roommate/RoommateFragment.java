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
import java.util.Map;

import com.example.mobile2025s2_1_2.R;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class RoommateFragment extends Fragment {

    private List<Map<String, Object>> otherUsers;
    private Map<String, Object> myInfo;

    private List<RoommateCardData.RoommateData> cardList; // 변환된 최종 카드 리스트

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.matching_roommate_main, container, false);

        // 뒤로가기
        ImageView roommateBack = view.findViewById(R.id.matching_roommate_back);
        roommateBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 전달받은 데이터
        Bundle args = getArguments();
        if (args != null) {
            this.myInfo = (Map<String, Object>) args.getSerializable("myInfo");
            this.otherUsers = (List<Map<String, Object>>) args.getSerializable("otherUsers");
        }

        // 카드 리스트
        RecyclerView recyclerView = view.findViewById(R.id.profile_roommate);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // ★ Firestore → 카드 데이터 변환
        cardList = new ArrayList<>();

        // ⭐ 1) 내 정보 먼저 추가
        if (myInfo != null) {
            cardList.add(new RoommateCardData.RoommateData(
                    safeToString(myInfo.get("email"), "N/A"),
                    safeToString(myInfo.get("name"), "미상"),
                    safeToString(myInfo.get("sex"), "미상"),
                    safeToString(myInfo.get("department"), "미상"),
                    safeToString(myInfo.get("grade"), "미상"),
                    safeToString(myInfo.get("mbti"), "미상"),
                    safeToString(myInfo.get("drink"), "N/A"),
                    safeToString(myInfo.get("smoking"), "N/A"),
                    50, 50, 50
            ));
        }

        // ⭐ 2) 다른 사용자 추가
        if (otherUsers != null) {
            for (Map<String, Object> user : otherUsers) {

                String email = safeToString(user.get("email"), "N/A");
                String name = safeToString(user.get("name"), "미상");
                String sex = safeToString(user.get("sex"), "미상");
                String domitory = safeToString(user.get("department"), "미상");
                String age = safeToString(user.get("grade"), "미상");
                String mbti = safeToString(user.get("mbti"), "미상");
                String drink = safeToString(user.get("drink"), "N/A");
                String smoke = safeToString(user.get("smoking"), "N/A");

                int clean = 50;
                int sleep = 50;
                int subtle = 50;

                cardList.add(new RoommateCardData.RoommateData(
                        email, name, sex, domitory, age, mbti, drink, smoke,
                        clean, sleep, subtle
                ));
            }
        }

        // Adapter 연결
        RoommateCardAdapter adapter = new RoommateCardAdapter(requireContext(), cardList);
        recyclerView.setAdapter(adapter);

        // 스냅 효과
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

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
                        child.setScaleX(0.9f);
                        child.setScaleY(0.9f);
                    }
                }
            }
        });

        // 매칭 버튼
        MaterialButton matchRoomButton = view.findViewById(R.id.match_roommate_button);

        matchRoomButton.setOnClickListener(v -> {

            View centerCard = snapHelper.findSnapView(layoutManager);

            if (centerCard != null) {
                int position = layoutManager.getPosition(centerCard);

                RoommateCardData.RoommateData selectedCard = cardList.get(position);

                String name = selectedCard.getName();
                String email = selectedCard.getEmail();

                RoommateMatchingFragment fragment = new RoommateMatchingFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("email", email);
                fragment.setArguments(bundle);

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

    // ★ Boolean → String crash 방지
    private String safeToString(Object value, String defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Boolean) return ((Boolean) value) ? "O" : "X";
        return value.toString();
    }
}
