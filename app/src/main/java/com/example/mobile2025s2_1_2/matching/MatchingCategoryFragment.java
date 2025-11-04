package com.example.mobile2025s2_1_2.matching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;


public class MatchingCategoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.matching_category,container,false);

        LinearLayout bottomNavBar = requireActivity().findViewById(R.id.custom_navbar);
        ImageView matching_nav_Icon_f = bottomNavBar.findViewById(R.id.nav_matching_icon);
        matching_nav_Icon_f.setImageResource(R.drawable.ic_matching_non);
        matching_nav_Icon_f.setImageResource(R.drawable.ic_matching);
        return view;
    }

}
