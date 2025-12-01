package com.example.mobile2025s2_1_2.matching.roommate;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.example.mobile2025s2_1_2.R;
import com.google.android.material.button.MaterialButton;

public class RoommateFragment extends Fragment {

    private List<Map<String, Object>> otherUsers;
    private Map<String, Object> myInfo;
    private List<RoommateCardData.RoommateData> cardList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.matching_roommate_main, container, false);

        ImageView roommateBack = view.findViewById(R.id.matching_roommate_back);
        roommateBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        Bundle args = getArguments();
        if (args != null) {
            this.myInfo = (Map<String, Object>) args.getSerializable("myInfo");
            this.otherUsers = (List<Map<String, Object>>) args.getSerializable("otherUsers");
        }

        // ------------------------------
        // ⭐ SharedPreferences에서 myEmail 직접 가져오기
        // ------------------------------
        SharedPreferences prefs =
                requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String myEmail = prefs.getString("user_email", "");
        Log.d("MATCH", "🔵 myEmail = " + myEmail);

        RecyclerView recyclerView = view.findViewById(R.id.profile_roommate);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        cardList = new ArrayList<>();

        String myGender = safe(myInfo.get("gender"));
        String myDorm = safe(myInfo.get("dorm"));

        int myClean = toInt(myInfo.get("clean"));
        int mySleep = toInt(myInfo.get("sleep"));
        int mySensitive = toInt(myInfo.get("sensitive"));

        List<UserSim> simList = new ArrayList<>();

        if (otherUsers != null) {
            for (Map<String, Object> u : otherUsers) {

                String userEmail = s(u.get("email"));

                // ------------------------------
                // ⭐ 자기 자신 제외
                // ------------------------------
                if (myEmail.equals(userEmail)) {
                    Log.d("MATCH", "🛑 자기 자신 제외됨: " + userEmail);
                    continue;
                }

                // 성별 + 기숙사 필터
                if (!safe(u.get("gender")).equals(myGender)) continue;
                if (!safe(u.get("dorm")).equals(myDorm)) continue;

                // 유사도 계산
                int clean = toInt(u.get("clean"));
                int sleep = toInt(u.get("sleep"));
                int sensitive = toInt(u.get("sensitive"));

                int cleanScore = 10 - Math.abs(clean - myClean);
                int sleepScore = 10 - Math.abs(sleep - mySleep);
                int sensitiveScore = 10 - Math.abs(sensitive - mySensitive);

                int totalScore = cleanScore + sleepScore + sensitiveScore;

                Log.d("MATCH",
                        "🟢 매칭 계산 → " + userEmail +
                                " (총점=" + totalScore + ")");

                simList.add(new UserSim(u, totalScore));
            }
        }

        // 점수 내림차순 정렬
        Collections.sort(simList, (a, b) -> b.score - a.score);

        int limit = Math.min(5, simList.size());
        Log.d("MATCH", "🔥 최종 카드 개수 = " + limit);

        // 카드 생성
        for (int i = 0; i < limit; i++) {
            Map<String, Object> u = simList.get(i).user;

            Log.d("MATCH", "🟩 카드 추가: " + s(u.get("email")));

            cardList.add(new RoommateCardData.RoommateData(
                    safe(u.get("email")),
                    safe(u.get("name")),
                    safe(u.get("gender")),
                    safe(u.get("dorm")),
                    safe(u.get("age")),
                    safe(u.get("mbti")),
                    safe(u.get("alcohol")),
                    safe(u.get("smoking")),
                    safe(u.get("clean")),
                    safe(u.get("sleep")),
                    safe(u.get("sensitive"))
            ));
        }

        // ⭐ cardList 생성 후 바로 아래에 넣기
        View emptyView = view.findViewById(R.id.non_matched_card);   // xml에 미리 만들어야 함

        if (cardList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            RoommateCardAdapter adapter =
                    new RoommateCardAdapter(requireContext(), cardList);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                    super.onScrollStateChanged(rv, newState);
                    applyCardEffects(rv, layoutManager);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                    super.onScrolled(rv, dx, dy);
                    applyCardEffects(rv, layoutManager);
                }
            });
        }

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        MaterialButton matchRoomButton = view.findViewById(R.id.match_roommate_button);

        matchRoomButton.setOnClickListener(v -> {
            View centerCard = snapHelper.findSnapView(layoutManager);
            if (centerCard == null) return;

            int pos = layoutManager.getPosition(centerCard);
            RoommateCardData.RoommateData selected = cardList.get(pos);

            RoommateMatchingFragment fragment = new RoommateMatchingFragment();
            Bundle bundle = new Bundle();
            bundle.putString("name", selected.getName());
            bundle.putString("email", selected.getEmail());
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private String safe(Object o) {
        if (o == null) return "";
        if (o instanceof Boolean) return ((Boolean) o) ? "O" : "X";
        return o.toString();
    }

    private String s(Object o) {
        if (o == null) return "";
        return o.toString().trim();
    }

    private int toInt(Object o) {
        if (o == null) return 0;
        try { return Integer.parseInt(o.toString()); }
        catch (Exception e) { return 0; }
    }

    private static class UserSim {
        Map<String, Object> user;
        int score;
        UserSim(Map<String, Object> u, int s) { this.user = u; this.score = s; }
    }

    private void applyCardEffects(RecyclerView rv, LinearLayoutManager lm) {
        int first = lm.findFirstVisibleItemPosition();
        int last = lm.findLastVisibleItemPosition();

        float centerX = rv.getWidth() / 2f;

        for (int i = first; i <= last; i++) {
            View item = lm.findViewByPosition(i);
            if (item == null) continue;

            float itemCenterX = item.getLeft() + (item.getWidth() / 2f);
            float distance = Math.abs(centerX - itemCenterX);
            float maxDistance = rv.getWidth() / 2f;

            float scale = 1 - (0.1f * (distance / maxDistance));
            float alpha = 1 - (0.4f * (distance / maxDistance));

            item.setScaleX(scale);
            item.setScaleY(scale);
            item.setAlpha(alpha);
        }
    }
}
