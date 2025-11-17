//package com.example.mobile2025s2_1_2.notification;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import com.example.mobile2025s2_1_2.*;
//import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
//
//public class NotificationActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.notification_main);
//
//        //하단 navBar
//        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
//        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
//        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_notification);
//    }
//}
package com.example.mobile2025s2_1_2.notification;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

public class NotificationActivity extends AppCompatActivity {

    private RelativeLayout toggleReceived; // 받은 매칭 루트
    private RelativeLayout toggleSent;     // 보낸 매칭 루트
    private TextView tvReceived, tvSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_main);

        // 하단 navBar
        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_notification);

        // 토글 참조 (notification_toggle_group.xml 내부 id 기준)
        toggleReceived = findViewById(R.id.alarm_toggle_r);
        toggleSent = findViewById(R.id.alarm_toggle_s);
        tvReceived = findViewById(R.id.tv_received);
        tvSent = findViewById(R.id.tv_sent);

        // 초기 상태: 받은 매칭 활성
        setToggleState(false);

        // 클릭 이벤트
        toggleReceived.setOnClickListener(v -> setToggleState(false));
        toggleSent.setOnClickListener(v -> setToggleState(true));
    }

    /** 토글의 활성/비활성 색상 및 폰트 전환 */
    private void setToggleState(boolean isSentActive) {
        Typeface semi = ResourcesCompat.getFont(this, R.font.semibold);
        Typeface reg = ResourcesCompat.getFont(this, R.font.regular);

        if (isSentActive) {
            // 보낸 매칭 활성
            toggleSent.setBackgroundResource(R.drawable.notification_toggle_r);
            tvSent.setTextColor(Color.WHITE);
            tvSent.setTypeface(semi);

            // 받은 매칭 비활성
            toggleReceived.setBackgroundResource(R.drawable.notification_toggle_s);
            tvReceived.setTextColor(Color.parseColor("#2DD7A4"));
            tvReceived.setTypeface(reg);

        } else {
            // 받은 매칭 활성
            toggleReceived.setBackgroundResource(R.drawable.notification_toggle_r);
            tvReceived.setTextColor(Color.WHITE);
            tvReceived.setTypeface(semi);

            // 보낸 매칭 비활성
            toggleSent.setBackgroundResource(R.drawable.notification_toggle_s);
            tvSent.setTextColor(Color.parseColor("#2DD7A4"));
            tvSent.setTypeface(reg);
        }
    }
}
