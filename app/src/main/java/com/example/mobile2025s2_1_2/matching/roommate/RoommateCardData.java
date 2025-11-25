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

        private String email;
        private String name;
        private String gender;
        private String dorm;
        private String age;
        private String mbti;
        private String alcohol;
        private String smoking;
        private String clean;
        private String sleep;
        private String sensitive;
        private String sleepTime;
        private String wakeTime;

        // 🔥 Firestore 데이터 → 카드 객체 생성자
        public RoommateData(String email, String name, String gender, String dorm, String age,
                            String mbti, String alcohol, String smoking,
                            String clean, String sleep, String sensitive,
                            String sleepTime, String wakeTime) {

            this.email = email;
            this.name = name;
            this.gender = gender;
            this.dorm = dorm;
            this.age = age;
            this.mbti = mbti;
            this.alcohol = alcohol;
            this.smoking = smoking;
            this.clean = clean;
            this.sleep = sleep;
            this.sensitive = sensitive;
            this.sleepTime = sleepTime;
            this.wakeTime = wakeTime;
        }

        // 🔥 Firebase / Gson 용 기본 생성자
        public RoommateData() {}

        // Getter
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getGender() { return gender; }
        public String getDorm() { return dorm; }
        public String getAge() { return age; }
        public String getMbti() { return mbti; }
        public String getAlcohol() { return alcohol; }
        public String getSmoking() { return smoking; }
        public String getClean() { return clean; }
        public String getSleep() { return sleep; }
        public String getSensitive() { return sensitive; }
        public String getSleepTime() { return sleepTime; }
        public String getWakeTime() { return wakeTime; }
    }

    // 🔥 JSON 로드 기능(선택)
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
