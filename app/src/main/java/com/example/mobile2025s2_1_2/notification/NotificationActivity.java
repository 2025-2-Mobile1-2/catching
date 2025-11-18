package com.example.mobile2025s2_1_2.notification;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

    // 리스트
    private RecyclerView recycler;
    private AlarmAdapter adapter;

    // 테스트 데이터
    private final List<AlarmItem> received = Arrays.asList(
            new AlarmItem("최북악 님으로부터 진로·전공 멘토 매칭 신청이 왔습니다!", true),   // 새 알림 (N)
            new AlarmItem("최북악 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!", false)   // 읽은 알림
    );

    private final List<AlarmItem> sent = Arrays.asList(
            new AlarmItem("김국민 님께 진로·전공 멘토 매칭 신청을 보냈습니다!", false),
            new AlarmItem("홍지우 님께 교내·교외 활동 팀원 매칭 신청을 보냈습니다!", true)  // 새 알림 (N)
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
        toggleReceived = findViewById(R.id.alarm_toggle_r); // 받은 매칭 루트
        toggleSent     = findViewById(R.id.alarm_toggle_s); // 보낸 매칭 루트
        tvReceived     = findViewById(R.id.tv_received);
        tvSent         = findViewById(R.id.tv_sent);

        // RecyclerView
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setClipChildren(false);
        recycler.setClipToPadding(false);

        // 초기: 받은 매칭 활성 + 받은 데이터 표시
        setToggleState(false); // UI만 전환
        showReceived();        // 데이터 바인딩

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

    /** 받은 탭 데이터 표시 */
    private void showReceived() {
        adapter = new AlarmAdapter(new ArrayList<>(received));
        recycler.setAdapter(adapter);
    }

    /** 보낸 탭 데이터 표시 */
    private void showSent() {
        adapter = new AlarmAdapter(new ArrayList<>(sent));
        recycler.setAdapter(adapter);
    }

    /** 토글의 활성/비활성 색상 및 폰트 전환(UI) */
    private void setToggleState(boolean isSentActive) {
        Typeface semi = ResourcesCompat.getFont(this, R.font.semibold);
        Typeface reg  = ResourcesCompat.getFont(this, R.font.regular);

        if (isSentActive) {
            // 보낸 활성
            toggleSent.setBackgroundResource(R.drawable.notification_toggle_r);
            tvSent.setTextColor(Color.WHITE);
            tvSent.setTypeface(semi);

            // 받은 비활성
            toggleReceived.setBackgroundResource(R.drawable.notification_toggle_s);
            tvReceived.setTextColor(Color.parseColor("#2DD7A4"));
            tvReceived.setTypeface(reg);

        } else {
            // 받은 활성
            toggleReceived.setBackgroundResource(R.drawable.notification_toggle_r);
            tvReceived.setTextColor(Color.WHITE);
            tvReceived.setTypeface(semi);

            // 보낸 비활성
            toggleSent.setBackgroundResource(R.drawable.notification_toggle_s);
            tvSent.setTextColor(Color.parseColor("#2DD7A4"));
            tvSent.setTypeface(reg);
        }
    }
}//
