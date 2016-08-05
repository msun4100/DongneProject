package kr.me.ansr.login;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-07-11.
 */
public class SignupWayActivity extends AppCompatActivity {

    private String TAG = SignupWayActivity.class.getSimpleName();
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_way);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SignUp Choice");
        //login siginup scrolling
        Button btn = (Button)findViewById(R.id.btn_local);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupWayActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });

        btn = (Button)findViewById(R.id.btn_facebook);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupWayActivity.this, "face book",Toast.LENGTH_SHORT).show();

            }
        });

        btn = (Button)findViewById(R.id.btn_kakao);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupWayActivity.this, "kakao talk",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
