package com.example.mobile2025s2_1_2.myprofile;

// 1. 필요한 import 구문들 추가
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // 👈 1. 이게 가장 중요!

import com.example.mobile2025s2_1_2.R; // 👈 2. R 파일 import

// 3. 'extends Fragment' 추가
public class ActivityFragment extends Fragment {

    // 4. onCreateView 추가 (fragment_activity.xml을 연결)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_activity.xml 파일을 화면에 띄웁니다.
        return inflater.inflate(R.layout.myprofile_fragment_activity, container, false);
    }
}