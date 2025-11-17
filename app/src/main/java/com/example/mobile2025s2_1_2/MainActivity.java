package com.example.mobile2025s2_1_2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.mobile2025s2_1_2.home.HomeActivity;
import com.example.mobile2025s2_1_2.login.LoginActivity;
;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean login = true;
        boolean signin = true;
        if (signin && login) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
//            finish();
        } else if(signin && !login){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
//            finish();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}