package com.example.mobile2025s2_1_2.notification;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationFragment extends Fragment {

    // 토글
    private RelativeLayout toggleReceived, toggleSent;
    private TextView tvReceived, tvSent;

    private Dialog profileDialog;
    private Dialog confirmDialog;

    // 리스트
    private RecyclerView recycler;
    private AlarmAdapter adapter;

    // 🔥 추가해야 하는 전역 변수
    private AlarmItem currentItem;   // 🔥 현재 클릭된 아이템 저장 변수

    // 테스트 데이터
    private final List<AlarmItem> received = Arrays.asList(
            new AlarmItem("최북악 님으로부터 진로·전공 멘토 매칭 신청이 왔습니다!", true),   // 새 알림 (N)
            new AlarmItem("최북악 님으로부터 기숙사 룸메이트 매칭 신청이 왔습니다!", true)   // 읽은 알림
    );

    private final List<AlarmItem> sent = Arrays.asList(
            new AlarmItem("김국민 님께 진로·전공 멘토 매칭 신청을 보냈습니다!", false),
            new AlarmItem("홍지우 님께 교내·교외 활동 팀원 매칭 신청을 보냈습니다!", true)  // 새 알림 (N)
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_main, container, false);

        // 하단 navBar
        LinearLayout bottomNavBar = view.findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(requireActivity(), bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_notification);

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

        // 초기: 받은 탭
        setToggleState(false);
        showReceived();

        toggleReceived.setOnClickListener(v -> {
            setToggleState(false);
            showReceived();
        });

        toggleSent.setOnClickListener(v -> {
            setToggleState(true);
            showSent();
        });

        return view;
    }

    /** 받은 탭 데이터 표시 */
    private void showReceived() {
        adapter = new AlarmAdapter(
                new ArrayList<>(received),
                true,
                (item, isReceivedList) -> handleAlarmClick(item, isReceivedList)   // 🔥 추가
        );
        recycler.setAdapter(adapter);
    }



    /** 보낸 탭 데이터 표시 */
    private void showSent() {
        adapter = new AlarmAdapter(
                new ArrayList<>(sent),
                false,
                (item, isReceivedList) -> handleAlarmClick(item, isReceivedList)   // 🔥 listener 추가
        );
        recycler.setAdapter(adapter);
    }

    private void handleAlarmClick(AlarmItem item, boolean isReceivedList) {

        // 🔥 현재 클릭된 아이템 기억
        currentItem = item;

        // 보낸 매칭 → 카카오 팝업만
        if (!isReceivedList) {
            showKakaoPopup();
            return;
        }

        // 🔥 이미 눌린 적 있음 → 마지막 팝업을 다시 띄우기
        if (item.clickedBefore) {
            if (item.lastPopupType == 2) {
                showConfirmPopup(); // profile_popup2
            } else if (item.lastPopupType == 4) {
                showRejectConfirmPopup(); // profile_popup4
            }
            return;
        }

        // 🔥 처음 클릭 → 기본 popup 띄우기
        item.clickedBefore = true;
        item.lastPopupType = 1; // profile_popup
        showProfilePopup();
    }




    /** 토글의 활성/비활성 색상 및 폰트 전환(UI) */
    private void setToggleState(boolean isSentActive) {
        Typeface semi = ResourcesCompat.getFont(requireContext(), R.font.semibold);
        Typeface reg  = ResourcesCompat.getFont(requireContext(), R.font.regular);

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

            // 🔥 상태 초기화!
            if (currentItem != null) {
                currentItem.clickedBefore = false;
                currentItem.lastPopupType = 0;
            }
        });

        ImageView btnAccept = profileDialog.findViewById(R.id.btn_accept);
        ImageView btnReject = profileDialog.findViewById(R.id.btn_reject);

        btnAccept.setOnClickListener(v -> {
            profileDialog.dismiss();
            if (currentItem != null) currentItem.lastPopupType = 2;
            showConfirmPopup();
        });

        btnReject.setOnClickListener(v -> {
            profileDialog.dismiss();
            showRejectPopup();
        });



        profileDialog.show();
    }

    private void showConfirmPopup() {

        // 🔥 마지막으로 뜬 팝업 = 2 저장
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

        // ✔ popup3의 "취소하기" 버튼 ID에 맞춰서 변경해야 함
        View btnReject = rejectDialog.findViewById(R.id.btn_reject_layout);
        btnReject.setOnClickListener(v -> {
            rejectDialog.dismiss();
            showRejectConfirmPopup();   // ★ 새 팝업 띄우기
        });
        // ✔ popup3의 "취소하기" 버튼 ID에 맞춰서 변경해야 함
        View btnClose = rejectDialog.findViewById(R.id.btn_delete_layout);

        btnClose.setOnClickListener(v -> {
            rejectDialog.dismiss();

            // 🔥 상태 초기화!
            if (currentItem != null) {
                currentItem.clickedBefore = false;
                currentItem.lastPopupType = 0;
            }
        });

        rejectDialog.show();
    }

    private void showRejectConfirmPopup() {

        // 🔥 마지막으로 뜬 팝업 = 4 저장
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

        // XML의 확인 버튼 ID → btn_confirm_layout
        View btnConfirm = deleteDialog.findViewById(R.id.btn_confirm_layout);

        btnConfirm.setOnClickListener(v -> deleteDialog.dismiss());

        deleteDialog.show();
    }




    // ★ 추가된 카카오톡 아이디 팝업 함수
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

        // 닫기/복사 버튼
        View btnCopy = kakaoDialog.findViewById(R.id.btn_copy);
        if (btnCopy != null) {
            btnCopy.setOnClickListener(v -> kakaoDialog.dismiss());
        }

        kakaoDialog.show();
    }
}//
