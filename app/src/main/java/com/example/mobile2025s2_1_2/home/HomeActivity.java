package com.example.mobile2025s2_1_2.home;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.home.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_main);

        //하단 navBar
        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        BottomNavBarHelper.setActiveTab(bottomNavBar, R.id.nav_home);

        //교내활동 정보
        RecyclerView cardschool = findViewById(R.id.card_school);
        //교내활동 정보 리스트
        List<Integer> images_school = Arrays.asList(
                R.drawable.test,
                R.drawable.test,
                R.drawable.test
        );
        HomeCardAdapter adapter_school = new HomeCardAdapter(images_school);
        cardschool.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) // 가로 스크롤
        );
        cardschool.setAdapter(adapter_school);

        //교외활동 정보
        RecyclerView cardout = findViewById(R.id.card_out);
        //교외활동 정보 리스트
        List<Integer> images_out = Arrays.asList(
                R.drawable.test,
                R.drawable.test,
                R.drawable.test,
                R.drawable.test
        );
        HomeCardAdapter adapter_out = new HomeCardAdapter(images_out);
        cardout.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) // 가로 스크롤
        );
        cardout.setAdapter(adapter_out);
    }
}