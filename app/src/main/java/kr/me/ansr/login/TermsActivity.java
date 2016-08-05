package kr.me.ansr.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import kr.me.ansr.R;
/**
 * Created by KMS on 2016-07-11.
 */
public class TermsActivity extends AppCompatActivity {

    CheckBox checkBox1, checkBox2;
    boolean isTermBoxChecked = false;
    boolean isPrivacyBoxChecked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("약관 동의");

        checkBox1 = (CheckBox)findViewById(R.id.check_term1);
        checkBox1.setChecked(false);
        isTermBoxChecked=checkBox1.isChecked();
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isTermBoxChecked=checkBox1.isChecked();
            }
        });
        checkBox2 = (CheckBox)findViewById(R.id.check_term2);
        checkBox2.setChecked(false);
        isPrivacyBoxChecked=checkBox2.isChecked();
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPrivacyBoxChecked=checkBox2.isChecked();
            }
        });

        Button btn = (Button)findViewById(R.id.btn_terms_all);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox1.setChecked(true);
                checkBox2.setChecked(true);
            }
        });
        btn = (Button)findViewById(R.id.btn_terms_confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTermBoxChecked == true && isPrivacyBoxChecked == true){
                    Intent intent = new Intent(TermsActivity.this, SignUpAccountActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(TermsActivity.this, "이용약관 동의를 해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
