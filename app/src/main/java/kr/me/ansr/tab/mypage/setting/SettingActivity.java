package kr.me.ansr.tab.mypage.setting;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kr.me.ansr.MyApplication;
import kr.me.ansr.R;
import kr.me.ansr.common.account.LogoutDialogFragment;
import kr.me.ansr.common.account.PWDialogFragment;
import kr.me.ansr.common.account.WithdrawDialogFragment;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();

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
            Intent intent;
            switch (v.getId()){
                case R.id.linear_set_lock:
                    Toast.makeText(SettingActivity.this, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.linear_set_version:
                    intent = new Intent(SettingActivity.this, OpenSourcesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                    break;
                case R.id.linear_set_ask:
                    Toast.makeText(SettingActivity.this, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.linear_set_help:
                    Toast.makeText(SettingActivity.this, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.linear_set_support:
                    Toast.makeText(SettingActivity.this, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.linear_set_cache:
//                    clearMemory() must be called on the main thread. clearDiskCache() must be called on a background thread.
//                        You can't call both at once on the same thread.
//                    Glide.get(MyApplication.getContext()).clearMemory();
//                    Glide.get(MyApplication.getContext()).clearDiskCache();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Glide.get(MyApplication.getContext()).clearMemory();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 400);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Glide.get(MyApplication.getContext()).clearDiskCache();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    Toast.makeText(SettingActivity.this, "이미지 캐시가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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

    private class ClearCacheTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... urls) {
            boolean bool = false;
            //...

            return bool;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // 파일 다운로드 퍼센티지 표시 작업
            // setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // 다 받아진 후 받은 파일 총용량 표시 작업
            // showDialog("Downloaded " + result + " bytes");
        }

        @Override
        protected void onCancelled(Boolean result) {
            super.onCancelled(result);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
