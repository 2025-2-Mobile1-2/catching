package com.example.mobile2025s2_1_2.myprofile;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mobile2025s2_1_2.*;
import com.example.mobile2025s2_1_2.utils.BottomNavBarHelper;

public class MyprofileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_main);

        LinearLayout bottomNavBar = findViewById(R.id.custom_navbar);
        BottomNavBarHelper.setupCustomNav(this, bottomNavBar);
        ImageView myprofile_nav_Icon = bottomNavBar.findViewById(R.id.nav_myprofile_icon);
        myprofile_nav_Icon.setImageResource(R.drawable.ic_profile_non);
        myprofile_nav_Icon.setImageResource(R.drawable.ic_profile);
        TextView myprofile_nav_Text = bottomNavBar.findViewById(R.id.nav_myprofile_text);
        myprofile_nav_Text.setTextColor(Color.parseColor("#FFCCECE3"));
        myprofile_nav_Text.setTextColor(Color.parseColor("#FF2DD7A4"));
    }
}