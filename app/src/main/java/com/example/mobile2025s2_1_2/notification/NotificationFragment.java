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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.R;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 이메일 → 이름 / 카카오 아이디 캐시
    private final Map<String, String> nameCache  = new HashMap<>();
    private final Map<String, String> kakaoCache = new HashMap<>();

    // 콜백용 인터페이스
    private interface StringCallback {
        void onResult(@Nullable String value);
    }

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

    // ============================================================
    //  Firestore에서 데이터 불러오기
    // ============================================================

    /** Firestore에서 받은 매칭 불러오기 (toID == 나) */
    private void loadReceivedFromFirestore() {
        if (db == null || currentUserEmail == null) return;

        db.collection("matching_status")
                // 지금은 전체를 가져온 뒤, 코드에서 toID == currentUserEmail만 필터
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    receivedList.clear();

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String docId    = doc.getId();
                        String fromId   = doc.getString("fromID");
                        String toId     = doc.getString("toID");
                        String state    = doc.getString("state");
                        String category = doc.getString("category");
                        Boolean isNewForB = doc.getBoolean("isNewForB");

                        // 내가 받은 알림만 남기기
                        if (toId == null || !toId.equals(currentUserEmail)) {
                            continue;
                        }

                        Log.d("NOTI_REC",
                                "doc=" + docId +
                                        ", fromID=" + fromId +
                                        ", toID=" + toId +
                                        ", state=" + state);

                        String display = (fromId != null) ? fromId : "상대";
                        String safeState    = (state != null) ? state : "request";
                        String safeCategory = (category != null) ? category : "roommate";

                        // 일단 이메일 기준으로 문장 생성 (이름은 나중에 갱신)
                        String text = buildReceivedText(display, safeState, safeCategory);

                        AlarmItem item = new AlarmItem(
                                docId,
                                text,
                                Boolean.TRUE.equals(isNewForB),
                                fromId,
                                safeState,
                                safeCategory,
                                true,       // 받은 탭
                                fromId      // otherEmail: 상대 이메일 = fromID
                        );

                        receivedList.add(item);
                    }

                    adapter = new AlarmAdapter(
                            new ArrayList<>(receivedList),
                            true,
                            (item, isReceivedList) -> handleAlarmClick(item, isReceivedList)
                    );
                    recycler.setAdapter(adapter);

                    // 🔥 이메일 → 이름으로 갱신
                    for (int i = 0; i < receivedList.size(); i++) {
                        final int index = i;
                        AlarmItem item = receivedList.get(i);
                        final String email = item.otherEmail;

                        if (email == null) continue;

                        fetchUserNameByEmail(email, name -> {
                            if (name == null) return;
                            String newText = buildReceivedText(name, item.state, item.category);
                            item.text = newText;
                            adapter.notifyItemChanged(index);
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e("NOTI_REC", "loadReceivedFromFirestore error", e));
    }

    /** Firestore에서 보낸 매칭 불러오기 (fromID == 나) */
    private void loadSentFromFirestore() {
        if (db == null || currentUserEmail == null) return;

        db.collection("matching_status")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    sentList.clear();

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String docId    = doc.getId();
                        String fromId   = doc.getString("fromID");
                        String toId     = doc.getString("toID");
                        String state    = doc.getString("state");
                        String category = doc.getString("category");
                        Boolean isNewForA = doc.getBoolean("isNewForA");

                        // 내가 보낸 알림만 남기기
                        if (fromId == null || !fromId.equals(currentUserEmail)) {
                            continue;
                        }

                        Log.d("NOTI_SENT",
                                "doc=" + docId +
                                        ", fromID=" + fromId +
                                        ", toID=" + toId +
                                        ", state=" + state);

                        String display = (toId != null) ? toId : "상대";
                        String safeState    = (state != null) ? state : "request";
                        String safeCategory = (category != null) ? category : "roommate";

                        String text = buildSentText(display, safeState, safeCategory);

                        AlarmItem item = new AlarmItem(
                                docId,
                                text,
                                Boolean.TRUE.equals(isNewForA),
                                fromId,
                                safeState,
                                safeCategory,
                                false,      // 보낸 탭
                                toId        // otherEmail: 상대 이메일 = toID
                        );

                        sentList.add(item);
                    }

                    adapter = new AlarmAdapter(
                            new ArrayList<>(sentList),
                            false,
                            (item, isReceivedList) -> handleAlarmClick(item, isReceivedList)
                    );
                    recycler.setAdapter(adapter);

                    // 🔥 이메일 → 이름으로 갱신
                    for (int i = 0; i < sentList.size(); i++) {
                        final int index = i;
                        AlarmItem item = sentList.get(i);
                        final String email = item.otherEmail;

                        if (email == null) continue;

                        fetchUserNameByEmail(email, name -> {
                            if (name == null) return;
                            String newText = buildSentText(name, item.state, item.category);
                            item.text = newText;
                            adapter.notifyItemChanged(index);
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e("NOTI_SENT", "loadSentFromFirestore error", e));
    }

    // ============================================================
    //  이메일 → 이름 / 카카오아이디 유틸
    // ============================================================

    private void fetchUserNameByEmail(@Nullable String email, @NonNull StringCallback callback) {
        if (email == null || db == null) {
            callback.onResult(null);
            return;
        }

        // 캐시 먼저 확인
        if (nameCache.containsKey(email)) {
            callback.onResult(nameCache.get(email));
            return;
        }

        db.collection("Users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    String name = null;
                    if (!snap.isEmpty()) {
                        name = snap.getDocuments().get(0).getString("name");
                    }
                    if (name == null || name.isEmpty()) {
                        name = email;  // fallback
                    }
                    nameCache.put(email, name);
                    callback.onResult(name);
                })
                .addOnFailureListener(e -> {
                    Log.e("USER_NAME", "fetchUserNameByEmail error", e);
                    callback.onResult(email);  // 실패 시 이메일 그대로
                });
    }

    private void fetchKakaoIdByEmail(@Nullable String email, @NonNull StringCallback callback) {
        if (email == null || db == null) {
            callback.onResult(null);
            return;
        }

        // 캐시 먼저 확인
        if (kakaoCache.containsKey(email)) {
            callback.onResult(kakaoCache.get(email));
            return;
        }

        db.collection("Users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    String kakaoId = null;
                    if (!snap.isEmpty()) {
                        kakaoId = snap.getDocuments().get(0).getString("kakaoId");
                    }
                    if (kakaoId == null || kakaoId.isEmpty()) {
                        kakaoId = null; // 아이디 없으면 null로 두고, 나중에 문구 처리
                    }
                    kakaoCache.put(email, kakaoId);
                    callback.onResult(kakaoId);
                })
                .addOnFailureListener(e -> {
                    Log.e("USER_KAKAO", "fetchKakaoIdByEmail error", e);
                    callback.onResult(null);
                });
    }

    // ============================================================
    //  알림 문구 생성
    // ============================================================

    private String buildReceivedText(String displayName, String state, String category) {
        if ("accepted".equals(state)) {
            return displayName + " 님이 매칭을 수락했습니다.";
        } else if ("rejected".equals(state)) {
            return displayName + " 님이 매칭을 거절했습니다.";
        } else {
            if ("roommate".equals(category)) {
                return displayName + " 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!";
            } else {
                return displayName + " 님으로부터 새로운 매칭 신청이 왔습니다!";
            }
        }
    }

    private String buildSentText(String displayName, String state, String category) {
        if ("accepted".equals(state)) {
            return displayName + " 님이 매칭을 수락했습니다. 카카오톡 아이디를 확인해 보세요.";
        } else if ("rejected".equals(state)) {
            return displayName + " 님이 매칭을 거절했습니다.";
        } else {
            if ("roommate".equals(category)) {
                return displayName + " 님께 기숙사 룸메이트 매칭 신청을 보냈습니다!";
            } else {
                return displayName + " 님께 새로운 매칭 신청을 보냈습니다!";
            }
        }
    }

    // ============================================================
    //  클릭 처리 & 배지 처리
    // ============================================================

    /** 알람 아이템 클릭 처리 공통 로직 */
    private void handleAlarmClick(AlarmItem item, boolean isReceivedList) {
        currentItem = item;

        // 1) Firestore에 읽음 처리 (배지는 AlarmAdapter에서 바로 숨김)
        markAlarmRead(item, isReceivedList);

        // 2) 보낸 매칭 탭 클릭
        if (!isReceivedList) {
            if ("accepted".equals(item.state)) {
                // 수락된 매칭 → 카카오톡 아이디 팝업
                showKakaoPopup();
            } else if ("rejected".equals(item.state)) {
                // 거절된 매칭 → 거절 확인 팝업
                showRejectConfirmPopup();
            }
            return;
        }

        // 3) 받은 매칭 탭 클릭

        // 이미 눌린 적 있음 → 마지막 팝업 다시 띄우기
        if (item.clickedBefore) {
            if (item.lastPopupType == 2) {
                showConfirmPopup();
            } else if (item.lastPopupType == 4) {
                showRejectConfirmPopup();
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

    // ============================================================
    //  팝업들
    // ============================================================

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

        View btnConfirm = confirmDialog.findViewById(R.id.btn_confirm_layout);
        btnConfirm.setOnClickListener(v -> confirmDialog.dismiss());

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

        View btnReject = rejectDialog.findViewById(R.id.btn_reject_layout);
        btnReject.setOnClickListener(v -> {
            rejectDialog.dismiss();
            showRejectConfirmPopup();
        });

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

        View btnConfirm = deleteDialog.findViewById(R.id.btn_confirm_layout);
        btnConfirm.setOnClickListener(v -> deleteDialog.dismiss());

        deleteDialog.show();
    }

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
        MaterialButton btnCopy = kakaoDialog.findViewById(R.id.btn_copy);

        // 현재 선택된 알림의 상대 이메일
        final String email = (currentItem != null) ? currentItem.otherEmail : null;

        // 복사용으로 실제 kakaoId를 보관
        final String[] kakaoHolder = new String[1];
        kakaoHolder[0] = null;

        // 1) 이름 세팅
        fetchUserNameByEmail(email, name -> {
            if (tvLine1 == null) return;
            String who = (name != null && !name.isEmpty())
                    ? name
                    : (email != null ? email : "상대");
            tvLine1.setText(who + "님의 카카오톡 아이디는");
        });

        // 2) 카카오 아이디 세팅
        fetchKakaoIdByEmail(email, kakaoId -> {
            if (tvKakaoId == null) return;

            if (kakaoId == null || kakaoId.isEmpty()) {
                tvKakaoId.setText("등록된 아이디 없음");
                kakaoHolder[0] = null;
            } else {
                tvKakaoId.setText(kakaoId);
                kakaoHolder[0] = kakaoId;
            }
        });

        // 3) 복사 버튼
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> {
                if (kakaoHolder[0] != null) {
                    ClipboardManager cm = (ClipboardManager)
                            requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (cm != null) {
                        cm.setPrimaryClip(ClipData.newPlainText("kakaoId", kakaoHolder[0]));
                    }
                }
                kakaoDialog.dismiss();
            });
        }

        kakaoDialog.show();
    }
}
