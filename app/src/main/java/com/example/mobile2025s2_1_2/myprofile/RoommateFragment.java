package com.example.mobile2025s2_1_2.myprofile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoommateFragment extends Fragment {

    private Spinner spinnerGender, spinnerDormitory, spinnerAge, spinnerMbti, spinnerAlcohol, spinnerSmoking;
    private SeekBar seekBarCleanliness, seekBarSnoring, seekBarSensitivity;
    private TextView valueLabel1, valueLabel2, valueLabel3;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String myEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myprofile_fragment_roommate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            myEmail = mAuth.getCurrentUser().getEmail();
        }

        initFragmentViews(view);
        setupAdapters();
        setupSeekBars();

        if (myEmail != null) {
            loadDataFromFirestore();
        }
    }

    private void setupAdapters() {
        if (getContext() == null) return;

        setSimpleAdapter(spinnerGender, R.array.gender_array);
        setSimpleAdapter(spinnerDormitory, R.array.dormitory_array);
        setSimpleAdapter(spinnerMbti, R.array.mbti_array);
        setSimpleAdapter(spinnerAlcohol, R.array.ox_array);
        setSimpleAdapter(spinnerSmoking, R.array.ox_array);

        if (spinnerAge != null) {
            List<String> ageList = new ArrayList<>();
            ageList.add("선택");
            for (int year = 2008; year >= 1980; year--) {
                ageList.add(year + "년생");
            }
            ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ageList);
            ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAge.setAdapter(ageAdapter);
        }
    }

    private void setupSeekBars() {
        if (seekBarCleanliness != null) seekBarCleanliness.setMax(10);
        if (seekBarSnoring != null) seekBarSnoring.setMax(10);
        if (seekBarSensitivity != null) seekBarSensitivity.setMax(10);

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) {
                    seekBar.setProgress(1);
                    return;
                }

                if (seekBar == seekBarCleanliness && valueLabel1 != null) valueLabel1.setText(String.valueOf(progress));
                else if (seekBar == seekBarSnoring && valueLabel2 != null) valueLabel2.setText(String.valueOf(progress));
                else if (seekBar == seekBarSensitivity && valueLabel3 != null) valueLabel3.setText(String.valueOf(progress));
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        };

        if (seekBarCleanliness != null) seekBarCleanliness.setOnSeekBarChangeListener(listener);
        if (seekBarSnoring != null) seekBarSnoring.setOnSeekBarChangeListener(listener);
        if (seekBarSensitivity != null) seekBarSensitivity.setOnSeekBarChangeListener(listener);
    }

    private void loadDataFromFirestore() {
        db.collection("Users").document(myEmail).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            setSpinnerByText(spinnerGender, documentSnapshot.getString("gender"));
                            setSpinnerByText(spinnerDormitory, documentSnapshot.getString("dorm"));
                            setSpinnerByText(spinnerAge, documentSnapshot.getString("age"));
                            setSpinnerByText(spinnerMbti, documentSnapshot.getString("mbti"));
                            setSpinnerByText(spinnerAlcohol, documentSnapshot.getString("alcohol"));

                            Object smokeVal = documentSnapshot.get("smoking");
                            setSpinnerByText(spinnerSmoking, String.valueOf(smokeVal));

                            setSeekBarValue(seekBarCleanliness, valueLabel1, documentSnapshot.get("clean"));
                            setSeekBarValue(seekBarSnoring, valueLabel2, documentSnapshot.get("sleep"));
                            setSeekBarValue(seekBarSensitivity, valueLabel3, documentSnapshot.get("sensitive"));

                            String kakaoId = documentSnapshot.getString("kakaoId");
                            if (getActivity() != null && kakaoId != null) {
                                EditText etKakao = getActivity().findViewById(R.id.edittext_kakao_id);
                                if (etKakao != null) etKakao.setText(kakaoId);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }

    public void saveRoommateData() {
        if (myEmail == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("gender", getSpinnerString(spinnerGender));
        updates.put("dorm", getSpinnerString(spinnerDormitory));
        updates.put("age", getSpinnerString(spinnerAge));
        updates.put("mbti", getSpinnerString(spinnerMbti));
        updates.put("alcohol", getSpinnerString(spinnerAlcohol));
        updates.put("smoking", getSpinnerString(spinnerSmoking));

        updates.put("clean", String.valueOf(seekBarCleanliness.getProgress()));
        updates.put("sleep", String.valueOf(seekBarSnoring.getProgress()));
        updates.put("sensitive", String.valueOf(seekBarSensitivity.getProgress()));

        if (getActivity() != null) {
            EditText etKakao = getActivity().findViewById(R.id.edittext_kakao_id);
            if (etKakao != null) updates.put("kakaoId", etKakao.getText().toString());
        }

        db.collection("Users").document(myEmail)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    // ⭐ 저장 성공 시 팝업 띄우기
                    showSaveCompleteDialog();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "저장 실패", Toast.LENGTH_SHORT).show());
    }

    // ⭐ 팝업창 띄우는 함수 (복구됨)
    private void showSaveCompleteDialog() {
        if (!isAdded() || getContext() == null) return;

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.matching_save_complete); // 여기 레이아웃 이름 확인

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.6f);
        }
        dialog.show();

        // 1.5초 뒤에 자동으로 닫힘
        new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 1500);
    }

    private void initFragmentViews(View view) {
        spinnerGender = view.findViewById(R.id.spinner_gender);
        spinnerDormitory = view.findViewById(R.id.spinner_dormitory);
        spinnerAge = view.findViewById(R.id.spinner_age);
        spinnerMbti = view.findViewById(R.id.spinner_mbti);
        spinnerAlcohol = view.findViewById(R.id.spinner_alcohol);
        spinnerSmoking = view.findViewById(R.id.spinner_smoke);
        seekBarCleanliness = view.findViewById(R.id.seekbar_cleanliness);
        valueLabel1 = view.findViewById(R.id.seekbar_value_label1);
        seekBarSnoring = view.findViewById(R.id.seekbar_Sleeptalk);
        valueLabel2 = view.findViewById(R.id.seekbar_value_label2);
        seekBarSensitivity = view.findViewById(R.id.seekbar_sensitive);
        valueLabel3 = view.findViewById(R.id.seekbar_value_label3);
    }

    private void setSimpleAdapter(Spinner spinner, int resId) {
        if (spinner == null) return;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), resId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setSpinnerByText(Spinner spinner, String dbValue) {
        if (spinner == null || dbValue == null) return;
        String target = dbValue.trim();
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter == null) return;

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().contains(target)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void setSeekBarValue(SeekBar seekBar, TextView label, Object val) {
        if (seekBar == null || val == null) return;
        try {
            int progress = (int) Double.parseDouble(String.valueOf(val));
            seekBar.setProgress(progress);
            if (label != null) label.setText(String.valueOf(progress));
        } catch (Exception e) {}
    }

    private String getSpinnerString(Spinner spinner) {
        if (spinner != null && spinner.getSelectedItem() != null) return spinner.getSelectedItem().toString();
        return "";
    }
}