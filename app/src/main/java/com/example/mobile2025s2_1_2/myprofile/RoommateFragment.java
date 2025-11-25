package com.example.mobile2025s2_1_2.myprofile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoommateFragment extends Fragment {

    // 기존 스피너
    private Spinner spinnerGender, spinnerDormitory, spinnerAge, spinnerMbti, spinnerAlcohol, spinnerSmoking;

    // ⭐ SeekBar 및 라벨
    private SeekBar seekBarCleanliness, seekBarSnoring, seekBarSensitivity;
    private TextView valueLabel1, valueLabel2, valueLabel3;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String myUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myprofile_fragment_roommate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                myUid = currentUser.getUid();
            } else {
                myUid = "hQlLVKfBya7shEe3adhl"; // 테스트용
            }

            initFragmentViews(view);
            setupAdapters();
            setupSeekBars(); // ⭐ SeekBar 설정

            loadDataFromFirestore();

        } catch (Exception e) {
            Log.e("CrashCheck", "초기화 중 오류 발생", e);
        }
    }

    private void initFragmentViews(View view) {
        spinnerGender = view.findViewById(R.id.spinner_gender);
        spinnerDormitory = view.findViewById(R.id.spinner_dormitory);
        spinnerAge = view.findViewById(R.id.spinner_age);
        spinnerMbti = view.findViewById(R.id.spinner_mbti);
        spinnerAlcohol = view.findViewById(R.id.spinner_alcohol);
        spinnerSmoking = view.findViewById(R.id.spinner_smoke);

        // ⭐ SeekBar 연결
        seekBarCleanliness = view.findViewById(R.id.seekbar_cleanliness);
        valueLabel1 = view.findViewById(R.id.seekbar_value_label1);

        seekBarSnoring = view.findViewById(R.id.seekbar_Sleeptalk);
        valueLabel2 = view.findViewById(R.id.seekbar_value_label2);

        seekBarSensitivity = view.findViewById(R.id.seekbar_sensitive);
        valueLabel3 = view.findViewById(R.id.seekbar_value_label3);
    }

    private void setupAdapters() {
        if (getContext() == null) return;
        setSpinnerAdapter(spinnerGender, R.array.gender_array);
        setSpinnerAdapter(spinnerDormitory, R.array.dormitory_array);
        setSpinnerAdapter(spinnerMbti, R.array.mbti_array);
        setSpinnerAdapter(spinnerAlcohol, R.array.ox_array);
        setSpinnerAdapter(spinnerSmoking, R.array.ox_array);

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

    // ⭐ SeekBar 리스너 설정
    private void setupSeekBars() {
        setupSingleSeekBar(seekBarCleanliness, valueLabel1);
        setupSingleSeekBar(seekBarSnoring, valueLabel2);
        setupSingleSeekBar(seekBarSensitivity, valueLabel3);
    }

    private void setupSingleSeekBar(SeekBar seekBar, TextView label) {
        if (seekBar != null && label != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    label.setText(String.valueOf(progress));
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override public void onStopTrackingTouch(SeekBar seekBar) { }
            });
            // 초기값 표시
            label.setText(String.valueOf(seekBar.getProgress()));
        }
    }

    private void setSpinnerAdapter(Spinner spinner, int arrayResId) {
        if (spinner == null) return;
        try {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    requireContext(), arrayResId, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            Log.e("CrashCheck", "어댑터 설정 오류 ID: " + arrayResId);
        }
    }

    private void loadDataFromFirestore() {
        if (myUid == null) return;

        DocumentReference docRef = db.collection("Users").document(myUid);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!isAdded() || getContext() == null) return;

            if (documentSnapshot.exists()) {
                try {
                    // 1. 스피너 데이터 로드
                    setSmartSelect(spinnerGender, documentSnapshot.getString("gender"));
                    setSmartSelect(spinnerDormitory, convertDormName(documentSnapshot.getString("dorm")));
                    setSmartSelect(spinnerAge, documentSnapshot.getString("age"));
                    setSmartSelect(spinnerMbti, documentSnapshot.getString("mbti"));
                    setSmartSelect(spinnerAlcohol, convertOX(documentSnapshot.getString("alcohol")));

                    Object rawSmoking = documentSnapshot.get("smoking");
                    String smokingVal = String.valueOf(rawSmoking);
                    setSmartSelect(spinnerSmoking, convertOX(smokingVal));

                    // ⭐ 2. SeekBar 데이터 로드 (clean, sleep, sensitive)
                    // DB에서 String("5")으로 오든 Number(5)로 오든 처리
                    setSeekBarValue(seekBarCleanliness, valueLabel1, String.valueOf(documentSnapshot.get("clean")));
                    setSeekBarValue(seekBarSnoring, valueLabel2, String.valueOf(documentSnapshot.get("sleep")));
                    setSeekBarValue(seekBarSensitivity, valueLabel3, String.valueOf(documentSnapshot.get("sensitive")));

                    // 3. 카톡 ID
                    String kakaoId = documentSnapshot.getString("kakaoId");
                    EditText etKakao = requireActivity().findViewById(R.id.edittext_kakao_id);
                    if (etKakao != null && kakaoId != null) {
                        etKakao.setText(kakaoId);
                    }
                } catch (Exception e) {
                    Log.e("CrashCheck", "데이터 적용 중 오류", e);
                }
            }
        }).addOnFailureListener(e -> Log.e("CrashCheck", "연결 실패", e));
    }

    public void saveRoommateData() {
        if (myUid == null || !isAdded()) return;

        String gender = getSpinnerString(spinnerGender);
        String dorm = getSpinnerString(spinnerDormitory);
        String age = getSpinnerString(spinnerAge);
        String mbti = getSpinnerString(spinnerMbti);
        String alcohol = getSpinnerString(spinnerAlcohol);
        String smokeStr = getSpinnerString(spinnerSmoking);

        EditText etKakao = requireActivity().findViewById(R.id.edittext_kakao_id);
        String kakaoId = (etKakao != null) ? etKakao.getText().toString() : "";

        // ⭐ SeekBar 값 가져오기
        String cleanVal = (seekBarCleanliness != null) ? String.valueOf(seekBarCleanliness.getProgress()) : "1";
        String sleepVal = (seekBarSnoring != null) ? String.valueOf(seekBarSnoring.getProgress()) : "1";
        String sensitiveVal = (seekBarSensitivity != null) ? String.valueOf(seekBarSensitivity.getProgress()) : "1";

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("gender", gender);
        userUpdates.put("dorm", dorm);
        userUpdates.put("age", age);
        userUpdates.put("mbti", mbti);
        userUpdates.put("alcohol", alcohol);

        boolean isSmoking = smokeStr.equals("O");
        userUpdates.put("smoking", isSmoking ? "O" : "X");

        // ⭐ SeekBar 데이터 추가
        userUpdates.put("clean", cleanVal);
        userUpdates.put("sleep", sleepVal);
        userUpdates.put("sensitive", sensitiveVal);

        userUpdates.put("kakaoId", kakaoId);

        db.collection("Users").document(myUid)
                .set(userUpdates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> showSaveCompleteDialog())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "저장 실패", Toast.LENGTH_SHORT).show());
    }

    private String getSpinnerString(Spinner spinner) {
        if (spinner != null && spinner.getSelectedItem() != null) {
            return spinner.getSelectedItem().toString();
        }
        return "";
    }

    private void showSaveCompleteDialog() {
        if (!isAdded() || getContext() == null) return;

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.matching_save_complete);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.6f);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        dialog.show();
        new Handler(Looper.getMainLooper()).postDelayed(dialog::dismiss, 1500);
        dialog.findViewById(android.R.id.content).setOnClickListener(v -> dialog.dismiss());
    }

    private String convertDormName(String dbValue) {
        if (dbValue == null) return "";
        if (dbValue.contains("B동") || dbValue.contains("A동") || dbValue.contains("C동")) {
            return "교내생활관";
        }
        return dbValue;
    }

    private String convertOX(String dbValue) {
        if (dbValue == null) return "";
        if (dbValue.equalsIgnoreCase("true") || dbValue.equals("음주") || dbValue.equals("흡연") || dbValue.equals("O") || dbValue.equals("있음")) {
            return "O";
        }
        if (dbValue.equalsIgnoreCase("false") || dbValue.equals("비음주") || dbValue.equals("비흡연") || dbValue.equals("X") || dbValue.equals("없음")) {
            return "X";
        }
        return dbValue;
    }

    private void setSmartSelect(Spinner spinner, String... targets) {
        if (spinner == null || spinner.getAdapter() == null) return;
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (String target : targets) {
            if (target == null) continue;
            for (int i = 0; i < adapter.getCount(); i++) {
                String item = adapter.getItem(i).toString();
                if (item.trim().equals(target.trim())) {
                    spinner.setSelection(i);
                    return;
                }
            }
        }
    }

    // ⭐ SeekBar 값 설정 도우미
    private void setSeekBarValue(SeekBar seekBar, TextView label, String value) {
        if (seekBar == null || value == null || value.equals("null")) return;
        try {
            // 소수점(.0)이 있을 경우 제거하고 정수로 변환
            double d = Double.parseDouble(value);
            int progress = (int) d;

            seekBar.setProgress(progress);
            if (label != null) {
                label.setText(String.valueOf(progress));
            }
        } catch (NumberFormatException e) {
            Log.e("CrashCheck", "SeekBar 숫자 변환 실패: " + value);
        }
    }
}