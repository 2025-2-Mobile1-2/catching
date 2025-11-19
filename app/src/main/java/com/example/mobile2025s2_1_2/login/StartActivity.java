package com.example.mobile2025s2_1_2.login;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile2025s2_1_2.R;
import com.google.firebase.FirebaseApp;

public class StartActivity extends AppCompatActivity {

    private final Class NEXT_ACTIVITY_CLASS = LoginActivity.class;
    private ConstraintLayout mainLayout;

    // ==========================================================
    // 🎯 1. 사용자가 직접 조정할 DP 상수 (가장 중요한 부분)
    // ==========================================================

    // 로고 그룹이 최종적으로 중앙에서 왼쪽으로 이동할 거리 (음수: 왼쪽, 양수: 오른쪽)
    // 현재 -20dp로 설정 (균형점)
    private final float FINAL_SHIFT_X_DP = -55f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main_start);

        // 뷰 ID 참조
        final View subtractLogo = findViewById(R.id.subtractLogo);
        final View symbolLogo = findViewById(R.id.imageView);
        final View titleText = findViewById(R.id.imageView2);
        final View subtitleText = findViewById(R.id.imageView3);
        mainLayout = findViewById(R.id.main);

        // --- 애니메이션 초기 상태 설정 ---
        mainLayout.setClickable(false);

        symbolLogo.setAlpha(0f);
        symbolLogo.setScaleX(0f);
        symbolLogo.setScaleY(0f);
        symbolLogo.setTranslationX(0f);
        symbolLogo.setTranslationY(0f);

        titleText.setAlpha(0f);
        titleText.setTranslationX(getResources().getDisplayMetrics().density * 50);




        float slideDistance = getResources().getDisplayMetrics().density * 50;
        subtitleText.setAlpha(0f);
        subtitleText.setTranslationY(slideDistance);

        // 뷰 측정 후 애니메이션 시작 로직 (계산 로직 제거)
        startFullAnimationSequence(subtractLogo, symbolLogo, titleText, subtitleText);

        // EdgeToEdge 및 WindowInsets 설정 유지
        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });
    }

    // ======================================================================
    //                             애니메이션 시퀀스 관리
    // ======================================================================

    private void startFullAnimationSequence(View subtractLogo, View symbolLogo, View titleText, View subtitleText) {

        // 1. subtractLogo의 첫 등장 (Fade In)
        ObjectAnimator subtractFadeIn = ObjectAnimator.ofFloat(subtractLogo, View.ALPHA, 0f, 1f);
        subtractFadeIn.setDuration(800);

        // 2. subtractLogo -> symbolLogo 순식간에 교체되는 전환
        AnimatorSet instantaneousTransition = createInstantaneousTransitionAnim(subtractLogo, symbolLogo);

        // 3. 로고 이동 및 한글 제목 등장 (🌟 상수 사용)
        AnimatorSet shiftAndTextSet = createLogoShiftAndTextAnim(symbolLogo, titleText);

        // 4. 서브타이틀 등장
        AnimatorSet subtitleSet = createSubtitleAnim(subtitleText);

        AnimatorSet fullAnimation = new AnimatorSet();
        fullAnimation.playSequentially(
                subtractFadeIn,
                instantaneousTransition,
                shiftAndTextSet,
                subtitleSet
        );

        fullAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                mainLayout.setClickable(true);
                mainLayout.setOnClickListener(v -> {
                    navigateToNextScreen();
                });
            }

            @Override
            public void onAnimationCancel(Animator animator) { }
            @Override
            public void onAnimationRepeat(Animator animator) { }
        });

        fullAnimation.start();
    }

    private AnimatorSet createInstantaneousTransitionAnim(View subtractLogo, View symbolLogo) {
        AnimatorSet transitionSet = new AnimatorSet();
        long duration = 300;

        // subtractLogo Fade Out & Scale Out
        ObjectAnimator subtractFadeOut = ObjectAnimator.ofFloat(subtractLogo, View.ALPHA, 1f, 0f);
        subtractFadeOut.setDuration(duration);
        ObjectAnimator subtractScaleOutX = ObjectAnimator.ofFloat(subtractLogo, View.SCALE_X, 1f, 0.5f);
        subtractScaleOutX.setDuration(duration);
        ObjectAnimator subtractScaleOutY = ObjectAnimator.ofFloat(subtractLogo, View.SCALE_Y, 1f, 0.5f);
        subtractScaleOutY.setDuration(duration);

        AnimatorSet subtractOut = new AnimatorSet();
        subtractOut.play(subtractFadeOut).with(subtractScaleOutX).with(subtractScaleOutY);

        // symbolLogo Fade In & Scale In
        ObjectAnimator symbolFadeIn = ObjectAnimator.ofFloat(symbolLogo, View.ALPHA, 0f, 1f);
        symbolFadeIn.setDuration(duration);
        ObjectAnimator symbolScaleInX = ObjectAnimator.ofFloat(symbolLogo, View.SCALE_X, 0.5f, 1f);
        symbolScaleInX.setDuration(duration);
        ObjectAnimator symbolScaleInY = ObjectAnimator.ofFloat(symbolLogo, View.SCALE_Y, 0.5f, 1f);
        symbolScaleInY.setDuration(duration);

        AnimatorSet symbolIn = new AnimatorSet();
        symbolIn.play(symbolFadeIn).with(symbolScaleInX).with(symbolScaleInY);

        transitionSet.play(subtractOut).with(symbolIn);

        return transitionSet;
    }

    // 2단계: 로고 이동 및 한글 제목 등장 (🌟 상수 사용)
    private AnimatorSet createLogoShiftAndTextAnim(View symbolLogo, View titleText) {

        // 🌟 FINAL_SHIFT_X_DP 상수를 Pixel로 변환하여 사용
        float finalTranslationX = getResources().getDisplayMetrics().density * FINAL_SHIFT_X_DP;

        // 1. symbolMove: 0f에서 finalTranslationX까지 이동
        ObjectAnimator symbolMove = ObjectAnimator.ofFloat(symbolLogo, View.TRANSLATION_X, 0f, finalTranslationX);
        symbolMove.setDuration(700);
        symbolMove.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator textFadeIn = ObjectAnimator.ofFloat(titleText, View.ALPHA, 0f, 1f);
        textFadeIn.setDuration(500);
        textFadeIn.setInterpolator(new AccelerateDecelerateInterpolator());

        // 2. textMove: 초기 위치(+50dp)에서 최종 위치(finalTranslationX)까지 이동
        ObjectAnimator textMove = ObjectAnimator.ofFloat(titleText, View.TRANSLATION_X, titleText.getTranslationX(), finalTranslationX);
        textMove.setDuration(700);
        textMove.setInterpolator(new DecelerateInterpolator());

        AnimatorSet shiftAndTextSet = new AnimatorSet();

        shiftAndTextSet.play(symbolMove);
        shiftAndTextSet.play(textFadeIn).with(textMove).after(200);

        return shiftAndTextSet;
    }

    // 3단계: 서브타이틀 슬라이드 업 등장
    private AnimatorSet createSubtitleAnim(View subtitleText) {

        ObjectAnimator slideUp = ObjectAnimator.ofFloat(subtitleText, View.TRANSLATION_Y,
                subtitleText.getTranslationY(), 0f);
        slideUp.setDuration(600);
        slideUp.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(subtitleText, View.ALPHA, 0f, 1f);
        fadeIn.setDuration(400);

        AnimatorSet subtitleSet = new AnimatorSet();
        subtitleSet.play(slideUp).with(fadeIn);

        return subtitleSet;
    }

    private void navigateToNextScreen() {
        Intent intent = new Intent(this, NEXT_ACTIVITY_CLASS);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}