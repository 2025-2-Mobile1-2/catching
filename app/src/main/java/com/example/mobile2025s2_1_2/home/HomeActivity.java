package com.example.mobile2025s2_1_2.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.home.notice.NoticeCardData;
import com.example.mobile2025s2_1_2.home.notice.NoticeFragment;
import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.home.schoolnotice.SchoolCardData;
import com.example.mobile2025s2_1_2.home.schoolnotice.SchoolCrawler;
import com.example.mobile2025s2_1_2.home.schoolnotice.SchoolFragment;
import com.example.mobile2025s2_1_2.home.schoolnotice.SchoolPreviewAdapter;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    public static View touchBlocker;

    // 🔥 알림 리스너용 필드
    private FirebaseFirestore db;
    private ListenerRegistration receivedListener;
    private ListenerRegistration sentListener;
    private String currentUserEmail;

    // 🔥 인앱 배너 뷰
    private View inAppBanner;
    private TextView tvInAppTitle;
    private LinearLayout bottomNavBar;

    // 🔥 새 알림 상태 캐시
    private boolean hasNewReceived = false;
    private boolean hasNewSent = false;
    private String bannerReceivedText = null;
    private String bannerSentText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        //공지사항
        ImageView noticeGo = findViewById(R.id.home_notice_go);
        noticeGo.setOnClickListener(v -> {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new NoticeFragment())
                .addToBackStack(null)
                .commit();
        });

        TextView previewTitle = findViewById(R.id.home_notice_preview);

        List<NoticeCardData.HomeNoticeData> notices =
                NoticeCardData.loadHomeNotices(this);

        if (notices != null && !notices.isEmpty()) {
            int lastIndex = notices.size() - 1;
            NoticeCardData.HomeNoticeData last = notices.get(lastIndex);

            previewTitle.setText(last.getTitle());
        }

        //학사공지
        ImageView schoolGo = findViewById(R.id.home_school_go);
        schoolGo.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SchoolFragment())
                    .addToBackStack(null)
                    .commit();
        });

        TextView preview1 = findViewById(R.id.school_preview_1);
        TextView preview2 = findViewById(R.id.school_preview_2);
        TextView preview3 = findViewById(R.id.school_preview_3);

        new Thread(() -> {
            List<SchoolCardData> all = SchoolCrawler.fetchNotices();

            // 첫 번째 제외
            List<SchoolCardData> preview = all.subList(2, Math.min(5, all.size()));

            runOnUiThread(() -> {
                if (preview.size() > 0) preview1.setText(preview.get(0).getTitle());
                if (preview.size() > 1) preview2.setText(preview.get(1).getTitle());
                if (preview.size() > 2) preview3.setText(preview.get(2).getTitle());
            });
        }).start();

        touchBlocker = findViewById(R.id.touch_blocker);

        // 하단 navBar
        bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_home);

        // 🔥 인앱 배너 뷰 찾기
        inAppBanner = findViewById(R.id.inapp_banner_root);
        tvInAppTitle = inAppBanner != null
                ? inAppBanner.findViewById(R.id.tv_inapp_title)
                : null;

        if (inAppBanner != null) {
            inAppBanner.setVisibility(View.GONE);

            // 배너 클릭 시 → 알림 탭으로 이동
            inAppBanner.setOnClickListener(v -> {
                // 하단 navBar에서 알림 탭 뷰 찾아서 클릭 시키기
                View navNotification = bottomNavBar.findViewById(R.id.nav_notification);
                if (navNotification != null) {
                    navNotification.performClick();
                }
                // 배너는 클릭 후 숨길지 말지는 취향
                inAppBanner.setVisibility(View.GONE);
                hasNewReceived = false;
                hasNewSent = false;
            });
        }

        // 🔥 Firestore & 현재 로그인 유저 이메일
        db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        currentUserEmail = prefs.getString("user_email", null);

        // 🔥 알림 리스너 시작
        startNotificationListeners();
    }

    // 🔥 받은 매칭 + 보낸 매칭 둘 다 실시간 감지
    private void startNotificationListeners() {
        if (currentUserEmail == null) return;

        // 1) 받은 매칭 (toID == 나)
        receivedListener = db.collection("matching_status")
                .whereEqualTo("toID", currentUserEmail)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, error) -> {
                    if (error != null || snap == null) return;

                    boolean hasNew = false;
                    String latestText = null;

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Boolean isNewForB = doc.getBoolean("isNewForB");
                        if (Boolean.TRUE.equals(isNewForB)) {
                            hasNew = true;

                            String fromId   = doc.getString("fromID");
                            String state    = doc.getString("state");
                            String category = doc.getString("category");

                            // TODO: 나중에 fromId → fromName으로 변경
                            String fromName = fromId != null ? fromId : "상대";

                            if ("accepted".equals(state)) {
                                latestText = fromName + " 님이 매칭을 수락했습니다.";
                            } else if ("rejected".equals(state)) {
                                latestText = fromName + " 님이 매칭을 거절했습니다.";
                            } else {
                                // 요청 상태
                                if ("roommate".equals(category)) {
                                    latestText = fromName + " 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!";
                                } else {
                                    latestText = fromName + " 님으로부터 새로운 매칭 신청이 왔습니다!";
                                }
                            }
                            break; // 가장 최근 새 알림 하나만 사용
                        }
                    }

                    hasNewReceived = hasNew;
                    bannerReceivedText = latestText;

                    updateInAppBanner();
                });

        // 2) 보낸 매칭 (fromID == 나)
        sentListener = db.collection("matching_status")
                .whereEqualTo("fromID", currentUserEmail)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, error) -> {
                    if (error != null || snap == null) return;

                    boolean hasNew = false;
                    String latestText = null;

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        Boolean isNewForA = doc.getBoolean("isNewForA");
                        if (Boolean.TRUE.equals(isNewForA)) {
                            hasNew = true;

                            String toId     = doc.getString("toID");
                            String state    = doc.getString("state");
                            String category = doc.getString("category");

                            // TODO: 나중에 toId → toName으로 변경
                            String toName = toId != null ? toId : "상대";

                            if ("accepted".equals(state)) {
                                latestText = toName + " 님이 매칭을 수락했습니다. 카카오톡 아이디를 확인해 보세요.";
                            } else if ("rejected".equals(state)) {
                                latestText = toName + " 님이 매칭을 거절했습니다.";
                            } else {
                                if ("roommate".equals(category)) {
                                    latestText = toName + " 님께 기숙사 룸메이트 매칭 신청을 보냈습니다!";
                                } else {
                                    latestText = toName + " 님께 새로운 매칭 신청을 보냈습니다!";
                                }
                            }
                            break;
                        }
                    }

                    hasNewSent = hasNew;
                    bannerSentText = latestText;

                    updateInAppBanner();
                });
    }

    // 🔥 상단 배너 표시/숨김 결정
    private void updateInAppBanner() {
        if (inAppBanner == null || tvInAppTitle == null) return;

        String textToShow = null;

        // 기본적으로 "받은 알림"을 우선 표시
        if (hasNewReceived && bannerReceivedText != null) {
            textToShow = bannerReceivedText;
        } else if (hasNewSent && bannerSentText != null) {
            textToShow = bannerSentText;
        }

        if (textToShow != null) {
            inAppBanner.setVisibility(View.VISIBLE);
            tvInAppTitle.setText(textToShow);
        } else {
            inAppBanner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 🔥 리스너 해제
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
