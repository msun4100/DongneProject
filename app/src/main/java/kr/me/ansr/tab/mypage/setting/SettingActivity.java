package kr.me.ansr.tab.mypage.setting;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kr.me.ansr.MyApplication;
import kr.me.ansr.R;
import kr.me.ansr.common.account.LogoutDialogFragment;
import kr.me.ansr.common.account.PWDialogFragment;
import kr.me.ansr.common.account.WithdrawDialogFragment;

public class SettingActivity extends AppCompatActivity {

    TextView toolbarTitle;
    LinearLayout lockView, versionView, askView, helpView, supView, cacheView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        toolbar.setBackgroundResource(R.drawable.z_titlebar_setting);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);	//6.0이상 음영효과 제거
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");

        lockView = (LinearLayout)findViewById(R.id.linear_set_lock);
        versionView = (LinearLayout)findViewById(R.id.linear_set_version);
        askView = (LinearLayout)findViewById(R.id.linear_set_ask);
        helpView = (LinearLayout)findViewById(R.id.linear_set_help);
        supView = (LinearLayout)findViewById(R.id.linear_set_support);
        cacheView = (LinearLayout)findViewById(R.id.linear_set_cache);
        lockView.setOnClickListener(mListener);
        versionView.setOnClickListener(mListener);
        askView.setOnClickListener(mListener);
        helpView.setOnClickListener(mListener);
        supView.setOnClickListener(mListener);
        cacheView.setOnClickListener(mListener);

        Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.linear_set_lock:
                    break;
                case R.id.linear_set_version:
                    break;
                case R.id.linear_set_ask:
                    break;
                case R.id.linear_set_help:
                    break;
                case R.id.linear_set_support:
                    break;
                case R.id.linear_set_cache:
                    break;
            }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
}
