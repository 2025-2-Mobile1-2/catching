package com.example.mobile2025s2_1_2.login;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.home.HomeActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {

    // UI 멤버 변수
    private EditText editTextName;
    private EditText editTextKakao;
    private Spinner spinnerGender, spinnerDormitory, spinnerAge, spinnerMbti, spinnerAlcohol, spinnerSmoking;
    private SeekBar seekBarCleanliness, seekBarSnoring, seekBarSensitivity;
    private TextView valueLabel1, valueLabel2, valueLabel3;
    private TextView textViewSleepTime, textViewWakeTime;
    private Button buttonComplete;

    // Firebase 관련
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userEmail;
    private String myUid; // 현재 로그인된 사용자 UID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile);

        // Firebase 초기화
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            myUid = currentUser.getUid();
        } else {
            // 로그인 안 된 상태 처리 (예: 로그인 화면으로 이동 등)
            Log.e("CreateProfile", "User not logged in");
            // myUid = "TEST_UID"; // 테스트 필요시
        }

        userEmail = getIntent().getStringExtra("user_email"); // Intent로 받은 이메일

        // 1. 뷰 초기화
        initViews();

        // 2. 버튼 초기 상태 설정
        buttonComplete.setEnabled(false);
        buttonComplete.setAlpha(0.5f);

        // 3. 어댑터 및 리스너 설정
        setupAdapters();
        setupSeekBars();
        setupListeners();

        // 4. 데이터 로드 (기존 데이터가 있다면 불러와서 세팅)
        loadDataFromFirestore();

        // 인셋 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            int type = WindowInsetsCompat.Type.systemBars();
            androidx.core.graphics.Insets systemBars = insets.getInsets(type);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextKakao = findViewById(R.id.editTextKakao); // ⭐ 추가

        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerDormitory = findViewById(R.id.spinner_dormitory);
        spinnerAge = findViewById(R.id.spinner_age);
        spinnerMbti = findViewById(R.id.spinner_mbti);
        spinnerAlcohol = findViewById(R.id.spinner_alcohol);
        spinnerSmoking = findViewById(R.id.spinner_smoke);

        seekBarCleanliness = findViewById(R.id.seekbar_cleanliness);
        valueLabel1 = findViewById(R.id.seekbar_value_label1);

        seekBarSnoring = findViewById(R.id.seekbar_snoring);
        valueLabel2 = findViewById(R.id.seekbar_value_label2);

        seekBarSensitivity = findViewById(R.id.seekbar_sensitivity);
        valueLabel3 = findViewById(R.id.seekbar_value_label3);

        textViewSleepTime = findViewById(R.id.textViewSleepTime);
        textViewWakeTime = findViewById(R.id.textViewWakeTime);
        buttonComplete = findViewById(R.id.buttonComplete);
    }

    private void setupAdapters() {
        setSpinnerAdapter(spinnerGender, R.array.gender_array);
        setSpinnerAdapter(spinnerDormitory, R.array.dormitory_array);
        setSpinnerAdapter(spinnerMbti, R.array.mbti_array);
        setSpinnerAdapter(spinnerAlcohol, R.array.ox_array);
        setSpinnerAdapter(spinnerSmoking, R.array.ox_array); // 흡연 여부 Spinner로 변경됨

        // 나이 Spinner 설정 (Java 코드로 생성)
        if (spinnerAge != null) {
            List<String> ageList = new ArrayList<>();
            ageList.add("선택");
            for (int year = 2008; year >= 1980; year--) {
                ageList.add(year + "년생");
            }
            ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ageList);
            ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerAge.setAdapter(ageAdapter);
        }
    }

    private void setSpinnerAdapter(Spinner spinner, int arrayResId) {
        if (spinner == null) return;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

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

    private void setupListeners() {
        // 시간 선택 팝업 연결
        textViewSleepTime.setOnClickListener(v -> showTimePickerDialog(textViewSleepTime, "잠드는 시간", 0, 0));
        textViewWakeTime.setOnClickListener(v -> showTimePickerDialog(textViewWakeTime, "일어나는 시간", 8, 0));

        // 필수 입력 체크 이벤트 연결
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { checkRequiredFields(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ⭐ 카카오톡 ID 입력 체크 추가
        editTextKakao.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { checkRequiredFields(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { checkRequiredFields(); }
            @Override public void onNothingSelected(AdapterView<?> parent) { checkRequiredFields(); }
        };

        // 모든 스피너에 리스너 달기 (필요에 따라 필수 항목 체크 로직에 포함)
        if(spinnerGender != null) spinnerGender.setOnItemSelectedListener(spinnerListener);
        if(spinnerDormitory != null) spinnerDormitory.setOnItemSelectedListener(spinnerListener);
        if(spinnerAge != null) spinnerAge.setOnItemSelectedListener(spinnerListener);
        if(spinnerMbti != null) spinnerMbti.setOnItemSelectedListener(spinnerListener);
        // ... 나머지 스피너들도 필요시 추가

        // 작성완료 버튼 클릭
        buttonComplete.setOnClickListener(v -> saveProfileToFirebase());
    }

    private void showTimePickerDialog(TextView targetTextView, String title, int defaultHour, int defaultMinute) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_time_picker, null);
        bottomSheetDialog.setContentView(dialogView);

        NumberPicker hourPicker = dialogView.findViewById(R.id.numberPickerHour);
        NumberPicker minutePicker = dialogView.findViewById(R.id.numberPickerMinute);
        Button confirmButton = dialogView.findViewById(R.id.buttonConfirm);
        TextView titleText = dialogView.findViewById(R.id.textViewDialogTitle);

        titleText.setText(title);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setFormatter(i -> String.format("%02d", i));

        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setFormatter(i -> String.format("%02d", i));

        String[] currentTime = targetTextView.getText().toString().split(":");
        int currentHour = defaultHour;
        int currentMinute = defaultMinute;

        if (currentTime.length == 2) {
            try { currentHour = Integer.parseInt(currentTime[0]); currentMinute = Integer.parseInt(currentTime[1]); }
            catch (NumberFormatException ignored) { }
        }

        hourPicker.setValue(currentHour);
        minutePicker.setValue(currentMinute);

        confirmButton.setOnClickListener(v -> {
            int selectedHour = hourPicker.getValue();
            int selectedMinute = minutePicker.getValue();
            String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
            targetTextView.setText(selectedTime);
            checkRequiredFields();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void checkRequiredFields() {
        // 필수 입력 조건 설정 (닉네임 필수, 나머지는 선택 or 0번째 인덱스 제외 등)
        // 예시: 닉네임 비어있지 않음 & 성별 선택됨 & 기숙사 선택됨 ...
        boolean isNameFilled = !editTextName.getText().toString().trim().isEmpty();
        boolean isKakaoFilled = !editTextKakao.getText().toString().trim().isEmpty(); // ⭐ 추가

        // 스피너 선택 여부 (0번째 항목이 '선택' 등의 기본값이라 가정할 때)
        boolean isGenderSelected = spinnerGender != null && spinnerGender.getSelectedItemPosition() != 0;
        boolean isDormSelected = spinnerDormitory != null && spinnerDormitory.getSelectedItemPosition() != 0;

        // ⭐ 카카오톡 ID도 필수 조건에 추가
        boolean allFilled = isNameFilled && isKakaoFilled && isGenderSelected && isDormSelected;

        // 필요에 따라 조건 추가

        buttonComplete.setEnabled(allFilled);
        buttonComplete.setAlpha(allFilled ? 1.0f : 0.5f);
    }

    private void loadDataFromFirestore() {
        if (myUid == null) return;

        DocumentReference docRef = db.collection("Users").document(myUid);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (isDestroyed() || isFinishing()) return;

            if (documentSnapshot.exists()) {
                try {
                    // 텍스트 필드
                    String name = documentSnapshot.getString("name");
                    if (name != null) editTextName.setText(name);

                    // ⭐ 카카오톡 ID 로드
                    String kakaoId = documentSnapshot.getString("kakaoId");
                    if (kakaoId != null) editTextKakao.setText(kakaoId);

                    // 스피너 데이터 로드
                    setSmartSelect(spinnerGender, documentSnapshot.getString("gender"));
                    setSmartSelect(spinnerDormitory, convertDormName(documentSnapshot.getString("dorm")));
                    setSmartSelect(spinnerAge, documentSnapshot.getString("age"));
                    setSmartSelect(spinnerMbti, documentSnapshot.getString("mbti"));
                    setSmartSelect(spinnerAlcohol, convertOX(documentSnapshot.getString("alcohol")));

                    // 흡연 (DB값이 boolean이거나 String일 수 있음 처리)
                    Object rawSmoking = documentSnapshot.get("smoking");
                    String smokingVal = String.valueOf(rawSmoking); // "true", "false", "O", "X" 등
                    setSmartSelect(spinnerSmoking, convertOX(smokingVal));

                    // SeekBar 데이터 로드
                    setSeekBarValue(seekBarCleanliness, valueLabel1, String.valueOf(documentSnapshot.get("clean")));
                    setSeekBarValue(seekBarSnoring, valueLabel2, String.valueOf(documentSnapshot.get("sleep")));
                    setSeekBarValue(seekBarSensitivity, valueLabel3, String.valueOf(documentSnapshot.get("sensitive")));

                    // 시간 설정
                    String sleepTime = documentSnapshot.getString("sleepTime");
                    if (sleepTime != null) textViewSleepTime.setText(sleepTime);

                    String wakeTime = documentSnapshot.getString("wakeTime");
                    if (wakeTime != null) textViewWakeTime.setText(wakeTime);

                    Toast.makeText(this, "기존 데이터를 불러왔습니다.", Toast.LENGTH_SHORT).show();
                    checkRequiredFields(); // 데이터 로드 후 버튼 상태 갱신

                } catch (Exception e) {
                    Log.e("CreateProfile", "데이터 적용 중 오류", e);
                }
            }
        }).addOnFailureListener(e -> Log.e("CreateProfile", "연결 실패", e));
    }

    private void saveProfileToFirebase() {
        Log.d("DEBUG", "Complete 버튼 클릭됨");

        // 1. 입력값 가져오기
        String name = editTextName.getText().toString().trim();
        String kakaoId = editTextKakao.getText().toString().trim(); // ⭐ 추가
        String gender = getSpinnerString(spinnerGender);
        String dorm = getSpinnerString(spinnerDormitory);
        String age = getSpinnerString(spinnerAge);
        String mbti = getSpinnerString(spinnerMbti);
        String alcohol = getSpinnerString(spinnerAlcohol);
        String smokeStr = getSpinnerString(spinnerSmoking); // O, X

        String cleanVal = String.valueOf(seekBarCleanliness.getProgress());
        String sleepVal = String.valueOf(seekBarSnoring.getProgress());
        String sensitiveVal = String.valueOf(seekBarSensitivity.getProgress());

        String sleepTime = textViewSleepTime.getText().toString();
        String wakeTime = textViewWakeTime.getText().toString();

        // 2. Map 생성
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("email", userEmail);
        userProfile.put("name", name);
        userProfile.put("kakaoId", kakaoId); // ⭐ 추가
        userProfile.put("gender", gender);
        userProfile.put("dorm", dorm);
        userProfile.put("age", age);
        userProfile.put("mbti", mbti);
        userProfile.put("alcohol", alcohol);
        userProfile.put("smoking", smokeStr); // "O" or "X"
        userProfile.put("clean", cleanVal);
        userProfile.put("sleep", sleepVal);
        userProfile.put("sensitive", sensitiveVal);
        userProfile.put("sleepTime", sleepTime);
        userProfile.put("wakeTime", wakeTime);

        // 3. Firestore 저장
        Log.d("Users", "🔥 Firestore 저장 시도 중...");
        if (myUid != null) {
            db.collection("Users").document(userEmail).set(userProfile, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        // SharedPreferences 저장
                        getSharedPreferences("user_prefs", MODE_PRIVATE)
                                .edit()
                                .putString("user_email", userEmail)
                                .apply();

                        Log.d("DEBUG", "유저 정보 저장됨");
                        Toast.makeText(this, "프로필 저장 완료!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DEBUG", "❌ Firestore 저장 실패: " + e.getMessage(), e);
                        Toast.makeText(this, "저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // 유틸리티 함수들
    private String getSpinnerString(Spinner spinner) {
        if (spinner != null && spinner.getSelectedItem() != null) {
            return spinner.getSelectedItem().toString();
        }
        return "";
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

    private String convertDormName(String dbValue) {
        if (dbValue == null) return "";
        if (dbValue.contains("B동") || dbValue.contains("A동") || dbValue.contains("C동")) {
            return "교내생활관"; // 예시 변환 로직
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

    private void setSeekBarValue(SeekBar seekBar, TextView label, String value) {
        if (seekBar == null || value == null || value.equals("null")) return;
        try {
            double d = Double.parseDouble(value);
            int progress = (int) d;
            seekBar.setProgress(progress);
            if (label != null) {
                label.setText(String.valueOf(progress));
            }
        } catch (NumberFormatException e) {
            Log.e("CreateProfile", "SeekBar 숫자 변환 실패: " + value);
        }
    }
}