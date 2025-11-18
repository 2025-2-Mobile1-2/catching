package com.example.mobile2025s2_1_2.notification;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    // 토글
    private RelativeLayout toggleReceived, toggleSent;
    private TextView tvReceived, tvSent;

    private Dialog profileDialog;
    private Dialog confirmDialog;

    // ★ 추가됨
    private Dialog kakaoDialog;    // 보낸 매칭 팝업용 다이얼로그

    // 리스트
    private RecyclerView recycler;
    private AlarmAdapter adapter;

    // 테스트 데이터
    private final List<AlarmItem> received = Arrays.asList(
            new AlarmItem("최북악 님으로부터 진로·전공 멘토 매칭 신청이 왔습니다!", true),
            new AlarmItem("최북악 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!", false)
    );

    private final List<AlarmItem> sent = Arrays.asList(
            new AlarmItem("김국민 님께 진로·전공 멘토 매칭 신청을 보냈습니다!", false),
            new AlarmItem("홍지우 님께 교내·교외 활동 팀원 매칭 신청을 보냈습니다!", true)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_main);

        // 하단 navBar
        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_notification);

        // 토글 뷰
        toggleReceived = findViewById(R.id.alarm_toggle_r);
        toggleSent     = findViewById(R.id.alarm_toggle_s);
        tvReceived     = findViewById(R.id.tv_received);
        tvSent         = findViewById(R.id.tv_sent);

        // RecyclerView
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setClipChildren(false);
        recycler.setClipToPadding(false);

        // 초기: 받은 매칭 활성
        setToggleState(false);
        showReceived();

        // 토글 클릭
        toggleReceived.setOnClickListener(v -> {
            setToggleState(false);
            showReceived();
        });
        toggleSent.setOnClickListener(v -> {
            setToggleState(true);
            showSent();
        });
    }

    /** 받은 탭 */
    private void showReceived() {
        adapter = new AlarmAdapter(new ArrayList<>(received), true);
        recycler.setAdapter(adapter);
    }

    /** 보낸 탭 */
    private void showSent() {
        adapter = new AlarmAdapter(new ArrayList<>(sent), false);
        recycler.setAdapter(adapter);
    }

    /** 토글 UI 전환 */
    private void setToggleState(boolean isSentActive) {
        Typeface semi = ResourcesCompat.getFont(this, R.font.semibold);
        Typeface reg  = ResourcesCompat.getFont(this, R.font.regular);

        if (isSentActive) {
            toggleSent.setBackgroundResource(R.drawable.notification_toggle_r);
            tvSent.setTextColor(Color.WHITE);
            tvSent.setTypeface(semi);

            toggleReceived.setBackgroundResource(R.drawable.notification_toggle_s);
            tvReceived.setTextColor(Color.parseColor("#2DD7A4"));
            tvReceived.setTypeface(reg);

        } else {
            toggleReceived.setBackgroundResource(R.drawable.notification_toggle_r);
            tvReceived.setTextColor(Color.WHITE);
            tvReceived.setTypeface(semi);

            toggleSent.setBackgroundResource(R.drawable.notification_toggle_s);
            tvSent.setTextColor(Color.parseColor("#2DD7A4"));
            tvSent.setTypeface(reg);
        }
    }

    public void showProfilePopup() {
        profileDialog = new Dialog(NotificationActivity.this);
        profileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        profileDialog.setContentView(R.layout.profile_popup);

        if (profileDialog.getWindow() != null) {
            profileDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#80000000"))
            );
            profileDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }

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
        confirmDialog = new Dialog(NotificationActivity.this);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(R.layout.profile_popup2);

        if (confirmDialog.getWindow() != null) {
            confirmDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#80000000"))
            );
            confirmDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }

        View btnConfirm = confirmDialog.findViewById(R.id.btn_confirm_layout);
        btnConfirm.setOnClickListener(v -> confirmDialog.dismiss());

        confirmDialog.show();
    }

    // ★ 추가된 카카오톡 아이디 팝업 함수
    public void showKakaoPopup() {
        kakaoDialog = new Dialog(NotificationActivity.this);
        kakaoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        kakaoDialog.setContentView(R.layout.notification_matchingsuccess);

        if (kakaoDialog.getWindow() != null) {
            kakaoDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#80000000"))
            );
            kakaoDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }

        // 닫기/복사 버튼
        View btnCopy = kakaoDialog.findViewById(R.id.btn_copy);
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> kakaoDialog.dismiss());
        }

        kakaoDialog.show();
    }
}
