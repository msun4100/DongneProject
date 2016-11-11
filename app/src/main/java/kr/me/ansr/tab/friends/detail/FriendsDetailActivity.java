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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.Random;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomDialogFragment;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.common.InputDialogFragment;
import kr.me.ansr.common.ReportDialogFragment;

import kr.me.ansr.image.MediaStoreActivity;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.chat.GcmChatFragment;
import kr.me.ansr.tab.friends.list.FriendsListActivity;
import kr.me.ansr.tab.friends.model.Desc;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.model.Sns;

import okhttp3.Request;

public class FriendsDetailActivity extends AppCompatActivity implements IDataReturned {
    private static final String TAG = FriendsDetailActivity.class.getSimpleName();
    int[] bgArr = {R.drawable.z_profile_bg_1, R.drawable.z_profile_bg_2};
    String tag = null;
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
            //각 status프래그먼트에서 열었는지 대학교리스트에서 열었는지 등을 알기위해
            // tag별로 InputDialog가 열렸을때 넥스트 프로세스를 수행함.
            tag = intent.getStringExtra("tag");
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

//        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        init(); //프로필만 바꾸고 취소 버튼을 누른경우 프로필이미지 또한 변경하기 위해
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
                            Intent i = new Intent(FriendsDetailActivity.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            i.putExtra("tab", "tab2");

                            GcmChatFragment.withIntent = true;
                            GcmChatFragment.item = mItem;
                            startActivity(i);
                            break;
                        case 2:
                            break;
                        case 3:
                            CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
                            Bundle b = new Bundle();
                            b.putString("tag", CustomDialogFragment.TAG_FRIENDS_DETAIL);
                            b.putString("title","친구 차단을 해제 하시겠습니까?");
                            b.putString("body", "학교 사람들 리스트에 노출됩니다.");
                            b.putString("choice","blockOff");
                            b.putSerializable("mItem", mItem);
                            mDialogFragment.setArguments(b);
                            mDialogFragment.show(getSupportFragmentManager(), "customDialog");
                            break;
                        default: break;
                    }

