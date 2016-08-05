package kr.me.ansr.tab.friends.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import kr.me.ansr.R;
import kr.me.ansr.tab.friends.model.Desc;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.model.Sns;

public class FriendsDetailActivity extends AppCompatActivity {

    int reqUserId, mPosition;
    FriendsResult mItem;

    ListView listView;
    ProfileAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
//            mItem = (BoardResult)intent.getSerializableExtra(BoardInfo.BOARD_DETAIL_OBJECT);
            reqUserId = intent.getIntExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, -1);
            mPosition = intent.getIntExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, -1);
            mItem = (FriendsResult)intent.getSerializableExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM);
            if(reqUserId == -1 || mPosition == -1 || mItem == null) forcedFinish();
        } else {
            forcedFinish();
        }
        listView = (ListView)findViewById(R.id.listView_profile);
        mAdapter = new ProfileAdapter();
        listView.setAdapter(mAdapter);
//        Log.e("detail.sns:", mItem.sns.get(0).toString());
//        Log.e("detail.sns.size:", ""+mItem.sns.size());
        init();

    }

    private void init(){
        if(mItem.sns.size() != 0){
            Log.e("sns:", mItem.sns.toString());
            for(Sns s : mItem.sns){
                mAdapter.add(s);
            }
        }
        if(mItem.desc.size() != 0){
            for(String s : mItem.desc){
                Desc d = new Desc();
                d.desc = s;
                mAdapter.add(d);
            }
        }
    }
    private void forcedFinish(){
        finish();
    }
}
