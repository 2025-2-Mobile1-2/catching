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
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class RoommateFragment extends Fragment {

    private Spinner spinnerGender, spinnerDormitory, spinnerAge, spinnerMbti, spinnerAlcohol, spinnerSmoking;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String myUid; // 실제로는 이메일이 들어감

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

            // ⭐ [핵심 수정] UID 대신 Email 사용
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                myUid = currentUser.getEmail(); // <--- getEmail() 사용!
                Log.d("RoommateFragment", "사용자 이메일(ID): " + myUid);
            } else {
                Toast.makeText(requireContext(), "로그인 정보 없음", Toast.LENGTH_SHORT).show();
                return;
            }

            initFragmentViews(view);
            setupAdapters();
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

        // Users -> [이메일] 문서 조회
        DocumentReference docRef = db.collection("Users").document(myUid);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!isAdded() || getContext() == null) return;

            if (documentSnapshot.exists()) {
                try {
                    setSmartSelect(spinnerGender, documentSnapshot.getString("gender"));
                    setSmartSelect(spinnerDormitory, convertDormName(documentSnapshot.getString("dorm")));
                    setSmartSelect(spinnerAge, documentSnapshot.getString("age"));
                    setSmartSelect(spinnerMbti, documentSnapshot.getString("mbti"));
                    setSmartSelect(spinnerAlcohol, convertOX(documentSnapshot.getString("alcohol")));

                    Object rawSmoking = documentSnapshot.get("smoking");
                    String smokingVal = String.valueOf(rawSmoking);
                    setSmartSelect(spinnerSmoking, convertOX(smokingVal));

                    // (카톡 ID는 부모가 하므로 여기선 생략 가능)

                } catch (Exception e) {
                    Log.e("CrashCheck", "데이터 적용 중 오류", e);
                }
            } else {
                Log.d("Firestore", "문서 없음: " + myUid);
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

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("gender", gender);
        userUpdates.put("dorm", dorm);
        userUpdates.put("age", age);
        userUpdates.put("mbti", mbti);
        userUpdates.put("alcohol", alcohol);

        boolean isSmoking = smokeStr.equals("O");
        userUpdates.put("smoking", isSmoking); // Boolean 저장 (필요시 String으로 변경)

        userUpdates.put("kakaoId", kakaoId);

        // Users -> [이메일] 문서에 저장
        db.collection("Users").document(myUid)
                .set(userUpdates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> showSaveCompleteDialog())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "저장 실패", Toast.LENGTH_SHORT).show());
    }

    // (나머지 함수들: getSpinnerString, showSaveCompleteDialog, convertDormName, convertOX, setSmartSelect는 그대로 유지)
    // 필요시 이전 코드에서 복사해 오시면 됩니다. (줄이 길어 생략했지만 내용은 같습니다)
    // -----------------------------------------------------------------------------------
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
}