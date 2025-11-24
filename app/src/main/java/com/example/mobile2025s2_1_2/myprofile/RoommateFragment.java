package com.example.mobile2025s2_1_2.myprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

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
        // RoommateFragment.java - onViewCreated 메서드 끝 부분에 추가

// 1. 위젯 참조
        SeekBar seekBarCleanliness = view.findViewById(R.id.seekbar_cleanliness);
        TextView valueLabel1 = view.findViewById(R.id.seekbar_value_label1);

// 2. Null 체크를 통해 충돌 방지 (가장 중요)
        if (seekBarCleanliness != null && valueLabel1 != null) {

            // 3. 리스너 설정: 값 업데이트 기능만 구현
            seekBarCleanliness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    // ⭐ 이 부분이 progress 값을 TextView에 표시하는 유일한 방법입니다.
                    valueLabel1.setText(String.valueOf(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

            // 4. 초기값 설정 (화면 로딩 시 초기 Progress 값(5)을 TextView에 표시)
            valueLabel1.setText(String.valueOf(seekBarCleanliness.getProgress()));
        }
        // RoommateFragment.java - onViewCreated 메서드 끝 부분에 추가

// ... (기존 청결도(seekbar_cleanliness) 로직 다음에 이어서) ...

// ----------------------------------------------------------------------
// ⭐ 8. 잠꼬대 정도 (SeekBar) 및 동적 라벨 기능 추가
// ----------------------------------------------------------------------

// 1. 위젯 참조
        SeekBar seekBarSnoring = view.findViewById(R.id.seekbar_Sleeptalk);
        TextView valueLabel2 = view.findViewById(R.id.seekbar_value_label2);

// 2. Null 체크를 통해 충돌 방지
        if (seekBarSnoring != null && valueLabel2 != null) {

            // 3. 리스너 설정: 값 업데이트 기능만 구현
            seekBarSnoring.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    // 값을 TextView에 표시
                    valueLabel2.setText(String.valueOf(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

            // 4. 초기값 설정
            valueLabel2.setText(String.valueOf(seekBarSnoring.getProgress()));
        }

// ----------------------------------------------------------------------
// ⭐ 9. 예민 정도 (SeekBar) 및 동적 라벨 기능 추가
// ----------------------------------------------------------------------

// 1. 위젯 참조
        SeekBar seekBarSensitivity = view.findViewById(R.id.seekbar_sensitive);
        TextView valueLabel3 = view.findViewById(R.id.seekbar_value_label3);

// 2. Null 체크를 통해 충돌 방지
        if (seekBarSensitivity != null && valueLabel3 != null) {

            // 3. 리스너 설정: 값 업데이트 기능만 구현
            seekBarSensitivity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    // 값을 TextView에 표시
                    valueLabel3.setText(String.valueOf(progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            });

            // 4. 초기값 설정
            valueLabel3.setText(String.valueOf(seekBarSensitivity.getProgress()));
        }
    }
}