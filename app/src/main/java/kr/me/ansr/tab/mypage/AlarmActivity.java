package kr.me.ansr.tab.mypage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;

public class AlarmActivity extends AppCompatActivity {

    @Bind(R.id.sw_my_all) SwitchCompat swAll;
    @Bind(R.id.sw_my_friends_req) SwitchCompat swFriendsReq;
    @Bind(R.id.sw_my_chat) SwitchCompat swChat;
    @Bind(R.id.sw_my_reply) SwitchCompat swReply;
    @Bind(R.id.sw_my_ringtone) SwitchCompat swRingtone;

    public TextView toolbarTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        toolbar.setBackgroundResource(R.drawable.z_titlebar_alarm);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);	//6.0이상 음영효과 제거
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");

        //init method 만들어
        int alarmAll = PropertyManager.getInstance().getAlarmAll();
        if(alarmAll > 0) swAll.setChecked(true); else swAll.setChecked(false);

//        swAll.setThumbResource(R.drawable.z_sw_custom_selector);
//        swAll.setTrackResource(R.drawable.z_sw_custom_selector);
//        switchView.setTextOn(context.getString(R.string.common_yes));
//        switchView.setTextOff(context.getString(R.string.common_no));
//        switchView.setShowText(true);
//        switchView.setThumbResource(R.drawable.btn_switch_selector);
//        switchView.setTrackResource(R.drawable.btn_switch_bg_selector);

        swAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //propertymanager에 저장.
                    PropertyManager.getInstance().setAlarmAll(1);
                } else {
                    PropertyManager.getInstance().setAlarmAll(0);
                }
                Toast.makeText(AlarmActivity.this, "alarmAll property:"+ PropertyManager.getInstance().getAlarmAll(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            finish();
//            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        }
        return super.onOptionsItemSelected(item);
    }
}
