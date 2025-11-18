package com.example.mobile2025s2_1_2.notification;

public class AlarmItem {
    public String text;
    public boolean isNew;   // N 배지 표시 여부

    public AlarmItem(String text, boolean isNew) {
        this.text = text;
        this.isNew = isNew;
    }
}
