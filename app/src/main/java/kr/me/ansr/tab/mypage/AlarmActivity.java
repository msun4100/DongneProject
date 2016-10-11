package kr.me.ansr.tab.mypage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    @Bind(R.id.sw_my_like) SwitchCompat swLike;
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

        swAll.setOnCheckedChangeListener(mListener);
        swFriendsReq.setOnCheckedChangeListener(mListener);
        swChat.setOnCheckedChangeListener(mListener);
        swReply.setOnCheckedChangeListener(mListener);
        swLike.setOnCheckedChangeListener(mListener);
        swRingtone.setOnCheckedChangeListener(mListener);

        init();
    }

    public CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.sw_my_all:
                    if(isChecked){
                        PropertyManager.getInstance().setAlarmAll(1);
//                        turnOn();
//                        Log.e("chk ",""+PropertyManager.getInstance().getAlarmChat());
                    } else {
                        turnOff();
                        PropertyManager.getInstance().setAlarmAll(0);
                    }
                    break;
                case R.id.sw_my_friends_req:
                    if(isChecked){
                        swAll.setChecked(true);
                        PropertyManager.getInstance().setAlarmFriend(1);
                    } else {
                        PropertyManager.getInstance().setAlarmFriend(0);
                    }
                    break;
                case R.id.sw_my_chat:
                    if(isChecked){
                        swAll.setChecked(true);
                        PropertyManager.getInstance().setAlarmChat(1);
                    } else {
                        PropertyManager.getInstance().setAlarmChat(0);
                    }
                    break;
                case R.id.sw_my_reply:
                    if(isChecked){
                        swAll.setChecked(true);
                        PropertyManager.getInstance().setAlarmReply(1);
                    } else {
                        PropertyManager.getInstance().setAlarmReply(0);
                    }
                    break;
                case R.id.sw_my_like:
                    if(isChecked){
                        swAll.setChecked(true);
                        PropertyManager.getInstance().setAlarmLike(1);
                    } else {
                        PropertyManager.getInstance().setAlarmLike(0);
                    }
                    break;
                case R.id.sw_my_ringtone:
                    if(isChecked){
                        swAll.setChecked(true);
                        PropertyManager.getInstance().setAlarmRingtone(1);
                    } else {
                        PropertyManager.getInstance().setAlarmRingtone(0);
                    }
                    break;
            }
        }
    };

    private void turnOff(){
        swAll.setChecked(false);
        swFriendsReq.setChecked(false);
        swChat.setChecked(false);
        swReply.setChecked(false);
        swLike.setChecked(false);
        swRingtone.setChecked(false);
    }
    private void turnOn(){
        swAll.setChecked(true);
        swFriendsReq.setChecked(true);
        swChat.setChecked(true);
        swReply.setChecked(true);
        swLike.setChecked(true);
        swRingtone.setChecked(true);
    }

    private void init(){
        int alarmAll = PropertyManager.getInstance().getAlarmAll();
        if(alarmAll > 0) swAll.setChecked(true); else swAll.setChecked(false);

        int alarmFriend = PropertyManager.getInstance().getAlarmFriend();
        if(alarmFriend > 0) swFriendsReq.setChecked(true); else swFriendsReq.setChecked(false);

        int alarmChat = PropertyManager.getInstance().getAlarmChat();
        if(alarmChat > 0) swChat.setChecked(true); else swChat.setChecked(false);

        int alarmReply = PropertyManager.getInstance().getAlarmReply();
        if(alarmReply > 0) swReply.setChecked(true); else swReply.setChecked(false);

        int alarmLike = PropertyManager.getInstance().getAlarmLike();
        if(alarmLike > 0) swLike.setChecked(true); else swLike.setChecked(false);

        int alarmRingtone = PropertyManager.getInstance().getAlarmRingtone();
        if(alarmRingtone > 0) swRingtone.setChecked(true); else swRingtone.setChecked(false);

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
