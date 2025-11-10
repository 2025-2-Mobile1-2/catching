package com.example.mobile2025s2_1_2.matching;

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
        private String major;
        private String mbti;

        // Getter 메서드 (Gson이 자동으로 매핑함)
        public String getName() { return name; }
        public String getMajor() { return major; }
        public String getMbti() { return mbti; }
    }
    // JSON 파일을 읽어서 룸메이트 데이터 리스트로 반환
    public static List<RoommateData> loadRoommates(Context context) {
        try {
            // 1️⃣ JSON 파일 가져오기 (res/raw/roommate_data.json)
            InputStream inputStream = context.getResources().openRawResource(R.raw.roommate_data);

            // 2️⃣ 파일을 문자 단위로 읽기 위한 Reader
            InputStreamReader reader = new InputStreamReader(inputStream);

            // 3️⃣ JSON → List<RoommateData>로 변환 (Gson 사용)
            Type listType = new TypeToken<List<RoommateData>>() {}.getType();
            List<RoommateData> roommateList = new Gson().fromJson(reader, listType);

            // 4️⃣ 리소스 닫기
            reader.close();
            inputStream.close();

            return roommateList;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}