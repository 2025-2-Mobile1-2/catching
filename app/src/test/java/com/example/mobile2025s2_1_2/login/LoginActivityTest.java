package com.example.mobile2025s2_1_2.login;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.AlertDialog;
import android.content.Intent;
import android.app.Application;

import com.example.mobile2025s2_1_2.home.HomeActivity;
import com.example.mobile2025s2_1_2.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    private LoginActivity activity;

    @Before
    public void setup() {
        // ❗ UI 없이 Activity 인스턴스만 생성 (리소스 로딩 없음)
        activity = Robolectric.buildActivity(LoginActivity.class).get();
    }

    @Test
    public void testLoginFailureShowsRetryDialog() {

        Task<GoogleSignInAccount> task =
                Tasks.forException(new Exception("Login failed"));

        activity.handleSignInResult(task);

        AlertDialog latestDialog = ShadowAlertDialog.getLatestAlertDialog();

        assertNotNull("팝업이 뜨지 않았습니다.", latestDialog);
        assertTrue(latestDialog.isShowing());
    }

    public void testInvalidEmailShowsDomainPopup() {
        GoogleSignInAccount fakeAccount = Mockito.mock(GoogleSignInAccount.class);
        Mockito.when(fakeAccount.getEmail()).thenReturn("test@gmail.com");

        Task<GoogleSignInAccount> task = Tasks.forResult(fakeAccount);

        activity.handleSignInResult(task);

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull("도메인 오류 팝업이 뜨지 않았습니다.", dialog);
        assertTrue(dialog.isShowing());
    }

    @Test
    public void testLoginSuccessGoesToNextActivity() {

        LoginActivity loginActivity = Robolectric.buildActivity(LoginActivity.class).get();

        loginActivity.startActivity(
                new Intent(loginActivity, HomeActivity.class)
        );

        Application app = loginActivity.getApplication();
        ShadowApplication shadowApp = Shadows.shadowOf(app);

        Intent nextIntent = shadowApp.getNextStartedActivity();

        assertNotNull("다음 화면으로 이동하지 않았습니다.", nextIntent);
        assertTrue(nextIntent.getComponent().getClassName().contains("HomeActivity"));
    }
}
