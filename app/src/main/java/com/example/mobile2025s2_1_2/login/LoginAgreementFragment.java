package com.example.mobile2025s2_1_2.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile2025s2_1_2.R;

public class LoginAgreementFragment extends Fragment {

    private CheckBox agreePhoto, agreeNotice;
    private Button btnSubmit;
    private TextView termsToggle;
    private LinearLayout termsContainer;

    private boolean isExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_main, container, false);

        ImageView backBtn = view.findViewById(R.id.login_agree_back);

        // 체크박스
        agreePhoto = view.findViewById(R.id.agree_photo);
        agreeNotice = view.findViewById(R.id.agree_notice);

        // 버튼
        btnSubmit = view.findViewById(R.id.btn_submit_agree);

        // 이용약관 토글
        termsToggle = view.findViewById(R.id.terms_toggle);
        termsContainer = view.findViewById(R.id.terms_container);

        updateSubmitButtonState();

        agreePhoto.setOnCheckedChangeListener((btn, v) -> updateSubmitButtonState());
        agreeNotice.setOnCheckedChangeListener((btn, v) -> updateSubmitButtonState());

        backBtn.setOnClickListener(v -> {
            if (requireActivity() instanceof LoginActivity) {
                ((LoginActivity) requireActivity()).onAgreementCompleted(false);
            }
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        termsToggle.setOnClickListener(v -> toggleTerms());

        btnSubmit.setOnClickListener(v -> {
            if (agreePhoto.isChecked() && agreeNotice.isChecked()) {

                // LoginActivity에 동의 값 전달
                if (requireActivity() instanceof LoginActivity) {
                    ((LoginActivity) requireActivity()).onAgreementCompleted(true);
                }

                // 프래그먼트 닫기
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void toggleTerms() {
        isExpanded = !isExpanded;

        if (isExpanded) {
            termsContainer.setVisibility(View.VISIBLE);
            termsToggle.setText("▼ 서비스 이용약관 접기");
        } else {
            termsContainer.setVisibility(View.GONE);
            termsToggle.setText("▶ 서비스 이용약관 보기");
        }
    }

    private void updateSubmitButtonState() {
        boolean enabled = agreePhoto.isChecked() && agreeNotice.isChecked();
        btnSubmit.setEnabled(enabled);
        btnSubmit.setAlpha(enabled ? 1f : 0.4f);
    }
}
