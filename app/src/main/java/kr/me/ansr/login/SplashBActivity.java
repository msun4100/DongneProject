package kr.me.ansr.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;

public class SplashBActivity extends AppCompatActivity {

    Button btn1;
    Button btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_b);

        Toast.makeText(SplashBActivity.this, "SplashB\n"+ PropertyManager.getInstance().getRegistrationId(), Toast.LENGTH_SHORT).show();
        btn1 = (Button)findViewById(R.id.btn_login);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashBActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btn2 = (Button)findViewById(R.id.btn_signup);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashBActivity.this, SignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });



    }
}
