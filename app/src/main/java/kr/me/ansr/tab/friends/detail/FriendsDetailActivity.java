package kr.me.ansr.tab.friends.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import kr.me.ansr.R;
import kr.me.ansr.common.InputDialogFragment;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.model.Desc;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.model.Sns;

public class FriendsDetailActivity extends AppCompatActivity {
    private static final String TAG = FriendsDetailActivity.class.getSimpleName();
    int reqUserId, mPosition;
    FriendsResult mItem;

    ListView listView;
    ProfileAdapter mAdapter;
    TextView usernameView, stuidView, deptnameView, jobnameView, jobteamView, distanceView;
    ImageView thumbIcon, firstIcon, secondIcon, thirdIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            reqUserId = intent.getIntExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, -1);
            mPosition = intent.getIntExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, -1);
            mItem = (FriendsResult)intent.getSerializableExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM);
            if(reqUserId == -1 || mPosition == -1 || mItem == null) forcedFinish();
        } else {
            forcedFinish();
        }
        usernameView = (TextView)findViewById(R.id.text_friends_detail_username);
        stuidView = (TextView)findViewById(R.id.text_friends_detail_stuid);
        deptnameView = (TextView)findViewById(R.id.text_friends_detail_deptname);
        jobnameView = (TextView)findViewById(R.id.text_friends_detail_jobname);
        jobteamView = (TextView)findViewById(R.id.text_friends_detail_jobteam);
        distanceView = (TextView)findViewById(R.id.text_friends_detail_distance);
        thumbIcon = (ImageView)findViewById(R.id.image_friends_detail_profile);

        listView = (ListView)findViewById(R.id.listView_profile);
        mAdapter = new ProfileAdapter();
        listView.setAdapter(mAdapter);

        firstIcon = (ImageView)findViewById(R.id.image_friends_detail_menu_list);
        secondIcon = (ImageView)findViewById(R.id.image_friends_detail_menu_add);
        thirdIcon =(ImageView)findViewById(R.id.image_friends_detail_menu_edit);
        firstIcon.setOnClickListener(mListener);
        secondIcon.setOnClickListener(mListener);
        thirdIcon.setOnClickListener(mListener);

        init();
    }
    public View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.image_friends_detail_menu_list:
                    Toast.makeText(FriendsDetailActivity.this, "first menu click", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.image_friends_detail_menu_add:
                    InputDialogFragment mDialogFragment = new InputDialogFragment();
                    Bundle b = new Bundle();
                    b.putString("tag", InputDialogFragment.TAG_FRIENDS_DETAIL);
                    b.putSerializable("item", mItem);
                    mDialogFragment.setArguments(b);
                    mDialogFragment.show(getSupportFragmentManager(), "customDialog");
                    break;
                case R.id.image_friends_detail_menu_edit:
                    Toast.makeText(FriendsDetailActivity.this, "third menu click", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void init(){
        if(mItem.sns.size() != 0){
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
        usernameView.setText(mItem.username);
        String stuId = String.valueOf(mItem.univ.get(0).getEnterYear());
        stuidView.setText(stuId.substring(2,4));    //2016 --> 16
        deptnameView.setText(mItem.univ.get(0).deptname);
        jobnameView.setText(mItem.job.getName());
        jobteamView.setText(mItem.job.getTeam());
        distanceView.setText(mItem.temp);
        if (!TextUtils.isEmpty(mItem.pic.small)) {
            String url = Config.FILE_GET_URL.replace(":userId", ""+mItem.userId).replace(":size", "small");
            Glide.with(getApplicationContext()).load(url)
                    .placeholder(R.drawable.ic_stub)
                    .signature(new StringSignature(mItem.getUpdatedAt()))
//                    .signature(new StringSignature(System.currentTimeMillis()/(24 * 60 * 60 * 1000)))
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(thumbIcon);
        } else {
            thumbIcon.setImageResource(R.mipmap.ic_launcher);
        }

    }
    public void nextProcess(String str){
        Toast.makeText(FriendsDetailActivity.this, "str:"+str, Toast.LENGTH_SHORT).show();
    }

    private void forcedFinish(){
        finish();
    }
}
