package com.example.mobile2025s2_1_2.myprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;

import java.util.ArrayList;
import java.util.List;

public class RoommateFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myprofile_fragment_roommate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 성별 (spinner_gender)
        Spinner spinnerGender = view.findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gender_array,
                android.R.layout.simple_spinner_item // ★ 기본 레이아웃 사용 (오류 방지)
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 로직 없음
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // 2. 기숙사 (spinner_dormitory)
        Spinner spinnerDormitory = view.findViewById(R.id.spinner_dormitory);
        ArrayAdapter<CharSequence> dormitoryAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.dormitory_array,
                android.R.layout.simple_spinner_item // ★ 기본 레이아웃 사용
        );
        dormitoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDormitory.setAdapter(dormitoryAdapter);


        // 3. 나이 (spinner_age)
        Spinner spinnerAge = view.findViewById(R.id.spinner_age);
        List<String> ageList = new ArrayList<>();
        ageList.add("선택");
        for (int year = 2008; year >= 1980; year--) {
            ageList.add(year + "년생");
        }
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item, // ★ 기본 레이아웃 사용
                ageList
        );
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAge.setAdapter(ageAdapter);


        // 4. MBTI (spinner_mbti)
        Spinner spinnerMbti = view.findViewById(R.id.spinner_mbti);
        ArrayAdapter<CharSequence> mbtiAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.mbti_array,
                android.R.layout.simple_spinner_item // ★ 기본 레이아웃 사용
        );
        mbtiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMbti.setAdapter(mbtiAdapter);


        // 5. 음주 여부 (spinner_alcohol)
        Spinner spinnerAlcohol = view.findViewById(R.id.spinner_alcohol);
        ArrayAdapter<CharSequence> alcoholAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.ox_array,
                android.R.layout.simple_spinner_item // ★ 기본 레이아웃 사용
        );
        alcoholAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlcohol.setAdapter(alcoholAdapter);


        // 6. 흡연 여부 (spinner_smoke)
        Spinner spinnerSmoking = view.findViewById(R.id.spinner_smoke);
        ArrayAdapter<CharSequence> smokingAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.ox_array,
                android.R.layout.simple_spinner_item // ★ 기본 레이아웃 사용
        );
        smokingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSmoking.setAdapter(smokingAdapter);

    }
}