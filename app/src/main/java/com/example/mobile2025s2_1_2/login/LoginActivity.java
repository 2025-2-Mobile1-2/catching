package com.example.mobile2025s2_1_2.login;

import androidx.appcompat.app.AlertDialog;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.view.View;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.Toast;

import com.example.mobile2025s2_1_2.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            int type = WindowInsetsCompat.Type.systemBars(); // ✅ 수정된 부분

            v.setPadding(
                    insets.getInsets(type).left,
                    insets.getInsets(type).top,
                    insets.getInsets(type).right,
                    insets.getInsets(type).bottom
            );
            return insets;
        });
        // ✅ Google 로그인 옵션 설정 (이메일 요청)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("456923291195-cm5q2ekcfa1h7upthi1klqtsq7kf77hk.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btn_google_sign_up).setOnClickListener(v -> signIn());

    }

    private void signIn() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            //mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, revokeTask -> {
                // 완전 초기화 후 로그인 창 띄우기
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            //});
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // ✅ 로그인 성공 시
            String email = account.getEmail();
            //Toast.makeText(this, "로그인 성공: " + email, Toast.LENGTH_SHORT).show();
            Log.d("GoogleSignIn", "Success: " + email);
            // ✨ 이메일 도메인 검사 추가
            if (email != null && email.endsWith("@kookmin.ac.kr")) {
                // ✨ InfoRecordActivity로 이동
                Intent intent = new Intent(this, CreateProfileActivity.class);
                intent.putExtra("user_email", email); // 필요하면 이메일 전달
                startActivity(intent);
                finish(); // ✨ MainActivity 종료 (뒤로가기 방지)
            } else {
                // 커스텀 AlertDialog
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_message, null);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button btnConfirm = dialogView.findViewById(R.id.dialog_button);
                btnConfirm.setOnClickListener(v -> dialog.dismiss());

                dialog.show();
            }

        } catch (ApiException e) {
            // ❌ 로그인 실패 시
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
    }
}
