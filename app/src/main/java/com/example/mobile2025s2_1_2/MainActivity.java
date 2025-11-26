package com.example.mobile2025s2_1_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.mobile2025s2_1_2.home.HomeActivity;
import com.example.mobile2025s2_1_2.login.LoginActivity;
import com.example.mobile2025s2_1_2.login.StartActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}