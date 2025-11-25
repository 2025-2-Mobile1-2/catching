package com.example.mobile2025s2_1_2.myprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView; // ⭐ 변경: AutoCompleteTextView 임포트
import android.widget.Spinner; // Spinner는 사용하지 않지만 기존 코드에 있었으므로 유지

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;

public class MentorFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ✅ 수정: myprofile_fragment_mentor 레이아웃 파일을 로드합니다.
        return inflater.inflate(R.layout.myprofile_fragment_mentor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ----------------------------------------------------------------------
        // 1. 전공 (AutoCompleteTextView) 검색 기능 설정
        // ----------------------------------------------------------------------

        // ✅ 변경: Spinner 대신 AutoCompleteTextView 위젯을 참조합니다. (ID도 XML에 맞게 가정)
        AutoCompleteTextView autoCompleteMajor = view.findViewById(R.id.autocomplete_major);

        // a. arrays.xml의 'kmu_all_majors_array'를 Adapter에 연결
        ArrayAdapter<CharSequence> majorAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                // ✅ 수정: 정확한 배열 ID를 사용합니다.
                R.array.kookmin_college_and_major,
                // 검색 추천 목록에 적합한 드롭다운 레이아웃을 사용합니다.
                android.R.layout.simple_dropdown_item_1line
        );

        // c. AutoCompleteTextView에 Adapter 연결
        if (autoCompleteMajor != null) { // Null 체크로 충돌 방지
            autoCompleteMajor.setAdapter(majorAdapter);

            // (선택 사항: 항목 선택 시 이벤트 처리)
            autoCompleteMajor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 사용자가 목록에서 항목을 선택했을 때의 로직을 여기에 추가합니다.
                    String selectedMajor = (String) parent.getItemAtPosition(position);
                    // 예: Log.d("Selection", "선택된 학과: " + selectedMajor);
                }
            });
        }

        // ----------------------------------------------------------------------
        // 2. 다·부전공 (AutoCompleteTextView) 검색 기능 설정
        // ----------------------------------------------------------------------

        // ✅ 변경: 다·부전공도 AutoCompleteTextView로 가정합니다. (ID도 XML에 맞게 가정)
        AutoCompleteTextView autoCompleteSecondaryMajor = view.findViewById(R.id.autocomplete_secondary_major);

        // a. arrays.xml의 'kmu_interdisciplinary_majors_array'를 Adapter에 연결
        ArrayAdapter<CharSequence> secondaryMajorAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                // ✅ 수정: 정확한 배열 ID를 사용합니다.
                R.array.kookmin_college_and_major,
                android.R.layout.simple_dropdown_item_1line
        );

        // c. AutoCompleteTextView에 Adapter 연결
        if (autoCompleteSecondaryMajor != null) { // Null 체크로 충돌 방지
            autoCompleteSecondaryMajor.setAdapter(secondaryMajorAdapter);
        }
    }
}