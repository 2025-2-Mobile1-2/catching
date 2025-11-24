package com.example.mobile2025s2_1_2.matching.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;

public class ActivityMatchingSuccessFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matching_activity_match_success, container, false);
        String name = getArguments().getString("name");

        TextView nameText = view.findViewById(R.id.matching_activity_name);
        nameText.setText(name);
        TextView toNameText = view.findViewById(R.id.matching_activity_to_name);
        toNameText.setText(name + " 님께");
        TextView textName = view.findViewById(R.id.matching_activity_text_name);
        textName.setText(name + "님께서 매칭을 수락하시면");




        //확인했어요
        TextView roommateMatchingOk = view.findViewById(R.id.matching_activity_success);
        roommateMatchingOk.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();

        });

        return view;
    }
}
