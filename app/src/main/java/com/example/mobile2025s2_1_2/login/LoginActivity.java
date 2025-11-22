package com.example.mobile2025s2_1_2.login;

// 🌟 Import 구문은 그대로 유지
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler; // 🌟 Handler import 추가
import android.os.Looper; // 🌟 Looper import 추가

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

    // 🌟 전체 애니메이션 지연 시간 상수 (1초)
    private static final long INITIAL_ANIMATION_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // 뷰 ID 참조
        final TextView tvInfo1 = findViewById(R.id.tv_info1);
        final TextView tvInfo2 = findViewById(R.id.tv_info2);
        final TextView tvInfo3 = findViewById(R.id.tv_info3);
        final Button btnGoogleSignup = findViewById(R.id.btn_google_sign_up);

        // --- 🌟 애니메이션 초기화 시작 ---

        try {
            // 1. 초기 상태 설정 (투명, 아래)
            initializeViewsForAnimation(tvInfo1, tvInfo2, tvInfo3, btnGoogleSignup);

            // 2. 🌟 Handler를 사용하여 1초 뒤에 애니메이션 실행
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startSlideUpFadeInSequence(tvInfo1, tvInfo2, tvInfo3, btnGoogleSignup);
            }, INITIAL_ANIMATION_DELAY);

        } catch (Exception e) {
            // ⚠️ 애니메이션이 실패하면 뷰를 강제로 보이게 함 (안전 장치)
            Log.e("AnimationError", "Animation failed to start or views are null: " + e.getMessage());
            setViewsVisibleAndRestored(tvInfo1, tvInfo2, tvInfo3, btnGoogleSignup);
        }

        // --- 🌟 애니메이션 초기화 끝 ---


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

        // Google 로그인 옵션 설정 (기존 코드 유지)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("456923291195-cm5q2ekcfa1h7upthi1klqtsq7kf77hk.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 버튼 클릭 리스너 (기존 코드 유지)
        findViewById(R.id.btn_google_sign_up).setOnClickListener(v -> signIn());
    }

    // ======================================================================
    // 🌟 새로 추가된 애니메이션 메서드
    // ======================================================================

    /**
     * 애니메이션 시작 전, 뷰의 초기 위치(아래)와 투명도(0)를 설정합니다.
     */
    private void initializeViewsForAnimation(View... views) {
        float initialShift = getResources().getDisplayMetrics().density * 10f; // 10dp 아래로 숨김
        for (View view : views) {
            if (view != null) {
                view.setAlpha(0f);
                view.setTranslationY(initialShift);
            }
        }
    }

    /**
     * 애니메이션 실패 시 뷰를 즉시 보이게 하고 위치를 복구합니다.
     */
    private void setViewsVisibleAndRestored(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setAlpha(1f);
                view.setTranslationY(0f);
            }
        }
    }


    /**
     * 지정된 뷰들을 순차적으로 아래에서 위로 부드럽게 나타나게 하는 애니메이션
     */
    private void startSlideUpFadeInSequence(View tv1, View tv2, View tv3, View btn) {

        long duration = 500; // 애니메이션 지속 시간 (0.5초)
        long delay = 150;    // 각 항목 간의 순차적 지연 시간 (0.15초)

        // 1. tv_info1 페이드 인
        AnimatorSet anim1 = createSlideFadeAnim(tv1, duration);

        // 2. tv_info2 페이드 인
        AnimatorSet anim2 = createSlideFadeAnim(tv2, duration);

        // 3. tv_info3 페이드 인
        AnimatorSet anim3 = createSlideFadeAnim(tv3, duration);

        // 4. 버튼 페이드 인
        AnimatorSet anim4 = createSlideFadeAnim(btn, duration);

        // AnimatorSet을 사용하여 순차적으로 실행
        AnimatorSet sequence = new AnimatorSet();

        // 각 항목 시작 시 지연 시간을 추가하여 순차적인 흐름을 만듭니다.
        // 첫 번째 뷰는 딜레이 없이 시작

        anim2.setStartDelay(delay);
        anim3.setStartDelay(delay * 2);
        anim4.setStartDelay(delay * 3);

        sequence.playTogether(anim1, anim2, anim3, anim4); // 모든 애니메이션을 병렬로 설정하고 딜레이로 순서를 제어

        sequence.start();
    }

    /**
     * 단일 뷰에 대한 Slide Up + Fade In 애니메이션을 생성합니다.
     */
    private AnimatorSet createSlideFadeAnim(View view, long duration) {
        // 투명도: 0f -> 1f
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        fadeIn.setDuration(duration);

        // 위치: 초기 숨김 위치(10dp 아래) -> 0f (원래 위치)
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getTranslationY(), 0f);
        slideUp.setDuration(duration);
        slideUp.setInterpolator(new DecelerateInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.play(fadeIn).with(slideUp);
        return set;
    }

    // ======================================================================
    // 기존 로그인 로직 (변경 없음)
    // ======================================================================

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