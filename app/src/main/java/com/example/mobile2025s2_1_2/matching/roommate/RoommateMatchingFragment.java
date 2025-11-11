package com.example.mobile2025s2_1_2.matching.roommate;

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

        TextView nameText = view.findViewById(R.id.roommate_name);
        nameText.setText(name);

        TextView toNameText = view.findViewById(R.id.roommate_to_name);
        toNameText.setText(name + " 님께");

        //기숙사 매칭 신청 텍스트 색상 다르게 하기
        TextView textView = view.findViewById(R.id.roommate_matching_title);
        String text = "<font color='#2DD7A4'>기숙사 룸메이트 매칭</font> <font color='#FF000000'>을 신청하시겠습니까?</font>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textView.setText(Html.fromHtml(text));
        }

        //신청하기 버튼
        TextView roommateMatchingOk = view.findViewById(R.id.roommate_matching_ok);
        roommateMatchingOk.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
            RoommateMatchingSuccessFragment fragment = new RoommateMatchingSuccessFragment();
            Bundle args = new Bundle();
            args.putString("name", name);
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .add(R.id.fragment_container, fragment, "RoommateMatchingSuccessFragment")
                    .addToBackStack(null)
                    .commit();
        });

        // 취소하기 버튼
        TextView roommateMatchingCancel = view.findViewById(R.id.roommate_matching_cancel);
        roommateMatchingCancel.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
}
