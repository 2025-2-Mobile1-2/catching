package com.example.mobile2025s2_1_2.home;

import java.util.Arrays;
import java.util.List;
public class HomeCardData {

    // 교내외 활동 카드 정보 형식
    public static class CardInfo {
        public String imageUrl;
        public String webUrl;
        public CardInfo(String imageUrl, String webUrl) {
            this.imageUrl = imageUrl;
            this.webUrl = webUrl;
        }
    }

    //교내 활동 정보
    public static List<CardInfo> getDataSchool() {
        return Arrays.asList(
                new CardInfo("https://example.com/school1.jpg", "https://school.example.com/page1"),
                new CardInfo("https://example.com/school2.jpg", "https://school.example.com/page2")
        );
    }

    //교외 활동 정보
    public static List<CardInfo> getDataOut() {
        return Arrays.asList(
                new CardInfo("https://example.com/out1.jpg", "https://out.example.com/page1"),
                new CardInfo("https://example.com/out2.jpg", "https://out.example.com/page2")
        );
    }
}
