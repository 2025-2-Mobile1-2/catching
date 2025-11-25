package com.example.mobile2025s2_1_2.login;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.home.HomeActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;        // 🎯 추가
import android.text.TextWatcher;     // 🎯 추가
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;       // 🎯 추가
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;         // 🎯 추가
import android.widget.TextView;
import android.widget.AdapterView;   // 🎯 추가
import android.widget.Toast;         // ☁️ Firebase 저장 후 토스트

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;   // ☁️ Firestore

import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {

    // 🎯 수정: 멤버 변수 추가
    private EditText editTextName;
    private Spinner spinnerDepartment, spinnerGrade, spinnerMbti;
    private TextView textViewSleepTime, textViewWakeTime;
    private Switch switchSnoring, switchSmoking;
    private Button buttonComplete;

    // ☁️ Firebase 저장용 이메일
    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // CreateProfileActivity onCreate 안에서
        userEmail = getIntent().getStringExtra("user_email"); // ☁️ 구글 이메일 받기

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile);

        // 🎯 뷰 초기화
        editTextName = findViewById(R.id.editTextName);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerGrade = findViewById(R.id.spinnerGrade);
        spinnerMbti = findViewById(R.id.spinnerMbti);
        textViewSleepTime = findViewById(R.id.textViewSleepTime);
        textViewWakeTime = findViewById(R.id.textViewWakeTime);
        switchSnoring = findViewById(R.id.switchSnoring);
        switchSmoking = findViewById(R.id.switchSmoking);
        buttonComplete = findViewById(R.id.buttonComplete);

        // 🎯 버튼 초기 상태 비활성화
        buttonComplete.setEnabled(false);    // ⚡
        buttonComplete.setAlpha(0.5f);       // ⚡

        // 학과 Spinner
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.department_array,
                android.R.layout.simple_spinner_item
        );
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        // 학년 Spinner
        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.grade_array,
                android.R.layout.simple_spinner_item
        );
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(gradeAdapter);

        // MBTI Spinner
        ArrayAdapter<CharSequence> mbtiAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.mbti_array,
                android.R.layout.simple_spinner_item
        );
        mbtiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMbti.setAdapter(mbtiAdapter);

        // ▼▼▼ '잠드는 시간' 팝업 코드 ▼▼▼
        textViewSleepTime.setOnClickListener(v -> showTimePickerDialog(textViewSleepTime, "잠드는 시간", 0, 0));

        // ▼▼▼ '일어나는 시간' 팝업 코드 ▼▼▼
        textViewWakeTime.setOnClickListener(v -> showTimePickerDialog(textViewWakeTime, "일어나는 시간", 8, 0));

        // 🎉 필수 입력 체크 이벤트 연결
        editTextName.addTextChangedListener(new TextWatcher() {     // ⚡
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { checkRequiredFields(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {  // ⚡
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { checkRequiredFields(); }
            @Override public void onNothingSelected(AdapterView<?> parent) { checkRequiredFields(); }
        };
        spinnerDepartment.setOnItemSelectedListener(spinnerListener);
        spinnerGrade.setOnItemSelectedListener(spinnerListener);
        spinnerMbti.setOnItemSelectedListener(spinnerListener);

        switchSnoring.setOnCheckedChangeListener((buttonView, isChecked) -> checkRequiredFields());  // 🔄
        switchSmoking.setOnCheckedChangeListener((buttonView, isChecked) -> checkRequiredFields());  // 🔄

        // ☁️ 작성완료 버튼 클릭 시 Firebase 저장
        buttonComplete.setOnClickListener(v -> saveProfileToFirebase());

        // 인셋 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            int type = WindowInsetsCompat.Type.systemBars();
            androidx.core.graphics.Insets systemBars = insets.getInsets(type);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * 시간 선택 팝업 (BottomSheetDialog)
     */
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

        confirmButton.setOnClickListener(v -> {      // ⚡
            int selectedHour = hourPicker.getValue();
            int selectedMinute = minutePicker.getValue();
            String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
            targetTextView.setText(selectedTime);
            checkRequiredFields();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    // ✅ 필수 입력 체크 함수
    private void checkRequiredFields() {          // ⚡
        boolean allFilled = !editTextName.getText().toString().trim().isEmpty()
                && spinnerDepartment.getSelectedItemPosition() != 0
                && spinnerGrade.getSelectedItemPosition() != 0;

        buttonComplete.setEnabled(allFilled);   // ⚡
        buttonComplete.setAlpha(allFilled ? 1.0f : 0.5f); // ⚡
    }

    // ☁️ Firebase 저장 함수
    private void saveProfileToFirebase() {
        Log.d("DEBUG", "Complete 버튼 클릭됨"); // ← 추가
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestore.setLoggingEnabled(true);

        Log.d("DEBUG", "Firestore 인스턴스: " + db);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        Log.d("DEBUG", "네트워크 연결: " + isConnected);

        FirebaseFirestore.getInstance().collection("test").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d("DEBUG", "📡 Firestore 연결 성공");
                    else Log.e("DEBUG", "📡 Firestore 연결 실패", task.getException());
                });


        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("email", userEmail);
        userProfile.put("name", editTextName.getText().toString().trim());
        userProfile.put("department", spinnerDepartment.getSelectedItem().toString());
        userProfile.put("grade", spinnerGrade.getSelectedItem().toString());
        userProfile.put("mbti", spinnerMbti.getSelectedItem().toString());
        userProfile.put("sleepTime", textViewSleepTime.getText().toString());
        userProfile.put("wakeTime", textViewWakeTime.getText().toString());
        userProfile.put("snoring", switchSnoring.isChecked());
        userProfile.put("smoking", switchSmoking.isChecked());

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("hello", "world");

        db.collection("testCollection").document("testDoc")
                .set(testMap)
                .addOnSuccessListener(aVoid -> Log.d("DEBUG","✅ Test 저장 성공"))
                .addOnFailureListener(e -> Log.e("DEBUG","❌ Test 저장 실패", e));



        Log.d("Users", "🔥 Firestore 저장 시도 중...");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users").document(uid).set(userProfile)
                .addOnSuccessListener(aVoid -> {
                    Log.d("DEBUG", "유저 정보 저장됨  ");
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
