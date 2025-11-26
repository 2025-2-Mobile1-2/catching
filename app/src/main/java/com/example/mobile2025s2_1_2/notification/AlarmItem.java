package com.example.mobile2025s2_1_2.notification;

public class AlarmItem {

    public String docId;        // Firestore 문서 ID
    public String text;         // 화면에 보여줄 문장
    public boolean isNew;       // N 뱃지 표시 여부
    public String fromID;       // Firestore에서 그대로 가져온 ID (사용 중)
    public String state;        // "request" / "accepted" / "rejected"
    public String category;     // "roommate" 등
    public boolean isReceived;  // 받은 탭인지 / 보낸 탭인지

    // 새로 추가되는 필드
    // 상대방 이메일 (받은 탭이면 fromID, 보낸 탭이면 toID)
    public String otherEmail;   // 🔥 카카오 아이디 조회용

    // 팝업 상태 관리용
    public boolean clickedBefore; // 한번이라도 클릭되었는지
    public int lastPopupType;     // 0: 없음, 1: popup1, 2: popup2, 4: popup4

    // Firestore용 빈 생성자
    public AlarmItem() { }

    public AlarmItem(String docId,
                     String text,
                     boolean isNew,
                     String fromID,
                     String state,
                     String category,
                     boolean isReceived,
                     String otherEmail) {   // 🔥 새 필드 포함된 생성자

        this.docId = docId;
        this.text = text;
        this.isNew = isNew;
        this.fromID = fromID;
        this.state = state;
        this.category = category;
        this.isReceived = isReceived;

        // 새 필드 저장
        this.otherEmail = otherEmail;

        // UI 상태 초기화
        this.clickedBefore = false;
        this.lastPopupType = 0;
    }
}
