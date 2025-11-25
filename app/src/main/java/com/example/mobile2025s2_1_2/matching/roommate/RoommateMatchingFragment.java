package com.example.mobile2025s2_1_2.matching.roommate;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;

public class RoommateMatchingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matching_roommate_match_dialog, container, false);

        //상대 이름
        String name = getArguments().getString("name");

        TextView nameText = view.findViewById(R.id.matching_roommate_name);
        nameText.setText(name);

        TextView toNameText = view.findViewById(R.id.matching_roommate_to_name);
        toNameText.setText(name + " 님께");

        //기숙사 매칭 신청 텍스트 색상 다르게 하기
        TextView textView = view.findViewById(R.id.matching_roommate_title);
        String text = "<font color='#2DD7A4'>기숙사 룸메이트 매칭</font><font color='#FF000000'>을 신청하시겠습니까?</font>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }

        //신청하기 버튼
        TextView roommateMatchingOk = view.findViewById(R.id.matching_roommate_ok);
        roommateMatchingOk.setOnClickListener(v -> {
            // 🔥 Firestore 매칭 상태 저장 (matching_status)
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            String fromID = prefs.getString("user_email", null);

            String toID = getArguments().getString("email");  // 상대 이메일 (호출부에서 전달 필요)

            Map<String, Object> matchData = new HashMap<>();
            matchData.put("fromID", fromID);
            matchData.put("toID", toID);
            matchData.put("category", "roommate");
            matchData.put("state", "request");
            matchData.put("isNewForA", true); // 보낸 사람(A)에게 새 알림
            matchData.put("isNewForB", true); // 받은 사람(B)에게 새 알림
            matchData.put("timestamp", FieldValue.serverTimestamp());

            db.collection("matching_status")
                    .add(matchData)
                    .addOnSuccessListener(doc -> {
                        Log.d("MATCH", "🔥 매칭 요청 저장됨! 문서 ID: " + doc.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MATCH", "❌ 매칭 저장 실패: ", e);
                    });

            requireActivity().getSupportFragmentManager().popBackStack();
            RoommateMatchingSuccessFragment fragment = new RoommateMatchingSuccessFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            args.putString("email", toID);
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.fragment_container, fragment, "RoommateMatchingSuccessFragment")
                    .addToBackStack(null)
                    .commit();
        });

        // 취소하기 버튼
        TextView roommateMatchingCancel = view.findViewById(R.id.matching_roommate_cancel);
        roommateMatchingCancel.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
}
