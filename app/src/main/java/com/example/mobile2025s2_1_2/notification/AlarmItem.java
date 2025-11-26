package com.example.mobile2025s2_1_2.notification;

public class AlarmItem {
    public String docId;        // Firestore 문서 ID
    public String text;         // 화면에 보여줄 문장
    public boolean isNew;       // N 뱃지 표시 여부
    public String fromID;
    public String state;        // "request" / "accepted" / "rejected"
    public String category;     // "roommate" 등
    public boolean isReceived;  // 받은 탭인지/보낸 탭인지

    // 팝업 상태 관리용 (이미 쓰고 있던 필드)
    public boolean clickedBefore; // 한번이라도 클릭되었는지
    public int lastPopupType;     // 0: 없음, 1: profile_popup, 2: popup2, 4: popup4

    public AlarmItem() {
        // Firestore용 빈 생성자
    }

    public AlarmItem(String docId,
                     String text,
                     boolean isNew,
                     String fromID,
                     String state,
                     String category,
                     boolean isReceived) {
        this.docId = docId;
        this.text = text;
        this.isNew = isNew;
        this.fromID = fromID;
        this.state = state;
        this.category = category;
        this.isReceived = isReceived;
        this.clickedBefore = false;
        this.lastPopupType = 0;
    }
}

