package com.example.mobile2025s2_1_2.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.home.notice.NoticeCardData;
import com.example.mobile2025s2_1_2.home.notice.NoticeFragment;
import com.example.mobile2025s2_1_2.home.schoolnotice.SchoolCardData;
import com.example.mobile2025s2_1_2.home.schoolnotice.SchoolCrawler;
import com.example.mobile2025s2_1_2.home.schoolnotice.SchoolFragment;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static View touchBlocker;

    // 🔔 Firestore & 리스너
    private FirebaseFirestore db;
    private ListenerRegistration receivedListener;
    private ListenerRegistration sentListener;
    private String currentUserEmail;

    // 🔔 상단 오버레이 배너
    private View overlayBanner;
    private TextView tvOverlayTitle;
    private LinearLayout bottomNavBar;

    // 첫 스냅샷 무시용 플래그
    private boolean firstReceivedSnapshot = true;
    private boolean firstSentSnapshot = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        // ───────── 공지사항 미리보기 ─────────
        ImageView noticeGo = findViewById(R.id.home_notice_go);
        noticeGo.setOnClickListener(v -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new NoticeFragment())
                .addToBackStack(null)
                .commit());

        TextView previewTitle = findViewById(R.id.home_notice_preview);
        List<NoticeCardData.HomeNoticeData> notices =
                NoticeCardData.loadHomeNotices(this);
        if (notices != null && !notices.isEmpty()) {
            NoticeCardData.HomeNoticeData last = notices.get(notices.size() - 1);
            previewTitle.setText(last.getTitle());
        }

        // ───────── 학사공지 미리보기 ─────────
        ImageView schoolGo = findViewById(R.id.home_school_go);
        schoolGo.setOnClickListener(v -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SchoolFragment())
                .addToBackStack(null)
                .commit());

        TextView preview1 = findViewById(R.id.school_preview_1);
        TextView preview2 = findViewById(R.id.school_preview_2);
        TextView preview3 = findViewById(R.id.school_preview_3);

        new Thread(() -> {
            List<SchoolCardData> all = SchoolCrawler.fetchNotices();
            List<SchoolCardData> preview = all.subList(2, Math.min(5, all.size()));

            runOnUiThread(() -> {
                if (preview.size() > 0) preview1.setText(preview.get(0).getTitle());
                if (preview.size() > 1) preview2.setText(preview.get(1).getTitle());
                if (preview.size() > 2) preview3.setText(preview.get(2).getTitle());
            });
        }).start();

        touchBlocker = findViewById(R.id.touch_blocker);

        // ───────── 하단 네비게이션 바 ─────────
        bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_home);

        // 🔥 기존 레이아웃 안에 있던 inapp_banner_root 카드는 사용 안 함 → 항상 GONE
        View bannerPlaceholder = findViewById(R.id.inapp_banner_root);
        if (bannerPlaceholder != null) {
            bannerPlaceholder.setVisibility(View.GONE);
        }

        // ───────── Firestore & 현재 로그인 유저 ─────────
        db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("user_email", null);

        // 🔔 실시간 알림 리스너 시작
        startNotificationListeners();
    }

    // ───────────────── 알림 리스너 ─────────────────
    private void startNotificationListeners() {
        if (currentUserEmail == null) return;

        // 1) 받은 매칭 (toID == 나)
        receivedListener = db.collection("matching_status")
                .whereEqualTo("toID", currentUserEmail)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, error) -> {
                    if (error != null || snap == null) return;

                    // 앱 진입 직후 첫 스냅샷은 초기 데이터 → 무시
                    if (firstReceivedSnapshot) {
                        firstReceivedSnapshot = false;
                        return;
                    }

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Boolean isNewForB = doc.getBoolean("isNewForB");
                        if (!Boolean.TRUE.equals(isNewForB)) continue;

                        String fromId   = doc.getString("fromID");
                        String state    = doc.getString("state");
                        String category = doc.getString("category");
                        String fromName = fromId != null ? fromId : "상대";

                        String message;
                        if ("accepted".equals(state)) {
                            message = fromName + " 님이 매칭을 수락했습니다.";
                        } else if ("rejected".equals(state)) {
                            message = fromName + " 님이 매칭을 거절했습니다.";
                        } else {
                            if ("roommate".equals(category)) {
                                message = fromName + " 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!";
                            } else {
                                message = fromName + " 님으로부터 새로운 매칭 신청이 왔습니다!";
                            }
                        }

                        showTopInAppBanner(message);
                        break; // 새 알림 하나만 배너로
                    }
                });

        // 2) 보낸 매칭 (fromID == 나)
        sentListener = db.collection("matching_status")
                .whereEqualTo("fromID", currentUserEmail)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, error) -> {
                    if (error != null || snap == null) return;

                    if (firstSentSnapshot) {
                        firstSentSnapshot = false;
                        return;
                    }

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Boolean isNewForA = doc.getBoolean("isNewForA");
                        if (!Boolean.TRUE.equals(isNewForA)) continue;

                        String toId     = doc.getString("toID");
                        String state    = doc.getString("state");
                        String category = doc.getString("category");
                        String toName   = toId != null ? toId : "상대";

                        String message;
                        if ("accepted".equals(state)) {
                            message = toName + " 님이 매칭을 수락했습니다. 카카오톡 아이디를 확인해 보세요.";
                        } else if ("rejected".equals(state)) {
                            message = toName + " 님이 매칭을 거절했습니다.";
                        } else {
                            if ("roommate".equals(category)) {
                                message = toName + " 님께 기숙사 룸메이트 매칭 신청을 보냈습니다!";
                            } else {
                                message = toName + " 님께 새로운 매칭 신청을 보냈습니다!";
                            }
                        }

                        showTopInAppBanner(message);
                        break;
                    }
                });
    }

    // ───────── 상단 오버레이 인앱 배너 (notification_b.xml 사용) ─────────
    private void showTopInAppBanner(String message) {
        if (message == null) return;

        if (overlayBanner == null) {
            FrameLayout root = findViewById(android.R.id.content);

            // 🔥 레이아웃 이름: notification_b.xml
            overlayBanner = getLayoutInflater()
                    .inflate(R.layout.notification_b, root, false);

            // layout 안의 TextView id: tv_alarm_title
            tvOverlayTitle = overlayBanner.findViewById(R.id.tv_alarm_title);

            // 최상단에 붙이기 위한 LayoutParams
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.topMargin = getStatusBarHeight() + dpToPx(8);
            overlayBanner.setLayoutParams(lp);

            // 처음에는 위로 숨겨두기
            overlayBanner.setTranslationY(-dpToPx(80));

            // 배너 클릭 시 → 알림 탭으로 이동 후 배너 숨김
            overlayBanner.setOnClickListener(v -> {
                if (bottomNavBar != null) {
                    View navNotification = bottomNavBar.findViewById(R.id.nav_notification);
                    if (navNotification != null) {
                        navNotification.performClick();
                    }
                }
                hideTopInAppBanner();
            });

            root.addView(overlayBanner);
        }

        tvOverlayTitle.setText(message);
        overlayBanner.setVisibility(View.VISIBLE);
        overlayBanner.animate()
                .translationY(0)
                .setDuration(200)
                .start();
    }

    private void hideTopInAppBanner() {
        if (overlayBanner == null) return;
        overlayBanner.animate()
                .translationY(-dpToPx(80))
                .setDuration(200)
                .withEndAction(() -> overlayBanner.setVisibility(View.GONE))
                .start();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resId = getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) result = getResources().getDimensionPixelSize(resId);
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receivedListener != null) {
            receivedListener.remove();
            receivedListener = null;
        }
        if (sentListener != null) {
            sentListener.remove();
            sentListener = null;
        }
    }
}