                    break;
                case R.id.image_friends_detail_menu_2:
                    if(mItem.userId == Integer.valueOf(PropertyManager.getInstance().getUserId())){
                        Intent intent = new Intent(FriendsDetailActivity.this, MediaStoreActivity.class);
                        intent.putExtra("mItem", mItem);
                        intent.putExtra("tag", MediaStoreActivity.TAG_FRIENDS_DETAIL);
                        startActivityForResult(intent, 123);
                    } else {
                        Intent intent = new Intent(FriendsDetailActivity.this, FriendsListActivity.class);
                        intent.putExtra("mItem", mItem);
                        startActivityForResult(intent, 124);
                    }
                    break;
                case R.id.image_friends_detail_menu_3:
                    ReportDialogFragment mDialogFragment = ReportDialogFragment.newInstance();
                    Bundle b = new Bundle();
                    b.putSerializable("userInfo", mItem);
                    b.putString("tag", ReportDialogFragment.TAG_FRIENDS_DETAIL);
                    mDialogFragment.setArguments(b);
                    mDialogFragment.show(getSupportFragmentManager(), "reportDialog");
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
        b.putString("tag", tag);
        b.putSerializable("mStatus", sr);
        b.putSerializable("mItem", mItem);
        mDialogFragment.setArguments(b);
        mDialogFragment.show(getSupportFragmentManager(), "inputDialog");
    }
    private void init(){
        //set menu images each friends status
        //내 프로필
        if(mItem.userId == Integer.valueOf(PropertyManager.getInstance().getUserId()) ){
//            status = 100;
            firstIcon.setVisibility(View.INVISIBLE);
            secondIcon.setImageResource(R.drawable.c__icon_4);
            thirdIcon.setVisibility(View.INVISIBLE);
        } else if(mItem.status == 0 || mItem.status == -100){
//            status = mItem.status;
            firstIcon.setImageResource(R.drawable.c__icon_5);
            secondIcon.setImageResource(R.drawable.c__icon_2);
            thirdIcon.setImageResource(R.drawable.c__icon_4);
        } else if(mItem.status == 1){
//            status = mItem.status;
            firstIcon.setImageResource(R.drawable.c__icon_3);
            secondIcon.setImageResource(R.drawable.c__icon_2);
            thirdIcon.setImageResource(R.drawable.c__icon_4);
        } else if(mItem.status == -1){
//            status = mItem.status;
            firstIcon.setImageResource(R.drawable.c__icon_1);
            secondIcon.setImageResource(R.drawable.c__icon_2);
            thirdIcon.setImageResource(R.drawable.c__icon_4);
        } else { //status == 2,3
//            status = mItem.status;
            firstIcon.setImageResource(R.drawable.c__icon_6);
            secondIcon.setImageResource(R.drawable.c__icon_2);
            thirdIcon.setImageResource(R.drawable.c__icon_4);
        }
        usernameView.setText(mItem.username);
        if(mItem.status == 1){
            usernameView.setVisibility(View.VISIBLE);
        } else {
            usernameView.setVisibility(View.GONE);
        }
        mAdapter.removeAll();
        if(mItem.sns.size() != 0){
            for(Sns s : mItem.sns){
                if(s.url != null && !s.url.equals("")){
                    mAdapter.add(s);
                }
            }
        }
        if(mItem.desc.size() != 0){
            for(String s : mItem.desc){
                Desc d = new Desc();
                d.desc = s;
                mAdapter.add(d);
            }
        }

        String stuId = String.valueOf(mItem.univ.get(0).getEnterYear());
        stuidView.setText(stuId.substring(2,4));    //2016 --> 16
        deptnameView.setText(mItem.univ.get(0).deptname);
        jobnameView.setText(mItem.job.getName());
        jobteamView.setText(mItem.job.getTeam());
        if(!mItem.temp.equals("")){
            distanceView.setText(mItem.temp);
        } else { distanceView.setText("0m");}

        String url = Config.FILE_GET_URL.replace(":userId", ""+mItem.userId).replace(":size", "large");
        Glide.with(getApplicationContext()).load(url)
                .placeholder(R.drawable.e__who_icon)
                .centerCrop()
//                .override(348,348)
                .signature(new StringSignature(mItem.getUpdatedAt()))
                .into(thumbIcon);

    }
    public void nextProcess(String msg){
        if(msg.equals("_cancel_")){
            if(tag.equals(InputDialogFragment.TAG_STATUS_RECEIVE) || tag.equals(InputDialogFragment.TAG_STATUS_SEND) || tag.equals(InputDialogFragment.TAG_STATUS_BLOCK)) {
                //이미지만 변경, 호출 프로세스는 해당 프래그먼트에서 static함수로 처리
                //새로 고침시 리스트 갱신은 onResume때마다 리스트 불러오도록 원시적으로 처리
                firstIcon.setImageResource(R.drawable.c__icon_1);
                mItem.status = -1;  //이건 재차 버튼이 또 눌릴경우 그에 맞는 상태 호출을 위해
                tag = InputDialogFragment.TAG_FRIENDS_DETAIL;   //태그가 send면 재차 눌렸을때 계속 리무브 프로세스만 진행됨. 아 코드 너무 더러워 지네
            } else {
                removeStatus(mItem.userId);
            }
        } else if(msg.equals("_ok_")) {
            if(tag.equals(InputDialogFragment.TAG_STATUS_RECEIVE) || tag.equals(InputDialogFragment.TAG_STATUS_SEND) || tag.equals(InputDialogFragment.TAG_STATUS_BLOCK)){
                //단순 이미지만 변경
                firstIcon.setImageResource(R.drawable.c__icon_3);
                mItem.status = 1;  //이건 재차 버튼이 또 눌릴경우 그에 맞는 상태 호출을 위해
                tag = InputDialogFragment.TAG_FRIENDS_DETAIL;
            }else {
                updateStatus(StatusInfo.STATUS_ACCEPTED, mItem.userId, msg);
            }
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
                                case 3:
                                    firstIcon.setImageResource(R.drawable.c__icon_6);
                                    break;
                                case 1:
                                    firstIcon.setImageResource(R.drawable.c__icon_3);
                                    break;
                                case 0:
                                    firstIcon.setImageResource(R.drawable.c__icon_5);
                                    break;
                            }
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
                            firstIcon.setImageResource(R.drawable.c__icon_1);
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

    public void reportUser(int type){
        if(mItem == null){
            return;
        }
        final int to = mItem.userId;
        String msg = "friends";
        NetworkManager.getInstance().postDongneReportUpdate(MyApplication.getContext(),
                type, //reportType
                to, //to == 선택된 아이템의 userId
                msg, //블락하는데 메세지는 상관없음.
                new NetworkManager.OnResultListener<StatusInfo>() {
                    @Override
                    public void onSuccess(Request request, StatusInfo result) {
                        if (result.error.equals(false)) {
                            Toast.makeText(MyApplication.getContext(), ""+result.message, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            updateStatus(3, to, "reported");    //신고처리 성공시 차단친구로 변경
                        } else {
                            Toast.makeText(MyApplication.getContext(), "error: true\n"+result.message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(MyApplication.getContext(), "onFailure: "+cause, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        dialog = new ProgressDialog(FriendsDetailActivity.this);
        dialog.setTitle("신고 요청 중..");
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


//    호출 순서 onActivityResult --> onResume
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                if (resultCode == RESULT_OK) {
//                    EventBus.getInstance().post(new FriendsFragmentResultEvent(requestCode, resultCode, data));
                    Bundle extraBundle = data.getExtras();
                    FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
                    if(result != null){
                        mItem = result;
//                        init();   //onResume에서 호출
                    }
                }
                if (resultCode == RESULT_CANCELED) {
                    Bundle extraBundle = data.getExtras();
                    FriendsResult result = (FriendsResult)extraBundle.getSerializable("mItem");
                    if(result != null){
                        mItem = result;
                        mItem.updatedAt = MyApplication.getInstance().getCurrentTimeStampString();
//                        init();
                    }
                }
                break;
        }

    }

    @Override
    public void onDataReturned(String choice) {
        //Use the returned value
        if(choice != null){
            Log.e("choice", choice);
            switch (choice){
                case "0":
                case "1":
                case "2":
                case "3":
                    reportUser(Integer.parseInt(choice));
                    break;
                case "block":
                    updateStatus(3, mItem.userId, "");
                    break;
                case "blockOff":
                    removeStatus(mItem.userId);
                    break;
                case "cutOff":
                    removeStatus(mItem.userId);
                    break;
            }
        }
    }
}
