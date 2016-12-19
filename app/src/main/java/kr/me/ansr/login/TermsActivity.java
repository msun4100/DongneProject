package kr.me.ansr.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kr.me.ansr.MyApplication;
import kr.me.ansr.R;
/**
 * Created by KMS on 2016-07-11.
 */
public class TermsActivity extends AppCompatActivity {

    CheckBox checkBox1, checkBox2, checkBox3;
    boolean isTermBoxChecked = false;
    boolean isPrivacyBoxChecked = false;
    boolean isLocationBoxChecked = false;

    Button btn1, btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        toolbar.setBackgroundResource(R.drawable.a_join_agree_titlebar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        checkBox1 = (CheckBox)findViewById(R.id.check_term1);
        checkBox1.setChecked(false);
        isTermBoxChecked=checkBox1.isChecked();
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isTermBoxChecked=checkBox1.isChecked();
                if(!isTermBoxChecked){
                    btn1.setBackgroundResource(R.drawable.a_join_agree_icon_1);
                    btn2.setBackgroundResource(R.drawable.a_join_confirm_icon_1);
                }
                if(isTermBoxChecked && isPrivacyBoxChecked && isLocationBoxChecked){
                    btn1.setBackgroundResource(R.drawable.a_join_agree_icon_2);
                    btn2.setBackgroundResource(R.drawable.a_join_confirm_btn_selector);
                }
            }
        });
        checkBox2 = (CheckBox)findViewById(R.id.check_term2);
        checkBox2.setChecked(false);
        isPrivacyBoxChecked=checkBox2.isChecked();
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isPrivacyBoxChecked=checkBox2.isChecked();
                if(!isPrivacyBoxChecked){
                    btn1.setBackgroundResource(R.drawable.a_join_agree_icon_1);
                    btn2.setBackgroundResource(R.drawable.a_join_confirm_icon_1);
                }
                if(isTermBoxChecked && isPrivacyBoxChecked && isLocationBoxChecked){
                    btn1.setBackgroundResource(R.drawable.a_join_agree_icon_2);
                    btn2.setBackgroundResource(R.drawable.a_join_confirm_btn_selector);
                }
            }
        });
        checkBox3 = (CheckBox)findViewById(R.id.check_term3);
        checkBox3.setChecked(false);
        isLocationBoxChecked=checkBox3.isChecked();
        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLocationBoxChecked=checkBox3.isChecked();
                if(!isLocationBoxChecked){
                    btn1.setBackgroundResource(R.drawable.a_join_agree_icon_1);
                    btn2.setBackgroundResource(R.drawable.a_join_confirm_icon_1);
                }
                if(isTermBoxChecked && isPrivacyBoxChecked && isLocationBoxChecked){
                    btn1.setBackgroundResource(R.drawable.a_join_agree_icon_2);
                    btn2.setBackgroundResource(R.drawable.a_join_confirm_btn_selector);
                }
            }
        });

        btn1 = (Button)findViewById(R.id.btn_terms_all);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox1.setChecked(true);
                checkBox2.setChecked(true);
                checkBox3.setChecked(true);
                btn1.setBackgroundResource(R.drawable.a_join_agree_icon_2);
            }
        });
        btn2 = (Button)findViewById(R.id.btn_terms_confirm);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTermBoxChecked == true && isPrivacyBoxChecked == true && isLocationBoxChecked == true){
                    Intent intent = new Intent(TermsActivity.this, SignUpAccountActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(TermsActivity.this, "이용약관 동의를 해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        gotoSplash();
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            gotoSplash();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoSplash(){
        Intent intent = new Intent(TermsActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
