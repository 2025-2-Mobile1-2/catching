package com.example.mobile2025s2_1_2.myprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner; // ⭐ Spinner 임포트 확인

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;

public class ActivityFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // myprofile_fragment_mentor 레이아웃 로드
        return inflater.inflate(R.layout.myprofile_fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ----------------------------------------------------------------------
        // 1. 전공 (AutoCompleteTextView) 검색 기능 설정 (기존 코드 유지)
        // ----------------------------------------------------------------------
        AutoCompleteTextView autoCompleteMajor = view.findViewById(R.id.autocomplete_major);
        ArrayAdapter<CharSequence> majorAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.kookmin_college_and_major, // 학과 배열 ID
                android.R.layout.simple_dropdown_item_1line
        );

        if (autoCompleteMajor != null) {
            autoCompleteMajor.setAdapter(majorAdapter);
            autoCompleteMajor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedMajor = (String) parent.getItemAtPosition(position);
                }
            });
        }

        // ----------------------------------------------------------------------
        // 2. 다·부전공 (AutoCompleteTextView) 검색 기능 설정 (기존 코드 유지)
        // ----------------------------------------------------------------------
        AutoCompleteTextView autoCompleteSecondaryMajor = view.findViewById(R.id.autocomplete_secondary_major);
        ArrayAdapter<CharSequence> secondaryMajorAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.kookmin_college_and_major, // 연계전공 배열 ID
                android.R.layout.simple_dropdown_item_1line
        );

        if (autoCompleteSecondaryMajor != null) {
            autoCompleteSecondaryMajor.setAdapter(secondaryMajorAdapter);
        }

        // ----------------------------------------------------------------------
        // ⭐ 3. 학년 (Spinner) 드롭다운 설정 추가
        // ----------------------------------------------------------------------

        // 1. XML 레이아웃에서 Spinner 위젯 찾기 (ID: spinner_grade 가정)
        Spinner spinnerGrade = view.findViewById(R.id.spinner_grade);

        // 2. arrays.xml의 'grade_array' 배열을 가져와 ArrayAdapter 생성
        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.grade_array, // arrays.xml에 정의된 학년 배열 ID
                android.R.layout.simple_spinner_item // 기본 스피너 레이아웃 (선택 전 모습)
        );

        // 3. 드롭다운 목록이 펼쳐졌을 때의 레이아웃 설정
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 4. Spinner에 Adapter 연결 (Null 체크로 안전하게 실행)
        if (spinnerGrade != null) {
            spinnerGrade.setAdapter(gradeAdapter);

            // (선택 사항) 사용자가 학년을 선택했을 때 이벤트 처리
            spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedGrade = parent.getItemAtPosition(position).toString();
                    // 예: Log.d("Selection", "선택된 학년: " + selectedGrade);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 아무것도 선택되지 않았을 때 (보통 비워둠)
                }
            });
        }
        // ----------------------------------------------------------------------
        // ⭐ 3. 학년 (Spinner) 드롭다운 설정 추가
        // ----------------------------------------------------------------------

        // 1. XML 레이아웃에서 Spinner 위젯 찾기 (ID: spinner_grade 가정)
        Spinner spinnerTeamplay = view.findViewById(R.id.spinner_teamplay);

        // 2. arrays.xml의 'grade_array' 배열을 가져와 ArrayAdapter 생성
        ArrayAdapter<CharSequence> teamplayAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.teamplay_array, // arrays.xml에 정의된 학년 배열 ID
                android.R.layout.simple_spinner_item // 기본 스피너 레이아웃 (선택 전 모습)
        );

        // 3. 드롭다운 목록이 펼쳐졌을 때의 레이아웃 설정
        teamplayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 4. Spinner에 Adapter 연결 (Null 체크로 안전하게 실행)
        if (spinnerTeamplay != null) {
            spinnerTeamplay.setAdapter(teamplayAdapter);

            // (선택 사항) 사용자가 학년을 선택했을 때 이벤트 처리
            spinnerTeamplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedTeamplay = parent.getItemAtPosition(position).toString();
                    // 예: Log.d("Selection", "선택된 학년: " + selectedGrade);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // 아무것도 선택되지 않았을 때 (보통 비워둠)
                }
            });
        }
    }
}