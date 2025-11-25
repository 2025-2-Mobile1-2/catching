package com.example.mobile2025s2_1_2.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.login.StartActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccountDialogFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String myemail = prefs.getString("user_email", null);


        View view = inflater.inflate(R.layout.settings_delete_account_dialog, container, false);

        //삭제 버튼
        TextView delete = view.findViewById(R.id.account_delete_ok);
        delete.setOnClickListener(v ->{
            SettingsFragment.touchBlocker.setVisibility(View.GONE);
            db.collection("Users")
                    .document(myemail)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "문서 삭제 성공!");

                        // 🔥 다이얼로그 종료
                        requireActivity().getSupportFragmentManager().popBackStack();

                        // 🔥 SharedPreferences 초기화 (자동 로그아웃)
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.apply();

                        // 🔥 로그인 화면으로 이동
                        Intent intent = new Intent(requireActivity(), StartActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .addOnFailureListener(e ->
                            Log.e("Firestore", "문서 삭제 실패", e)
                    );
        });


        //취소버틍
        TextView deleteCancel = view.findViewById(R.id.account_delete_cancel);
        deleteCancel.setOnClickListener(v -> {
            SettingsFragment.touchBlocker.setVisibility(View.GONE);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        return view;
    }
}
