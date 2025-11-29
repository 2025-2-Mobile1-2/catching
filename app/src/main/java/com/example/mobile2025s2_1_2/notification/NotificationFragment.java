package com.example.mobile2025s2_1_2.notification;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    // 토글
    private RelativeLayout toggleReceived, toggleSent;
    private TextView tvReceived, tvSent;

    // 팝업
    private Dialog profileDialog;
    private Dialog confirmDialog;

    // 리스트
    private RecyclerView recycler;
    private AlarmAdapter adapter;

    // 현재 클릭된 알람
    private AlarmItem currentItem;

    // Firestore
    private FirebaseFirestore db;
    private String currentUserEmail;

    // 메모리 캐시용 리스트
    private final List<AlarmItem> receivedList = new ArrayList<>();
    private final List<AlarmItem> sentList     = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.notification_main, container, false);

        // 하단 navBar
        LinearLayout bottomNavBar = view.findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(requireActivity(), bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_notification);

        // Firestore & 현재 유저 이메일
        db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE);
        currentUserEmail = prefs.getString("user_email", null);

        Log.d("NOTI_USER", "currentUserEmail = " + currentUserEmail);

        // 토글
        toggleReceived = view.findViewById(R.id.alarm_toggle_r);
        toggleSent     = view.findViewById(R.id.alarm_toggle_s);
        tvReceived     = view.findViewById(R.id.tv_received);
        tvSent         = view.findViewById(R.id.tv_sent);

        // RecyclerView
        recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setClipChildren(false);
        recycler.setClipToPadding(false);

        // 초기: 받은 탭 활성화 + Firestore에서 받은 매칭 불러오기
        setToggleState(false);
        loadReceivedFromFirestore();

        toggleReceived.setOnClickListener(v -> {
            setToggleState(false);
            loadReceivedFromFirestore();
        });

        toggleSent.setOnClickListener(v -> {
            setToggleState(true);
            loadSentFromFirestore();
        });

        return view;
    }

    // ================== Firestore 로딩 ==================

    /** 카테고리 코드 → 한글 문구 매핑 */
    private String getCategoryLabel(String category) {
        if (category == null) return "매칭";

        switch (category) {
            case "roommate":
            case "roomate":   // 혹시 오타 방어
                return "기숙사 룸메이트 매칭";
            case "activity":
                return "교내·교외 활동 팀원 매칭";
            case "mentorship":
                return "진로·전공 멘토 매칭";
            default:
                return "매칭";
        }
    }

    /** Firestore에서 받은 매칭 불러오기 (toID == 나) + Users 컬렉션에서 이름 가져오기 */
    private void loadReceivedFromFirestore() {
        if (db == null || currentUserEmail == null) return;

        db.collection("matching_status")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    receivedList.clear();

                    // 1차로 "내가 받은 것"만 필터링
                    List<DocumentSnapshot> myDocs = new ArrayList<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String toId = doc.getString("toID");
                        if (toId != null && toId.equals(currentUserEmail)) {
                            myDocs.add(doc);
                        }
                    }

                    if (myDocs.isEmpty()) {
                        adapter = new AlarmAdapter(
                                new ArrayList<>(receivedList),
                                true,
                                (item, isReceivedList) -> handleAlarmClick(item, isReceivedList)
                        );
                        recycler.setAdapter(adapter);
                        return;
                    }

                    // 비동기 이름 로딩 개수 카운트
                    final int total = myDocs.size();
                    final int[] doneCount = {0};

                    for (DocumentSnapshot doc : myDocs) {
                        String docId    = doc.getId();
                        String fromId   = doc.getString("fromID");
                        String toId     = doc.getString("toID");
                        String state    = doc.getString("state");
                        String category = doc.getString("category");
                        Boolean isNewForB = doc.getBoolean("isNewForB");

                        Log.d("NOTI_REC",
                                "doc=" + docId +
                                        ", fromID=" + fromId +
                                        ", toID=" + toId +
                                        ", state=" + state);

                        // fromId 기준으로 Users 컬렉션에서 이름 가져오기
                        fetchUserName(fromId, name -> {
                            String fromName = name != null ? name : (fromId != null ? fromId : "상대");
                            String categoryLabel = getCategoryLabel(category);

                            String text;
                            if ("accepted".equals(state)) {
                                // 내가 상대의 [카테고리]를 수락
                                text = fromName + " 님의 " + categoryLabel + "을 수락했습니다.";
                            } else if ("rejected".equals(state)) {
                                // 내가 상대의 [카테고리]를 거절
                                text = fromName + " 님의 " + categoryLabel + "을 거절했습니다.";
                            } else { // "request" 또는 null
                                // 상대가 나에게 [카테고리] 신청
                                text = fromName + " 님으로부터 " + categoryLabel + " 신청이 왔습니다!";
                            }

                            AlarmItem item = new AlarmItem(
                                    docId,
                                    text,
                                    Boolean.TRUE.equals(isNewForB),
                                    fromId,
                                    state != null ? state : "request",
                                    category != null ? category : "roommate",
                                    true,       // 받은 탭
                                    fromId      // 상대 이메일 (카카오 아이디 조회용)
                            );

                            receivedList.add(item);

                            doneCount[0]++;
                            if (doneCount[0] >= total) {
                                // 모두 끝났을 때 어댑터 갱신
                                adapter = new AlarmAdapter(
                                        new ArrayList<>(receivedList),
                                        true,
                                        (clickedItem, isReceivedList) -> handleAlarmClick(clickedItem, isReceivedList)
                                );
                                recycler.setAdapter(adapter);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NOTI_REC", "loadReceivedFromFirestore error", e);
                });
    }

    /** Firestore에서 보낸 매칭 불러오기 (fromID == 나) + Users 컬렉션에서 이름 가져오기 */
    private void loadSentFromFirestore() {
        if (db == null || currentUserEmail == null) return;

        db.collection("matching_status")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    sentList.clear();

                    // 1차로 "내가 보낸 것"만 필터링
                    List<DocumentSnapshot> myDocs = new ArrayList<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String fromId = doc.getString("fromID");
                        if (fromId != null && fromId.equals(currentUserEmail)) {
                            myDocs.add(doc);
                        }
                    }

                    if (myDocs.isEmpty()) {
                        adapter = new AlarmAdapter(
                                new ArrayList<>(sentList),
                                false,
                                (item, isReceivedList) -> handleAlarmClick(item, isReceivedList)
                        );
                        recycler.setAdapter(adapter);
                        return;
                    }

                    final int total = myDocs.size();
                    final int[] doneCount = {0};

                    for (DocumentSnapshot doc : myDocs) {
                        String docId    = doc.getId();
                        String fromId   = doc.getString("fromID");
                        String toId     = doc.getString("toID");
                        String state    = doc.getString("state");
                        String category = doc.getString("category");
                        Boolean isNewForA = doc.getBoolean("isNewForA");

                        Log.d("NOTI_SENT",
                                "doc=" + docId +
                                        ", fromID=" + fromId +
                                        ", toID=" + toId +
                                        ", state=" + state);

                        // toId 기준으로 Users 컬렉션에서 이름 가져오기
                        fetchUserName(toId, name -> {
                            String toName = name != null ? name : (toId != null ? toId : "상대");
                            String categoryLabel = getCategoryLabel(category);

                            String text;
                            if ("accepted".equals(state)) {
                                // 상대가 내 [카테고리]를 수락
                                text = toName + " 님이 " + categoryLabel + "을 수락했습니다. 카카오톡 아이디를 확인해 보세요.";
                            } else if ("rejected".equals(state)) {
                                // 상대가 내 [카테고리]를 거절
                                text = toName + " 님이 " + categoryLabel + "을 거절했습니다.";
                            } else { // "request" 또는 null
                                // 내가 상대에게 [카테고리] 신청
                                text = toName + " 님께 " + categoryLabel + " 신청을 보냈습니다!";
                            }

                            AlarmItem item = new AlarmItem(
                                    docId,
                                    text,
                                    Boolean.TRUE.equals(isNewForA),
                                    fromId,
                                    state != null ? state : "request",
                                    category != null ? category : "roommate",
                                    false,      // 보낸 탭
                                    toId        // 상대 이메일 (카카오 아이디 조회용)
                            );

                            sentList.add(item);

                            doneCount[0]++;
                            if (doneCount[0] >= total) {
                                adapter = new AlarmAdapter(
                                        new ArrayList<>(sentList),
                                        false,
                                        (clickedItem, isReceivedList) -> handleAlarmClick(clickedItem, isReceivedList)
                                );
                                recycler.setAdapter(adapter);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("NOTI_SENT", "loadSentFromFirestore error", e);
                });
    }

    /** Users 컬렉션에서 이메일 기준으로 name 가져오기 */
    private void fetchUserName(@Nullable String email, @NonNull NameCallback callback) {
        if (email == null || email.isEmpty() || db == null) {
            callback.onNameLoaded(null);
            return;
        }

        db.collection("Users")
                .document(email)
                .get()
                .addOnSuccessListener(doc -> {
                    String name = doc.getString("name");
                    callback.onNameLoaded(name);
                })
                .addOnFailureListener(e -> {
                    Log.e("NOTI", "fetchUserName error", e);
                    callback.onNameLoaded(null);
                });
    }

    private interface NameCallback {
        void onNameLoaded(@Nullable String name);
    }

    // ================== 알람 클릭 처리 ==================

    /** 알람 아이템 클릭 처리 공통 로직 */
    private void handleAlarmClick(AlarmItem item, boolean isReceivedList) {
        currentItem = item;

        // 1) 읽음 처리 + 바로 UI 반영
        markAlarmRead(item, isReceivedList);
        item.isNew = false;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        // 2) 보낸 매칭 탭 클릭
        if (!isReceivedList) {
            // 보낸 쪽은 state 로만 처리
            if ("accepted".equals(item.state)) {
                // 내가 보낸 게 수락됨 → 카카오 아이디 팝업
                showKakaoPopup();
            } else if ("rejected".equals(item.state)) {
                // 내가 보낸 게 거절됨 → 거절 확인 팝업
                showRejectConfirmPopup();
            } else {
                // "request" 상태면 아직 대기 중
            }
            return;
        }

        // 3) 받은 매칭 탭 클릭

        // 상태가 이미 확정된(accepted / rejected) 경우:
        // 프래그먼트를 다시 들어와도 무조건 두 번째 팝업만 뜨도록.
        if ("accepted".equals(item.state)) {
            showConfirmPopup();
            return;
        } else if ("rejected".equals(item.state)) {
            showRejectConfirmPopup();
            return;
        }

        // 여기까지 왔으면 state == "request" (아직 수락/거절 전)

        // 이미 한 번 눌러서 프로필 본 적 있으면 마지막 팝업 기준으로 다시 열어주기
        if (item.clickedBefore) {
            if (item.lastPopupType == 2) {
                showConfirmPopup();
            } else if (item.lastPopupType == 4) {
                showRejectConfirmPopup();
            } else {
                showProfilePopup();
            }
            return;
        }

        // 처음 클릭 → 프로필 팝업
        item.clickedBefore = true;
        item.lastPopupType = 1;
        showProfilePopup();
    }

    /** N 뱃지 읽음 처리 (isNewForA / isNewForB false로) */
    private void markAlarmRead(AlarmItem item, boolean isReceivedList) {
        if (db == null || item.docId == null) return;

        String field = isReceivedList ? "isNewForB" : "isNewForA";

        db.collection("matching_status")
                .document(item.docId)
                .update(field, false)
                .addOnFailureListener(e ->
                        Log.e("NOTI", "markAlarmRead update error", e)
                );
    }

    /** 토글의 활성/비활성 색상 및 폰트 전환(UI) */
    private void setToggleState(boolean isSentActive) {
        Typeface semi = ResourcesCompat.getFont(requireContext(), R.font.semibold);
        Typeface reg  = ResourcesCompat.getFont(requireContext(), R.font.regular);

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

    // ================== 팝업 로직들 ==================

    public void showProfilePopup() {
        profileDialog = new Dialog(requireContext());
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
        btnClose.setOnClickListener(v -> {
            profileDialog.dismiss();

            if (currentItem != null) {
                currentItem.clickedBefore = false;
                currentItem.lastPopupType = 0;
            }
        });

        ImageView btnAccept = profileDialog.findViewById(R.id.btn_accept);
        ImageView btnReject = profileDialog.findViewById(R.id.btn_reject);

        // 🔹 여기서부터 Users 컬렉션에서 상대 정보 가져와서 채우기
        String otherEmail = (currentItem != null) ? currentItem.otherEmail : null;

        // 팝업 안 뷰들 미리 findViewById
        TextView tvName       = profileDialog.findViewById(R.id.roommate_name);
        TextView tvGender     = profileDialog.findViewById(R.id.roommate_gender);
        TextView tvDormitory  = profileDialog.findViewById(R.id.roommate_dormitory);
        TextView tvAge        = profileDialog.findViewById(R.id.roommate_age);
        TextView tvMbti       = profileDialog.findViewById(R.id.roommate_mbti);
        TextView tvDrink      = profileDialog.findViewById(R.id.roommate_drink);
        TextView tvSmoke      = profileDialog.findViewById(R.id.roommate_smoke);

        TextView tvCleanValue     = profileDialog.findViewById(R.id.roommate_clean_value);
        TextView tvSleepValue     = profileDialog.findViewById(R.id.roommate_sleep_value);
        TextView tvSensitiveValue = profileDialog.findViewById(R.id.roommate_sensitive_value);

        SeekBar sbClean     = profileDialog.findViewById(R.id.roommate_clean_seekbar);
        SeekBar sbSleep     = profileDialog.findViewById(R.id.roommate_sleep_seekbar);
        SeekBar sbSensitive = profileDialog.findViewById(R.id.roommate_sensitive_seekbar);

        // 슬라이더는 읽기 전용으로
        if (sbClean != null) {
            sbClean.setEnabled(false);
            sbClean.setClickable(false);
        }
        if (sbSleep != null) {
            sbSleep.setEnabled(false);
            sbSleep.setClickable(false);
        }
        if (sbSensitive != null) {
            sbSensitive.setEnabled(false);
            sbSensitive.setClickable(false);
        }

        if (db != null && otherEmail != null && !otherEmail.isEmpty()) {
            db.collection("Users")
                    .document(otherEmail)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (!doc.exists()) return;

                        String name      = doc.getString("name");
                        String gender    = doc.getString("gender");
                        String dorm      = doc.getString("dorm");
                        String age       = doc.getString("age");
                        String mbti      = doc.getString("mbti");
                        String alcohol   = doc.getString("alcohol");   // "O"/"X" or 값
                        String smoking   = doc.getString("smoking");   // "O"/"X"

                        Object cleanObj     = doc.get("clean");
                        Object sleepObj     = doc.get("sleep");
                        Object sensitiveObj = doc.get("sensitive");

                        int cleanVal     = toInt(cleanObj, 5);
                        int sleepVal     = toInt(sleepObj, 5);
                        int sensitiveVal = toInt(sensitiveObj, 5);

                        // 텍스트 채우기
                        if (name != null && tvName != null) {
                            tvName.setText(name);
                        }
                        if (gender != null && tvGender != null) {
                            tvGender.setText(gender);
                        }
                        if (dorm != null && tvDormitory != null) {
                            tvDormitory.setText(dorm);
                        }
                        if (age != null && tvAge != null) {
                            tvAge.setText(age);
                        }
                        if (mbti != null && tvMbti != null) {
                            tvMbti.setText(mbti);
                        }
                        if (tvDrink != null) {
                            String drinkText = "음주 " +
                                    (alcohol == null ? "정보없음" : alcohol);
                            tvDrink.setText(drinkText);
                        }
                        if (tvSmoke != null) {
                            String smokeText = "흡연 " +
                                    (smoking == null ? "정보없음" : smoking);
                            tvSmoke.setText(smokeText);
                        }

                        // 숫자 & SeekBar 값 설정 (0~10 사이로 클램프)
                        cleanVal     = clamp(cleanVal, 0, 10);
                        sleepVal     = clamp(sleepVal, 0, 10);
                        sensitiveVal = clamp(sensitiveVal, 0, 10);

                        if (sbClean != null) sbClean.setProgress(cleanVal);
                        if (sbSleep != null) sbSleep.setProgress(sleepVal);
                        if (sbSensitive != null) sbSensitive.setProgress(sensitiveVal);

                        if (tvCleanValue != null) tvCleanValue.setText(String.valueOf(cleanVal));
                        if (tvSleepValue != null) tvSleepValue.setText(String.valueOf(sleepVal));
                        if (tvSensitiveValue != null) tvSensitiveValue.setText(String.valueOf(sensitiveVal));
                    })
                    .addOnFailureListener(e -> {
                        Log.e("NOTI", "상대 프로필 조회 실패", e);
                    });
        }

        // ✅ 수락 버튼
        btnAccept.setOnClickListener(v -> {
            profileDialog.dismiss();

            if (currentItem != null && currentItem.docId != null) {
                db.collection("matching_status")
                        .document(currentItem.docId)
                        .update(
                                "state", "accepted",
                                "isNewForA", true,
                                "isNewForB", false
                        );

                currentItem.state = "accepted";
                currentItem.lastPopupType = 2;
            }

            showConfirmPopup();
        });

        // ✅ 거절 버튼
        btnReject.setOnClickListener(v -> {
            profileDialog.dismiss();

            if (currentItem != null && currentItem.docId != null) {
                db.collection("matching_status")
                        .document(currentItem.docId)
                        .update(
                                "state", "rejected",
                                "isNewForA", true,
                                "isNewForB", false
                        );

                currentItem.state = "rejected";
            }

            showRejectPopup();
        });

        profileDialog.show();
    }

    /** Object(숫자/문자열/Double 등) → int 로 안전하게 변환 */
    private int toInt(Object value, int defaultVal) {
        if (value == null) return defaultVal;
        try {
            return (int) Math.round(Double.parseDouble(String.valueOf(value)));
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /** min ≤ x ≤ max 범위로 클램프 */
    private int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private void showConfirmPopup() {
        if (currentItem != null) currentItem.lastPopupType = 2;

        confirmDialog = new Dialog(requireContext());
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

        // 기본 뷰들
        TextView tvName       = confirmDialog.findViewById(R.id.tv_name_confirm);
        TextView tvMessage    = confirmDialog.findViewById(R.id.tv_message);
        TextView tvSubMessage = confirmDialog.findViewById(R.id.tv_sub_message);

        // 기본 문구
        String baseName = "상대방";

        if (tvName != null) {
            tvName.setText(baseName);
        }
        if (tvMessage != null) {
            tvMessage.setText("매칭을 수락했어요");
        }
        if (tvSubMessage != null) {
            tvSubMessage.setText("곧 " + baseName + " 님께서 연락을 주실 거예요!");
        }

        // fromID(이메일)로 Users 컬렉션에서 name 가져와서 업데이트
        if (db != null && currentItem != null && currentItem.fromID != null) {
            db.collection("Users")
                    .document(currentItem.fromID)
                    .get()
                    .addOnSuccessListener(snap -> {
                        if (confirmDialog == null || !confirmDialog.isShowing()) return;

                        String fetchedName = snap.getString("name");
                        if (fetchedName == null || fetchedName.trim().isEmpty()) return;

                        String displayName = fetchedName;

                        if (tvName != null) {
                            tvName.setText(displayName);
                        }
                        if (tvMessage != null) {
                            tvMessage.setText("매칭을 수락했어요");
                        }
                        if (tvSubMessage != null) {
                            tvSubMessage.setText("곧 " + displayName + " 님께서 연락을 주실 거예요!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("NOTI", "showConfirmPopup: 이름 로드 실패", e);
                    });
        }

        View btnConfirmLayout = confirmDialog.findViewById(R.id.btn_confirm_layout);
        if (btnConfirmLayout != null) {
            btnConfirmLayout.setOnClickListener(v -> confirmDialog.dismiss());
        }

        confirmDialog.show();
    }

    private void showRejectPopup() {
        Dialog rejectDialog = new Dialog(requireContext());
        rejectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rejectDialog.setContentView(R.layout.profile_popup3);

        if (rejectDialog.getWindow() != null) {
            rejectDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#80000000"))
            );
            rejectDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }

        // 🔹 이름 / 서브메시지 TextView
        TextView tvNameReject  = rejectDialog.findViewById(R.id.tv_name_reject);   // "최북악" 자리
        TextView tvSubMessage  = rejectDialog.findViewById(R.id.tv_sub_message);   // "김국민 님께..." 자리

        // 기본 플레이스홀더
        if (tvNameReject != null) {
            tvNameReject.setText("상대방");
        }
        if (tvSubMessage != null) {
            tvSubMessage.setText("사용자님께 더 좋은 매칭이 이뤄질 수 있게\n캐칭이 더 노력할게요!");
        }

        // ✅ fromID → 요청 보낸 사람 이름, toID(=currentUserEmail) → 나 이름
        if (db != null && currentItem != null) {
            String fromEmail = currentItem.fromID;      // 매칭을 보낸 사람 (상대)
            String toEmail   = currentUserEmail;        // 매칭을 받은 사람 (나, toID)

            // fromID 이름: tv_name_reject 에 세팅
            if (fromEmail != null && !fromEmail.isEmpty()) {
                db.collection("Users")
                        .document(fromEmail)
                        .get()
                        .addOnSuccessListener(snap -> {
                            if (!snap.exists() || !rejectDialog.isShowing()) return;
                            String fromName = snap.getString("name");
                            if (fromName != null && !fromName.trim().isEmpty() && tvNameReject != null) {
                                tvNameReject.setText(fromName);
                            }
                        })
                        .addOnFailureListener(e ->
                                Log.e("NOTI", "showRejectPopup: fromName load fail", e)
                        );
            }

            // toID 이름: tv_sub_message 안의 "김국민" 자리에 세팅
            if (toEmail != null && !toEmail.isEmpty()) {
                db.collection("Users")
                        .document(toEmail)
                        .get()
                        .addOnSuccessListener(snap -> {
                            if (!snap.exists() || !rejectDialog.isShowing()) return;
                            String toName = snap.getString("name");
                            if (toName != null && !toName.trim().isEmpty() && tvSubMessage != null) {
                                tvSubMessage.setText(
                                        toName + "님께 더 좋은 매칭이 이뤄질 수 있게\n캐칭이 더 노력할게요!"
                                );
                            }
                        })
                        .addOnFailureListener(e ->
                                Log.e("NOTI", "showRejectPopup: toName load fail", e)
                        );
            }
        }

        // ❌ 거절 확정 버튼 → profile_popup4
        View btnReject = rejectDialog.findViewById(R.id.btn_reject_layout);
        btnReject.setOnClickListener(v -> {
            rejectDialog.dismiss();
            showRejectConfirmPopup();
        });

        // ❌ 취소 버튼 → 팝업만 닫고 상태 초기화
        View btnClose = rejectDialog.findViewById(R.id.btn_delete_layout);
        btnClose.setOnClickListener(v -> {
            rejectDialog.dismiss();

            if (currentItem != null) {
                currentItem.clickedBefore = false;
                currentItem.lastPopupType = 0;
            }
        });

        rejectDialog.show();
    }


    private void showRejectConfirmPopup() {
        if (currentItem != null) currentItem.lastPopupType = 4;

        Dialog deleteDialog = new Dialog(requireContext());
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.profile_popup4);

        if (deleteDialog.getWindow() != null) {
            deleteDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.parseColor("#80000000"))
            );
            deleteDialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
        }

        TextView tvNameDelete  = deleteDialog.findViewById(R.id.tv_name_delete);   // "최북악"
        TextView tvSubMessage  = deleteDialog.findViewById(R.id.tv_sub_message);   // "김국민 님께..."

        // 🔹 기본 값 설정
        String baseName = "상대방";

        if (tvNameDelete != null) {
            tvNameDelete.setText(baseName);
        }

        if (tvSubMessage != null) {
            tvSubMessage.setText("님께 더 좋은 매칭이 이뤄질 수 있게\n캐칭이 더 노력할게요!");
        }

        // ✅ fromID / toID 기준으로 이름 주입
        if (db != null && currentItem != null) {
            String fromEmail = currentItem.fromID;   // 요청 보낸 사람
            String toEmail   = currentUserEmail;     // 요청 받은 사람(나)

            // fromID 이름 → tv_name_delete
            if (fromEmail != null && !fromEmail.isEmpty()) {
                db.collection("Users")
                        .document(fromEmail)
                        .get()
                        .addOnSuccessListener(snap -> {
                            if (!snap.exists() || !deleteDialog.isShowing()) return;
                            String fromName = snap.getString("name");
                            if (fromName != null && !fromName.trim().isEmpty() && tvNameDelete != null) {
                                tvNameDelete.setText(fromName);
                            }
                        })
                        .addOnFailureListener(e ->
                                Log.e("NOTI", "showRejectConfirmPopup: fromName load fail", e)
                        );
            }

            // toID 이름 → tv_sub_message 안의 "김국민" 자리에 세팅
            if (toEmail != null && !toEmail.isEmpty()) {
                db.collection("Users")
                        .document(toEmail)
                        .get()
                        .addOnSuccessListener(snap -> {
                            if (!snap.exists() || !deleteDialog.isShowing()) return;
                            String toName = snap.getString("name");
                            if (toName != null && !toName.trim().isEmpty() && tvSubMessage != null) {
                                tvSubMessage.setText(
                                        toName + "님께 더 좋은 매칭이 이뤄질 수 있게\n캐칭이 더 노력할게요!"
                                );
                            }
                        })
                        .addOnFailureListener(e ->
                                Log.e("NOTI", "showRejectConfirmPopup: toName load fail", e)
                        );
            }
        }

        View btnConfirm = deleteDialog.findViewById(R.id.btn_confirm_layout);
        btnConfirm.setOnClickListener(v -> deleteDialog.dismiss());

        deleteDialog.show();
    }



    /** 카카오 아이디 팝업 (Users 컬렉션에서 kakaoId 가져와서 표시) */
    public void showKakaoPopup() {
        Dialog kakaoDialog = new Dialog(requireContext());
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

        TextView tvLine1   = kakaoDialog.findViewById(R.id.tv_line1);
        TextView tvKakaoId = kakaoDialog.findViewById(R.id.tv_kakao_id);

        tvKakaoId.setText("미등록");

        String otherEmail = (currentItem != null) ? currentItem.otherEmail : null;

        if (db != null && otherEmail != null && !otherEmail.isEmpty()) {
            db.collection("Users")
                    .document(otherEmail)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String kakaoId = doc.getString("kakaoId");
                        String name    = doc.getString("name");

                        if (kakaoId == null || kakaoId.trim().isEmpty()) {
                            kakaoId = "미등록";
                        }
                        tvKakaoId.setText(kakaoId);

                        if (name != null && !name.trim().isEmpty()) {
                            tvLine1.setText(name + "님의 카카오톡 아이디는");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("NOTI", "카카오 아이디 조회 실패", e);
                    });
        }

        View btnCopy = kakaoDialog.findViewById(R.id.btn_copy);
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> {
                String kakaoId = tvKakaoId.getText().toString();
                if (!"미등록".equals(kakaoId)) {
                    ClipboardManager clipboard =
                            (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("kakaoId", kakaoId);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(requireContext(), "카카오 아이디가 복사되었습니다.", Toast.LENGTH_SHORT).show();
                }
                kakaoDialog.dismiss();
            });
        }

        kakaoDialog.show();
    }
}
