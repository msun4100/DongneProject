package kr.me.ansr.tab.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import kr.me.ansr.R;
import kr.me.ansr.common.event.EventBus;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.mypage.status.BlockFragment;
import kr.me.ansr.tab.mypage.status.InviteFragment;
import kr.me.ansr.tab.mypage.status.ReceiveFragment;
import kr.me.ansr.tab.mypage.status.SendFragment;

public class FriendStatusActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String F1_TAG = "receiveTab";
    private static final String F2_TAG = "sendTab";
    private static final String F3_TAG = "blockTab";
    private static final String F4_TAG = "inviteTab";
    ImageView blockIcon;
    ImageView sendIcon;
    ImageView inviteIcon;
    ImageView receiveIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        toolbar.setBackgroundResource(R.drawable.z_titlebar_friend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        receiveIcon = (ImageView)findViewById(R.id.image_my_receive);
        sendIcon = (ImageView)findViewById(R.id.image_my_send);
        blockIcon = (ImageView)findViewById(R.id.image_my_block);
        inviteIcon = (ImageView)findViewById(R.id.image_my_invite);
        receiveIcon.setOnClickListener(this);
        sendIcon.setOnClickListener(this);
        blockIcon.setOnClickListener(this);
        inviteIcon.setOnClickListener(this);

        EventBus.getInstance().register(this);
        init();
    }

    private void init(){
        Bundle bundle = new Bundle();
        Fragment f = getSupportFragmentManager().findFragmentByTag(F1_TAG);
        if (f == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ReceiveFragment newFragment = ReceiveFragment.newInstance();
            newFragment.setArguments(bundle);
            ft.replace(R.id.containerforstatus, newFragment, F1_TAG);
            ft.commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty_main, menu);
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

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Fragment f;
        switch (v.getId()){
            case R.id.image_my_receive:
                receiveIcon.setImageResource(R.drawable.f_icon_receive_2);
                sendIcon.setImageResource(R.drawable.f_icon_send_1);
                blockIcon.setImageResource(R.drawable.f_icon_block_1);
                inviteIcon.setImageResource(R.drawable.f_icon_invite_1);
                f = getSupportFragmentManager().findFragmentByTag(F1_TAG);
                if (f == null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ReceiveFragment newFragment = ReceiveFragment.newInstance();
                    newFragment.setArguments(bundle);
                    ft.replace(R.id.containerforstatus, newFragment, F1_TAG);
                    ft.commit();
                }
                break;
            case R.id.image_my_send:
                receiveIcon.setImageResource(R.drawable.f_icon_receive_1);
                sendIcon.setImageResource(R.drawable.f_icon_send_2);
                blockIcon.setImageResource(R.drawable.f_icon_block_1);
                inviteIcon.setImageResource(R.drawable.f_icon_invite_1);

                f = getSupportFragmentManager().findFragmentByTag(F2_TAG);
                if (f == null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    SendFragment newFragment = SendFragment.newInstance();
                    newFragment.setArguments(bundle);
                    ft.replace(R.id.containerforstatus, newFragment, F2_TAG);
                    ft.commit();
                }
                break;
            case R.id.image_my_block:
                receiveIcon.setImageResource(R.drawable.f_icon_receive_1);
                sendIcon.setImageResource(R.drawable.f_icon_send_1);
                blockIcon.setImageResource(R.drawable.f_icon_block_2);
                inviteIcon.setImageResource(R.drawable.f_icon_invite_1);

                f = getSupportFragmentManager().findFragmentByTag(F3_TAG);
                if (f == null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    BlockFragment newFragment = BlockFragment.newInstance();
                    newFragment.setArguments(bundle);
                    ft.replace(R.id.containerforstatus, newFragment, F3_TAG);
                    ft.commit();
                }
                break;
            case R.id.image_my_invite:
                receiveIcon.setImageResource(R.drawable.f_icon_receive_1);
                sendIcon.setImageResource(R.drawable.f_icon_send_1);
                blockIcon.setImageResource(R.drawable.f_icon_block_1);
                inviteIcon.setImageResource(R.drawable.f_icon_invite_2);
//                bundle.putString("filePath", "");
                f = getSupportFragmentManager().findFragmentByTag(F4_TAG);
                if (f == null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    InviteFragment newFragment = InviteFragment.newInstance();
                    newFragment.setArguments(bundle);
                    ft.replace(R.id.containerforstatus, newFragment, F4_TAG);
                    ft.commit();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ReceiveFragment.FRIENDS_RC_NUM:
                if (resultCode == RESULT_OK) {
//					EventBus.getInstance().post(new FriendsFragmentResultEvent(requestCode, resultCode, data));
//					sectionFragment말고 모든 FriendsResult를 사용하는 아답터들이 알아야 하니까 굳이 위처럼하지말고 여기 스텝에서 포스트 이벤트를 뿌림.
                    Bundle extraBundle = data.getExtras();
                    FriendsResult result = (FriendsResult)extraBundle.getSerializable(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM);
                    if(result != null) {
                        Log.e("FriendsStatusActivity", result.toString());
                        EventBus.getInstance().post(result);
                    }

                }
                break;
        }

    }
}
