package com.example.mobile2025s2_1_2.notification;

import android.app.Dialog;
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
    private String matchedUserName;
    private String currnetUserName;

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

        db.collection("Users")
                .document(currentUserEmail)
                .get()
                .addOnSuccessListener(docMyEmail ->{
                    String userName = docMyEmail.getString("name");
                    currnetUserName = userName;
                });

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

    /** Firestore에서 받은 매칭 불러오기 (toID == 나) */
    private void loadReceivedFromFirestore() {
        if (db == null || currentUserEmail == null) return;

        db.collection("matching_status")
                // ★ 여기서는 전체 가져오고
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

                        // 🔥 내가 받은 알림만 남기기
                        if (toId == null || !toId.equals(currentUserEmail)) {
                            continue;
                        }

                        Log.d("NOTI_REC",
                                "doc=" + docId +
                                        ", fromID=" + fromId +
                                        ", toID=" + toId +
                                        ", state=" + state);

                        String fromName = "  ";
                        String text;
                        if ("accepted".equals(state)) {
                            text = fromName + " 님의 매칭을 수락했습니다.";
                        } else if ("rejected".equals(state)) {
                            text = fromName + " 님의 매칭을 거절했습니다.";
                        } else { // "request" 또는 null
                            if(category.equals("roommate")){
                                text = fromName + " 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!";
                            } else if(category.equals("mentorship")){
                                text = fromName + " 님으로부터 진로·전공 멘토 매칭 신청이 왔습니다!";
                            }else{
                                text = fromName + " 님으로부터 교내·교외 활동 팀원 신청이 왔습니다!";
                            }
                        }


                        AlarmItem item = new AlarmItem(
                                docId,
                                text,
                                Boolean.TRUE.equals(isNewForB),
                                fromId,
                                state != null ? state : "request",
                                category != null ? category : "roommate",
                                true// 받은 탭
                        );

                        receivedList.add(item);
                        // 🔥 Firestore에서 이름 가져오면 item.text만 업데이트
                        db.collection("Users")
                                .document(fromId)
                                .get()
                                .addOnSuccessListener(docUser -> {
                                    String fetchedName = docUser.getString("name");
                                    if (fetchedName != null) {
                                        if ("accepted".equals(item.state)) {
                                            item.text = fetchedName + " 님의 매칭을 수락했습니다.";
                                            item.lastPopupType = 2;
                                        } else if ("rejected".equals(item.state)) {
                                            item.text = fetchedName + " 님의 매칭을 거절했습니다.";
                                            item.lastPopupType = 4;
                                        } else {
                                            if(item.category.equals("roommate")){
                                                item.text = fetchedName + " 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!";
                                            } else if(item.category.equals("mentorship")){
                                                item.text = fetchedName + " 님으로부터 진로·전공 멘토 매칭 신청이 왔습니다!";
                                            }else{
                                                item.text = fetchedName + " 님으로부터 교내·교외 활동 팀원 신청이 왔습니다!";
                                            }
                                        }
                                        matchedUserName = fetchedName;
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }

                    adapter = new AlarmAdapter(
                            new ArrayList<>(receivedList),
                            true,
                            (item, isReceivedList) -> handleAlarmClick(item, isReceivedList)
                    );
                    recycler.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Log.e("NOTI_REC", "loadReceivedFromFirestore error", e);
                });
    }

    /** Firestore에서 보낸 매칭 불러오기 (fromID == 나) */
    private void loadSentFromFirestore() {
        if (db == null || currentUserEmail == null) return;

        db.collection("matching_status")
                // ★ 여기서도 전체 가져오고
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

                        // 🔥 내가 보낸 알림만 남기기
                        if (fromId == null || !fromId.equals(currentUserEmail)) {
                            continue;
                        }

                        Log.d("NOTI_SENT",
                                "doc=" + docId +
                                        ", fromID=" + fromId +
                                        ", toID=" + toId +
                                        ", state=" + state);

                        String toName ="  ";

                        String text;
                        if ("accepted".equals(state)) {
                            text = toName + " 님이 매칭을 수락했습니다. 카카오톡 아이디를 확인해 보세요.";
                        } else if ("rejected".equals(state)) {
                            text = toName + " 님이 매칭을 거절했습니다.";
                        } else { // "request" 또는 null
                            if(category.equals("roommate")){
                                text = toName + " 님께 기숙사 룸메이트 매칭 신청을 보냈습니다!";
                            } else if(category.equals("mentorship")){
                                text = toName + " 님께 진로·전공 멘토 매칭 신청을 보냈습니다!";
                            }else{
                                text = toName + " 님께 교내·교외 활동 팀원 신청을 보냈습니다!";
                            }
                        }

                        AlarmItem item = new AlarmItem(
                                docId,
                                text,
                                Boolean.TRUE.equals(isNewForA),
                                fromId,
                                state != null ? state : "request",
                                category != null ? category : "roommate",
                                false// 보낸 탭
                        );

                        sentList.add(item);
                        // 🔥 Firestore에서 이름 가져오면 item.text만 업데이트
                        db.collection("Users")
                                .document(toId)
                                .get()
                                .addOnSuccessListener(doctoUser -> {
                                    String fetchedName = doctoUser.getString("name");
                                    if (fetchedName != null) {
                                        if ("accepted".equals(item.state)) {
                                            item.text = fetchedName + " 님이 매칭을 수락했습니다.";
                                        } else if ("rejected".equals(item.state)) {
                                            item.text = fetchedName + " 님이 매칭을 거절했습니다.";
                                        } else {
                                            if(item.category.equals("roommate")){
                                                item.text = fetchedName + " 님께 기숙사 룸메이트 매칭 신청을 보냈습니다!";
                                            } else if(item.category.equals("mentorship")){
                                                item.text = fetchedName + " 님께 진로·전공 멘토 매칭 신청을 보냈습니다!";
                                            }else{
                                                item.text = fetchedName + " 님께 교내·교외 활동 팀원 신청을 보냈습니다!";
                                            }
                                        }
                                        matchedUserName = fetchedName;
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }

                    adapter = new AlarmAdapter(
                            new ArrayList<>(sentList),
                            false,
                            (item, isReceivedList) -> handleAlarmClick(item, isReceivedList)
                    );
                    recycler.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Log.e("NOTI_SENT", "loadSentFromFirestore error", e);
                });
    }

    /** 알람 아이템 클릭 처리 공통 로직 */
    private void handleAlarmClick(AlarmItem item, boolean isReceivedList) {
        currentItem = item;

        // 1) Firestore에 읽음 처리 요청 (배지는 Adapter 쪽에서 바로 숨김)
        markAlarmRead(item, isReceivedList);

        // 2) 보낸 매칭 탭 클릭
        if (!isReceivedList) {
            if ("accepted".equals(item.state)) {
                showKakaoPopup();
            } else if ("rejected".equals(item.state)) {
                showRejectConfirmPopup();
            }
            return;
        }
        Log.d("NOTI_T", "k=" + item.lastPopupType );

        // 3) 받은 매칭 탭 클릭
        if (item.lastPopupType == 2) {
            showConfirmPopup();
        } else if (item.lastPopupType == 4) {
            showRejectConfirmPopup();
        }else{
            item.clickedBefore = true;
            item.lastPopupType = 1;
            showProfilePopup();
        }
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

    //룸메이트 팝업
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

        //닫기 버튼
        ImageView btnClose = profileDialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            profileDialog.dismiss();

            if (currentItem != null) {
                currentItem.clickedBefore = false;
                currentItem.lastPopupType = 0;
            }
        });


        //상대 유저 정보
        db.collection("Users")
                .document(currentItem.fromID)
                .get()
                .addOnSuccessListener(doc -> {
                    String name = doc.getString("name");
                    String gender = doc.getString("gender");
                    String dorm = doc.getString("dorm");
                    String age = doc.getString("age");
                    String mbti = doc.getString("mbti");
                    String drink = doc.getString("drink");
                    String smoking = doc.getString("smoking");
                    String clean = doc.getString("clean");
                    String sleep = doc.getString("sleep");
                    String sensitive = doc.getString("sensitive");
                    String sleepTime = doc.getString("sleepTime");
                    String wakeTime = doc.getString("wakeTime");

                    //정보 연결하기
                    TextView userName = profileDialog.findViewById(R.id.roommate_name);
                    userName.setText(name != null ? name : "정보 없음");

                    TextView userGender = profileDialog.findViewById(R.id.roommate_gender);
                    userGender.setText(gender != null ? gender : "정보 없음");

                    TextView userDorm = profileDialog.findViewById(R.id.roommate_dormitory);
                    userDorm.setText(dorm != null ? dorm : "정보 없음");

                    TextView userAge = profileDialog.findViewById(R.id.roommate_age);
                    userAge.setText(age != null ? age : "정보 없음");

                    TextView userMbti = profileDialog.findViewById(R.id.roommate_mbti);
                    userMbti.setText(mbti != null ? mbti : "정보 없음");

                    TextView userDrink = profileDialog.findViewById(R.id.roommate_drink);
                    userDrink.setText(drink != null ? "음주 " + drink : "정보 없음");

                    TextView userSmoke = profileDialog.findViewById(R.id.roommate_smoke);
                    userSmoke.setText(smoking != null ? "흡연 " + smoking : "정보 없음");

                    //자고 일어나는 시간
                    TextView userSleepTime = profileDialog.findViewById(R.id.roommate_time);
                    String timeText;
                    if (sleepTime != null && wakeTime != null) {
                        timeText = sleepTime + " ~ " + wakeTime;
                    } else if (sleepTime != null) {
                        timeText = sleepTime;
                    } else if (wakeTime != null) {
                        timeText = wakeTime;
                    } else {
                        timeText = "정보 없음";
                    }
                    userSleepTime.setText(timeText);

                    TextView userCleanValue = profileDialog.findViewById(R.id.roommate_clean_value);
                    userCleanValue.setText(clean != null ? clean : "정보 없음");
                    SeekBar userCleanBar = profileDialog.findViewById(R.id.roommate_clean_seekbar);
                    userCleanBar.setProgress(Integer.parseInt(clean != null ? clean : "0"));

                    TextView userSleepValue = profileDialog.findViewById(R.id.roommate_sleep_value);
                    userSleepValue.setText(sleep != null ? sleep : "정보 없음");
                    SeekBar userSleepBar = profileDialog.findViewById(R.id.roommate_sleep_seekbar);
                    userSleepBar.setProgress(Integer.parseInt(sleep != null ? sleep : "0"));

                    TextView userSensitiveValue = profileDialog.findViewById(R.id.roommate_sensitive_value);
                    userSensitiveValue.setText(sensitive != null ? sensitive : "정보 없음");
                    SeekBar userSensitivepBar = profileDialog.findViewById(R.id.roommate_sensitive_seekbar);
                    userSensitivepBar.setProgress(Integer.parseInt(sensitive != null ? sensitive : "0"));


                });

        //수락, 취소 버튼
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
            loadReceivedFromFirestore();
            showConfirmPopup();
        });

        // ✅ 거절 버튼
        btnReject.setOnClickListener(v -> {
            profileDialog.dismiss();
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

        //매칭 유저 이름
        TextView userName = confirmDialog.findViewById(R.id.tv_name_confirm);
        userName.setText(matchedUserName);

        //매칭 카테고리 메세지
        TextView confirmCate = confirmDialog.findViewById(R.id.tv_message);
        if(currentItem.category.equals("roommate")){
            confirmCate.setText("기숙사 룸메이트 매칭을 수락했어요!");
        }else if(currentItem.category.equals("mentorship")){
            confirmCate.setText("진로·전공 멘토 매칭을 수락했어요!");
        }else{
            confirmCate.setText("교내·교외 활동 팀원 매칭을 수락했어요!");
        }

        //매칭 메세지
        TextView confirmText = confirmDialog.findViewById(R.id.tv_sub_message);
        confirmText.setText(matchedUserName+" 님과 24시간 이야기나 정말 수준의\n 대화를 기대하세요!");


        //확인 버튼
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

        //이름
        TextView userName = rejectDialog.findViewById(R.id.tv_name_reject);
        userName.setText(matchedUserName);

        //매칭 메세지
        TextView rejectText = rejectDialog.findViewById(R.id.tv_sub_message);
        rejectText.setText(currnetUserName+" 님께 더 좋은 매칭이 이뤄질 수 있게 캐칭이 더 \n노력할게요!");


        //거절 버튼
        View btnReject = rejectDialog.findViewById(R.id.btn_reject_layout);
        btnReject.setOnClickListener(v -> {
            rejectDialog.dismiss();
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
            loadReceivedFromFirestore();
            showRejectConfirmPopup();
        });

        //취소 버튼
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

        //이름
        TextView userName = deleteDialog.findViewById(R.id.tv_name_delete);
        userName.setText(matchedUserName);

        //매칭 카테고리 메세지
        TextView deleteCate = deleteDialog.findViewById(R.id.tv_message);
        if(currentItem.category.equals("roommate")){
            deleteCate.setText("기숙사 룸메이트 매칭을 거절했어요!");
        }else if(currentItem.category.equals("mentorship")){
            deleteCate.setText("진로·전공 멘토 매칭을 거절했어요!");
        }else{
            deleteCate.setText("교내·교외 활동 팀원 매칭을 거절했어요!");
        }

        //매칭 메세지
        TextView deletetText = deleteDialog.findViewById(R.id.tv_sub_message);
        deletetText.setText(currnetUserName+" 님께 더 좋은 매칭이 이뤄질 수 있게 캐칭이 더 \n노력할게요!");

        //확인 버튼
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

        TextView userName = kakaoDialog.findViewById(R.id.tv_line1);
        userName.setText(matchedUserName+" 님의 카카오톡 아이디는");

        View btnCopy = kakaoDialog.findViewById(R.id.btn_copy);
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> kakaoDialog.dismiss());
        }

        kakaoDialog.show();
    }
}
//