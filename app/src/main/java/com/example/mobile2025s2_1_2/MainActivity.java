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

        // TODO: 이 값들은 SharedPreferences나 토큰에서 실제 로그인 상태를 가져와야 합니다.
        boolean login = true;  // (현재는 테스트용 하드코딩)
        boolean signin = true; // (현재는 테스트용 하드코딩)

        // 1. (로그인 O, 회원가입 O) -> 홈 화면으로
        if (signin && login) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish(); // (중요) 현재 액티비티를 종료해서 뒤로가기 스택에 안 남김
            return;   // (중요) 아래의 setContentView를 실행하지 않고 즉시 종료
        }

        // 2. (로그인 X, 회원가입 O) -> 로그인 화면으로
        else if (signin && !login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // (중요) 현재 액티비티 종료
            return;   // (중요) 아래의 setContentView를 실행하지 않고 즉시 종료
        }

        // 3. (회원가입 X) -> 아무것도 안 함 (현재 화면을 보여줌)
        // 이 경우에만 아래의 UI 코드가 실행되어 activity_main (회원가입 화면 등)이 표시됨

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}