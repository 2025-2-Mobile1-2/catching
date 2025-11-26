package com.example.mobile2025s2_1_2.matching;

import android.os.Bundle;
import android.text.Html;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.matching.roommate.RoommateFragment;
import com.example.mobile2025s2_1_2.matching.activity.ActivityFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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
                    .replace(R.id.match_category_container, activityFragment)
                    .addToBackStack(null)
                    .commit();
        });

        //교내, 교외 활동 매칭 버튼
        View activityView = view.findViewById(R.id.match_cate_activity);

        activityView.setOnClickListener(v -> {
            ActivityFragment activityFragment = new ActivityFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.match_category_container, activityFragment)

                    .addToBackStack(null)
                    .commit();
        });

        //기숙사 룸메이트 매칭 버튼
        View roomateView = view.findViewById(R.id.match_cate_roommate);

        roomateView.setOnClickListener(v -> {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            // 🔥 로그인 안 되어있으면 바로 return
            if (user == null) {
                Log.e("MATCH", "로그인 안 되어 있음!");
                Toast.makeText(getContext(), "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = user.getEmail();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // 1) 내 정보 가져오기
            db.collection("Users").document(uid).get()
                    .addOnSuccessListener(myDoc -> {
                        Log.e("MATCH", "실행");

                        if (!myDoc.exists()) {
                            Log.e("MATCH", "내 정보가 Firestore에 없음.");
                            return;
                        }

                        Map<String, Object> myInfo = myDoc.getData();

                        // 2) 전체 사용자 정보 가져오기
                        db.collection("Users").get()
                                .addOnSuccessListener(query -> {

                                    List<Map<String, Object>> otherUsers = new ArrayList<>();

                                    for (DocumentSnapshot doc : query.getDocuments()) {
                                        if (!doc.getId().equals(uid)) {  // 본인 제외
                                            otherUsers.add(doc.getData());
                                        }
                                    }

                                    Log.d("MATCH", "내 정보: " + myInfo);
                                    Log.d("MATCH", "다른 사람들: " + otherUsers);

                                    // 3) RoommateFragment 로 데이터 전달
                                    RoommateFragment fragment = new RoommateFragment();

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("myInfo", (Serializable) myInfo);
                                    bundle.putSerializable("otherUsers", (Serializable) otherUsers);
                                    fragment.setArguments(bundle);

                                    getParentFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.match_category_container, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("MATCH", "전체 유저 정보 가져오기 실패", e);
                                });

                    })
                    .addOnFailureListener(e -> {
                        Log.e("MATCH", "내 정보 가져오기 실패", e);
                    });

        });



        return view;
    }


}
