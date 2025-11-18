package com.example.mobile2025s2_1_2.notification;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile2025s2_1_2.*;

public class ProfilePopActivity extends AppCompatActivity {

    private Button btnShowProfile;
    private Dialog profileDialog;
    private Dialog confirmDialog;

    private boolean shouldReopenProfilePopup = false; // 🔥 추가됨

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_test);

        btnShowProfile = findViewById(R.id.btn_show_profile);

        btnShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfilePopup();
            }
        });
    }

    private void showProfilePopup() {
        profileDialog = new Dialog(ProfilePopActivity.this);

        profileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        profileDialog.setContentView(R.layout.profile_popup);

        if (profileDialog.getWindow() != null) {
            profileDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#80000000"))
            );
            profileDialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
            );
        }

        profileDialog.setCancelable(true);

        ImageView btnClose = profileDialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> profileDialog.dismiss());

        ImageView btnAccept = profileDialog.findViewById(R.id.btn_accept);
        ImageView btnReject = profileDialog.findViewById(R.id.btn_reject);

        btnAccept.setOnClickListener(v -> {
            profileDialog.dismiss();
            showConfirmPopup();
        });

        btnReject.setOnClickListener(v -> profileDialog.dismiss());

        profileDialog.show();
    }

    private void showConfirmPopup() {
        confirmDialog = new Dialog(ProfilePopActivity.this);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(R.layout.profile_popup2);

        if (confirmDialog.getWindow() != null) {
            confirmDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#80000000"))
            );
            confirmDialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
            );
        }

        confirmDialog.setCancelable(true);

        View btnConfirm = confirmDialog.findViewById(R.id.btn_confirm_layout);
        btnConfirm.setOnClickListener(v -> {
            confirmDialog.dismiss();
            shouldReopenProfilePopup = true;
            recreate();
        });

        confirmDialog.show();
    }

    // 🔥 finish() 후 액티비티로 돌아오면 ProfilePopup1 다시 뜨도록
    @Override
    protected void onResume() {
        super.onResume();

        if (shouldReopenProfilePopup) {
            shouldReopenProfilePopup = false;
            showProfilePopup();
        }
    }
}
