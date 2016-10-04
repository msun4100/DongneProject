package kr.me.ansr.tab.friends.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.Random;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.InputDialogFragment;
import kr.me.ansr.image.MediaStoreActivity;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.model.Desc;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.model.Sns;
import kr.me.ansr.tab.friends.set.ProfileSettingActivity;
import okhttp3.Request;

public class FriendsDetailActivity extends AppCompatActivity {
    private static final String TAG = FriendsDetailActivity.class.getSimpleName();
    int[] bgArr = {R.drawable.z_profile_bg_1, R.drawable.z_profile_bg_2};

    int reqUserId, mPosition;
    FriendsResult mItem;

    ListView listView;
    ProfileAdapter mAdapter;
    TextView usernameView, stuidView, deptnameView, jobnameView, jobteamView, distanceView;
    ImageView background, thumbIcon, firstIcon, secondIcon, thirdIcon, backIcon;

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
            Log.e("mPosition: ", ""+mPosition);
            Log.e("mItem:", mItem.toString());
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

        listView = (ListView)findViewById(R.id.listView_profile);
        mAdapter = new ProfileAdapter();
        listView.setAdapter(mAdapter);

        background = (ImageView)findViewById(R.id.image_friends_detail_main);
        background.setImageResource( bgArr[new Random().nextInt(2)] );
        thumbIcon = (ImageView)findViewById(R.id.image_friends_detail_profile);
        backIcon = (ImageView)findViewById(R.id.image_friends_detail_back_icon);

        firstIcon = (ImageView)findViewById(R.id.image_friends_detail_menu_1);
        secondIcon = (ImageView)findViewById(R.id.image_friends_detail_menu_2);
        thirdIcon =(ImageView)findViewById(R.id.image_friends_detail_menu_3);
        firstIcon.setOnClickListener(mListener);
        secondIcon.setOnClickListener(mListener);
        thirdIcon.setOnClickListener(mListener);
        backIcon.setOnClickListener(mListener);

