package com.example.mobile2025s2_1_2.login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    // 애니메이션 딜레이 상수
    private static final long INITIAL_ANIMATION_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // 뷰 참조
        final TextView tvInfo1 = findViewById(R.id.tv_info1);
        final TextView tvInfo2 = findViewById(R.id.tv_info2);
        final TextView tvInfo3 = findViewById(R.id.tv_info3);
        final Button btnGoogleSignup = findViewById(R.id.btn_google_sign_up);

        // --- 애니메이션 초기화 및 실행 (충돌 해결됨) ---
        try {
            // 1. 뷰들을 처음에 안 보이게(투명+아래로) 설정
            initializeViewsForAnimation(tvInfo1, tvInfo2, tvInfo3, btnGoogleSignup);

            // 2. 1초 뒤에 애니메이션 시작
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startSlideUpFadeInSequence(tvInfo1, tvInfo2, tvInfo3, btnGoogleSignup);
            }, INITIAL_ANIMATION_DELAY);

        } catch (Exception e) {
            Log.e("AnimationError", "Animation failed: " + e.getMessage());
            setViewsVisibleAndRestored(tvInfo1, tvInfo2, tvInfo3, btnGoogleSignup);
        }

        // WindowInsets 설정
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
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
        }

        // Google 로그인 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("456923291195-cm5q2ekcfa1h7upthi1klqtsq7kf77hk.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 버튼 리스너
        if (btnGoogleSignup != null) {
            btnGoogleSignup.setOnClickListener(v -> signIn());
        }
    }

    // ======================================================================
    // 애니메이션 메서드들
    // ======================================================================

    private void initializeViewsForAnimation(View... views) {
        float initialShift = getResources().getDisplayMetrics().density * 10f;
        for (View view : views) {
            if (view != null) {
                view.setAlpha(0f);
                view.setTranslationY(initialShift);
            }
        }
    }

    private void setViewsVisibleAndRestored(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setAlpha(1f);
                view.setTranslationY(0f);
            }
        }
    }

    private void startSlideUpFadeInSequence(View tv1, View tv2, View tv3, View btn) {
        long duration = 500;
        long delay = 150;

        AnimatorSet anim1 = createSlideFadeAnim(tv1, duration);
        AnimatorSet anim2 = createSlideFadeAnim(tv2, duration);
        AnimatorSet anim3 = createSlideFadeAnim(tv3, duration);
        AnimatorSet anim4 = createSlideFadeAnim(btn, duration);

        AnimatorSet sequence = new AnimatorSet();
        anim2.setStartDelay(delay);
        anim3.setStartDelay(delay * 2);
        anim4.setStartDelay(delay * 3);

        sequence.playTogether(anim1, anim2, anim3, anim4);
        sequence.start();
    }

    private AnimatorSet createSlideFadeAnim(View view, long duration) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        fadeIn.setDuration(duration);

        ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getTranslationY(), 0f);
        slideUp.setDuration(duration);
        slideUp.setInterpolator(new DecelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.play(fadeIn).with(slideUp);
        return set;
    }

    // ======================================================================
    // 로그인 로직
    // ======================================================================

    private void signIn() {
        // 기존 로그인 기록이 있으면 지우고 다시 로그인 창 띄우기 (깔끔한 방식)
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String email = account.getEmail();

            // 이메일 도메인 확인 (@kookmin.ac.kr)
            if (email != null && email.endsWith("@kookmin.ac.kr")) {
                Intent intent = new Intent(this, CreateProfileActivity.class);
                intent.putExtra("user_email", email);
                startActivity(intent);
                finish();
            } else {
                // 경고 다이얼로그
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_message, null);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                Button btnConfirm = dialogView.findViewById(R.id.dialog_button);
                btnConfirm.setOnClickListener(v -> dialog.dismiss());
                dialog.show();

                // 실패 시 로그아웃 처리하여 다음 시도 가능하게 함
                mGoogleSignInClient.signOut();
            }

        } catch (ApiException e) {
            Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
    }
}