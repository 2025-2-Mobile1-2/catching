package com.example.mobile2025s2_1_2.matching.roommate;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import com.example.mobile2025s2_1_2.R;

public class RoommateCardData {

    public static class RoommateData {
        private String name;
        private String sex;
        private String dormitory;
        private String age;
        private String mbti;
        private String drink;
        private String smoke;
        private int clean;
        private int sleep;
        private int subtlety;

        // 🔥 [추가됨] Firestore 데이터 → 카드 객체로 생성할 수 있게 하는 생성자
        public RoommateData(String name, String sex, String dormitory, String age,
                            String mbti, String drink, String smoke,
                            int clean, int sleep, int subtlety) {

            this.name = name;
            this.sex = sex;
            this.dormitory = dormitory;
            this.age = age;
            this.mbti = mbti;
            this.drink = drink;
            this.smoke = smoke;
            this.clean = clean;
            this.sleep = sleep;
            this.subtlety = subtlety;
            this.dormitory=dormitory;
        }

        // 🔥 Gson/Firestore가 필요로 하는 기본 생성자
        public RoommateData() {}

        // Getter 메서드
        public String getName() { return name; }
        public String getSex() { return sex; }
        public String getDomitory() { return dormitory; }
        public String getAge() { return age; }
        public String getMbti() { return mbti; }
        public String getDrink() { return drink; }
        public String getSmoke() { return smoke; }
        public int getClean() { return clean; }
        public int getSleep() { return sleep; }
        public int getSubtlety() { return subtlety; }

        public String  getDormitory() {
            return dormitory;
        }
    }


    // (⚠ Firestore 사용하면 JSON 필요한 경우가 거의 없지만, 혹시 raw JSON도 쓸 수 있으니 유지)
    public static List<RoommateData> loadRoommates(Context context) {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.user_data);
            InputStreamReader reader = new InputStreamReader(inputStream);

            Type listType = new TypeToken<List<RoommateData>>() {}.getType();
            List<RoommateData> roommateList = new Gson().fromJson(reader, listType);

            reader.close();
            inputStream.close();

            return roommateList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
