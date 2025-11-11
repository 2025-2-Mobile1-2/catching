package com.example.mobile2025s2_1_2.matching.roommate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;

public class RoommateMatchingSuccessFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matching_roommate_match_success, container, false);
        String name = getArguments().getString("name");

        TextView nameText = view.findViewById(R.id.roommate_name);
        nameText.setText(name);
        TextView toNameText = view.findViewById(R.id.roommate_to_name);
        toNameText.setText(name + " 님께");
        TextView textName = view.findViewById(R.id.roommate_text_name);
        textName.setText(name + "님께서 매칭을 수락하시면");




        //확인했어요
        TextView roommateMatchingOk = view.findViewById(R.id.roommate_matching_success);
        roommateMatchingOk.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();

        });

        return view;
    }
}
