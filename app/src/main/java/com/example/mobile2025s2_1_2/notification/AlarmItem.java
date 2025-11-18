package com.example.mobile2025s2_1_2.notification;

public class AlarmItem {
    public String text;
    public boolean isNew;   // N 배지 표시 여부

    public boolean clickedBefore = false;  // 🔥 첫 클릭 여부 저장
    public int lastPopupType = 0;         // 🔥 마지막으로 띄운 팝업 기록 (1,2,4)

    public AlarmItem(String text, boolean isNew) {
        this.text = text;
        this.isNew = isNew;
    }
}
