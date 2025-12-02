package com.example.mobile2025s2_1_2.login;

import androidx.appcompat.app.AlertDialog;

import android.content.SharedPreferences;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

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
            int type = WindowInsetsCompat.Type.systemBars();

            v.setPadding(
                    insets.getInsets(type).left,
                    insets.getInsets(type).top,
                    insets.getInsets(type).right,
                    insets.getInsets(type).bottom
            );
            return insets;
        });

        // Google 로그인 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("456923291195-cm5q2ekcfa1h7upthi1klqtsq7kf77hk.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btn_google_sign_up).setOnClickListener(v -> signIn());
    }

    private void signIn() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
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

    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            if (account == null) {
                Toast.makeText(this, "로그인 실패: 계정 정보 없음", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = account.getEmail();
            Log.d("GoogleSignIn", "Success: " + email);

            // 🔥 국민대 이메일 검사 제거됨

            // FirebaseAuth 인증
            FirebaseAuth auth = FirebaseAuth.getInstance();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            auth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("GoogleSignIn", "🔥 FirebaseAuth 로그인 성공: " + auth.getCurrentUser().getUid());

                            // 자동로그인 정보 저장
                            SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("user_email", email);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(email).get()
                                    .addOnSuccessListener(doc -> {
                                        if (doc.exists()) {
                                            Intent intent = new Intent(this, com.example.mobile2025s2_1_2.home.HomeActivity.class);
                                            intent.putExtra("user_email", email);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(this, CreateProfileActivity.class);
                                            intent.putExtra("user_email", email);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "사용자 확인 실패", e);
                                        Toast.makeText(this, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                    });

                        } else {
                            Log.e("GoogleSignIn", "❌ FirebaseAuth 인증 실패", task.getException());
                            Toast.makeText(this, "Firebase 인증 실패", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Log.e("GoogleSignIn", "로그인 처리 중 오류 발생", e);
        }
    }
}
