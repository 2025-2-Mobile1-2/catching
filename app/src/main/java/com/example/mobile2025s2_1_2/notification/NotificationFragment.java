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

                            String text;
                            if ("accepted".equals(state)) {
                                text = fromName + " 님이 매칭을 수락했습니다.";
                            } else if ("rejected".equals(state)) {
                                text = fromName + " 님이 매칭을 거절했습니다.";
                            } else { // "request" 또는 null
                                text = fromName + " 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!";
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

                            String text;
                            if ("accepted".equals(state)) {
                                text = toName + " 님이 매칭을 수락했습니다. 카카오톡 아이디를 확인해 보세요.";
                            } else if ("rejected".equals(state)) {
                                text = toName + " 님이 매칭을 거절했습니다.";
                            } else { // "request" 또는 null
                                text = toName + " 님께 기숙사 룸메이트 매칭 신청을 보냈습니다!";
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

        // 1) UI에서 N 뱃지 바로 제거
        if (item.isNew) {
            item.isNew = false;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            // Firestore에도 읽음 처리
            markAlarmRead(item, isReceivedList);
        }

        // 2) 보낸 매칭 탭 클릭
        if (!isReceivedList) {
            if ("accepted".equals(item.state)) {
                showKakaoPopup();
            } else if ("rejected".equals(item.state)) {
                showRejectConfirmPopup();
            }
            return;
        }

        // 3) 받은 매칭 탭 클릭
        if (item.clickedBefore) {
            if (item.lastPopupType == 2) {
                showConfirmPopup();
            } else if (item.lastPopupType == 4) {
                showRejectConfirmPopup();
            }
            return;
        }

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

        // 기본 값
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
}//