        init();
    }
    public View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.image_friends_detail_menu_1:
                    switch (mItem.status){

                        case -100:
                        case 0:
                        case 100:
                            getStatus();
                            break;
                        case -1:
                            //아무 관계도 아님
                            int userId =Integer.parseInt(PropertyManager.getInstance().getUserId());
                            StatusResult s = new StatusResult(
                                    userId, //from
                                    mItem.userId, //to
                                    -1, //status
                                    userId, //actionUser
                                    "", //updatedAt
                                    ""  //msg
                            );
                            showDialog(s);
                            break;
                        case 1:
                            Toast.makeText(FriendsDetailActivity.this, "1:1대화", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                        case 3:
                        default:
                            Toast.makeText(FriendsDetailActivity.this, "차단 해제\nstatus:"+mItem.status, Toast.LENGTH_SHORT).show();
                            break;
                    }

                    break;
                case R.id.image_friends_detail_menu_2:
                    switch (mItem.status){
                        case 100:
                            break;
                        default:

                            if(mItem.userId == Integer.valueOf(PropertyManager.getInstance().getUserId())){
//                                Intent intent = new Intent(FriendsDetailActivity.this, ProfileSettingActivity.class);
                                Intent intent = new Intent(FriendsDetailActivity.this, MediaStoreActivity.class);
                                intent.putExtra("mItem", mItem);
                                startActivityForResult(intent, 123);
                            }
                            Toast.makeText(FriendsDetailActivity.this, "show friends list", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case R.id.image_friends_detail_menu_3:
                    Toast.makeText(FriendsDetailActivity.this, "third menu click", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FriendsDetailActivity.this, MediaStoreActivity.class);
//                    Intent intent = new Intent(FriendsDetailActivity.this, ProfileSettingActivity.class);
//                    intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, data);
//                    intent.putExtra(FriendsInfo.FRIENDS_DETAIL_USER_ID, data.userId);
//                    intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, position);
                    intent.putExtra("mItem", mItem);
                    startActivityForResult(intent, 123); //tabHost가 있는 FriendsFragment에서 리절트를 받음
                    break;
                case R.id.image_friends_detail_back_icon:
                    finishAndReturnData();
                    break;
                default:
                    break;
            }
        }
    };

    private void showDialog(StatusResult sr){
        InputDialogFragment mDialogFragment = InputDialogFragment.newInstance();
        Bundle b = new Bundle();
        b.putString("tag", InputDialogFragment.TAG_FRIENDS_DETAIL);
        b.putSerializable("mStatus", sr);
        b.putSerializable("item", mItem);
        mDialogFragment.setArguments(b);
        mDialogFragment.show(getSupportFragmentManager(), "inputDialog");
    }
    private void init(){
        //set menu images each friends status
        //내 프로필
        if(mItem.userId == Integer.valueOf(PropertyManager.getInstance().getUserId()) ){
//            status = 100;
            firstIcon.setVisibility(View.INVISIBLE);
            secondIcon.setImageResource(R.drawable.c__icon_3);
            thirdIcon.setVisibility(View.INVISIBLE);
        } else if(mItem.status == 0 || mItem.status == -100){
//            status = mItem.status;
            firstIcon.setImageResource(R.drawable.c__icon_5);
            secondIcon.setImageResource(R.drawable.c__icon_2);
            thirdIcon.setImageResource(R.drawable.c__icon_3);
        } else if(mItem.status == 1){
//            status = mItem.status;
            firstIcon.setImageResource(R.drawable.c__icon_1);
            secondIcon.setImageResource(R.drawable.c__icon_2);
            thirdIcon.setImageResource(R.drawable.c__icon_3);
        } else if(mItem.status == -1){
//            status = mItem.status;
            firstIcon.setImageResource(R.drawable.c__icon_4);
            secondIcon.setImageResource(R.drawable.c__icon_2);
            thirdIcon.setImageResource(R.drawable.c__icon_3);
        } else { //status == 2,3
//            status = mItem.status;
            firstIcon.setImageResource(R.mipmap.ic_launcher);
            secondIcon.setImageResource(R.drawable.c__icon_2);
            thirdIcon.setImageResource(R.drawable.c__icon_3);
        }

        if(mItem.isFriend){

        }
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
        if(!mItem.temp.equals("")){
            distanceView.setText(mItem.temp);
        } else { distanceView.setText("0m");}

        if (!TextUtils.isEmpty(mItem.pic.small)) {
            String url = Config.FILE_GET_URL.replace(":userId", ""+mItem.userId).replace(":size", "small");
            Glide.with(getApplicationContext()).load(url)
                    .placeholder(R.drawable.e__who_icon)
//                    .fitCenter()
                    .centerCrop()
                    .signature(new StringSignature(mItem.getUpdatedAt()))
//                    .signature(new StringSignature(System.currentTimeMillis()/(24 * 60 * 60 * 1000)))
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(thumbIcon);
        } else {
            thumbIcon.setImageResource(R.drawable.e__who_icon);
        }

    }
    public void nextProcess(String msg){
//        Toast.makeText(FriendsDetailActivity.this, "msg:"+msg, Toast.LENGTH_SHORT).show();
        if(msg.equals("_cancel_")){
            removeStatus(mItem.userId);
        } else if(msg.equals("_ok_")) {
            updateStatus(StatusInfo.STATUS_ACCEPTED, mItem.userId, msg);
        } else {
            updateStatus(StatusInfo.STATUS_PENDING, mItem.userId, msg);
        }
    }
    ProgressDialog dialog = null;

    private void getStatus(){
        NetworkManager.getInstance().getDongneFriendsStatusUserId(getApplicationContext(),
                //userId와의 관계 및 msg 가져오기
                mItem.userId, //userId
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        if (result.error.equals(false)) {
                            StatusResult mStatus = result.result;
                            mItem.status = mStatus.status;
                            showDialog(mStatus);
                        } else {
                            Toast.makeText(getApplicationContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(FriendsDetailActivity.this);
        dialog.setTitle("요청 상태를 불러오는 중...");
        dialog.show();
    }

    private void updateStatus(int status, int to, String msg){
        NetworkManager.getInstance().postDongneFriendsUpdate(getApplicationContext(),
                status, //0
                to, //to
                msg,
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        Log.e("updateStatus:", result.message);
                        if (result.error.equals(false)) {
                            mItem.status = result.result.status;
                            //.. imageButton1 이미지 변경
                            switch (mItem.status){
                                case 1:
                                    firstIcon.setImageResource(R.drawable.c__icon_1);
                                    break;
                                case 0:
                                    firstIcon.setImageResource(R.drawable.c__icon_5);
                                    break;
                            }
//                            Toast.makeText(getApplicationContext(), "친구 요청이 완료되었습니다."+"\n"+result.message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
                            Log.e(TAG+" error: true", result.message);
                        }
                        dialog.dismiss();
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(FriendsDetailActivity.this);
        dialog.setTitle("친구 요청 중입니다...");
        dialog.show();
    }

    public void removeStatus(int userId){
        NetworkManager.getInstance().postDongneFriendsRemove(MyApplication.getContext(),
                userId,
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        Log.e("removeStatus:", result.message);
                        if (result.error.equals(false)) {
//                            Toast.makeText(FriendsDetailActivity.this, "", Toast.LENGTH_LONG).show();
                            mItem.status = -1;
                            firstIcon.setImageResource(R.drawable.c__icon_4);
                            //.. imageButton1 이미지 변경
                        } else {
                            Toast.makeText(FriendsDetailActivity.this, "error: true\n"+result.message, Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(MyApplication.getContext(), "onFailure\n"+cause, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(FriendsDetailActivity.this);
        dialog.setTitle("서버 요청 중..");
        dialog.show();
    }

    private void finishAndReturnData(){
        Intent intent = new Intent();
        intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_ITEM, mItem);
        intent.putExtra(FriendsInfo.FRIENDS_DETAIL_MODIFIED_POSITION, mPosition);
        setResult(RESULT_OK, intent);//RESULT_OK를 FriendsFragment의 온리절트에서 받음
        finish();
    }

    private void forcedFinish(){
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finishAndReturnData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishAndReturnData();
    }
}
