package com.example.mobile2025s2_1_2.matching;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

import java.util.List;

public class RoommateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_roommate_main);

        RecyclerView recyclerView = findViewById(R.id.profile_roommate);

// ✅ 가로 스크롤
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

// ✅ 카드 데이터 연결
        List<RoommateCardData.RoommateData> roommateList = RoommateCardData.loadRoommates(this);
        RoommateCardAdapter adapter = new RoommateCardAdapter(this, roommateList);
        recyclerView.setAdapter(adapter);

// ✅ 스냅 효과: 중앙 카드에 자동 정렬
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
    }
}