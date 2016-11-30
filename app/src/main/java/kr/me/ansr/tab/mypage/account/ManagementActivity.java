package kr.me.ansr.tab.mypage.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CommonInfo;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.common.IPWReturned;
import kr.me.ansr.common.account.LogoutDialogFragment;
import kr.me.ansr.common.account.PWDialogFragment;
import kr.me.ansr.common.account.WithdrawDialogFragment;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.login.SplashActivity;
import okhttp3.Request;

public class ManagementActivity extends AppCompatActivity implements IDataReturned, IPWReturned {

    TextView toolbarTitle;
    LinearLayout pwView, logoutView, withdrawView;
    ImageView thumbIcon;
    TextView nameView, emailView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        toolbar.setBackgroundResource(R.drawable.z_titlebar_my);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);	//6.0이상 음영효과 제거
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("");

        pwView = (LinearLayout)findViewById(R.id.linear_manage_pw);
        logoutView = (LinearLayout)findViewById(R.id.linear_manage_logout);
        withdrawView = (LinearLayout)findViewById(R.id.linear_manage_withdraw);
        pwView.setOnClickListener(mListener);
        logoutView.setOnClickListener(mListener);
        withdrawView.setOnClickListener(mListener);

        thumbIcon = (ImageView)findViewById(R.id.image_manage_thumb);
        nameView = (TextView)findViewById(R.id.text_manage_username);
        emailView = (TextView)findViewById(R.id.text_manage_email);
        initData();

        Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void initData(){
        String username = PropertyManager.getInstance().getUserName();
        if(username != null && !username.equals("")){
            nameView.setText(username);
        } else {
            nameView.setText("유저명");
        }
        String email = PropertyManager.getInstance().getEmail();
        if(email != null && !email.equals("")){
            emailView.setText(email);
        } else {
            emailView.setText("이메일");
        }
        String userId = PropertyManager.getInstance().getUserId();
        String url = Config.FILE_GET_URL.replace(":userId", "" + userId).replace(":size", "small");
        Glide.with(this).load(url).placeholder(R.drawable.e__who_icon)
                .centerCrop()
                .signature(new StringSignature(MyApplication.getInstance().getCurrentTimeStampString()))
                .into(thumbIcon);
    }
    private void logout(){
        NetworkManager.getInstance().getDongneLogout(ManagementActivity.this, new NetworkManager.OnResultListener<CommonInfo>() {
            @Override
            public void onSuccess(Request request, CommonInfo result) {
                if (result.error.equals(true)) {
                    Toast.makeText(ManagementActivity.this, "result.error:" + result.message, Toast.LENGTH_SHORT).show();
                } else {
                    PropertyManager.getInstance().clearProperties();
                    Intent intent = new Intent(ManagementActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(ManagementActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void withdraw(String withdraw){
        Log.e("withdraw", withdraw);
        Toast.makeText(ManagementActivity.this, "신고사유:" + withdraw, Toast.LENGTH_SHORT).show();
    }
    private void changePW(String pw){
        if(pw == null) return;
        Log.e("pw", pw);
        String email = PropertyManager.getInstance().getEmail();
        String password = PropertyManager.getInstance().getPassword();
        String value = pw;
        NetworkManager.getInstance().putDongneChangePW(ManagementActivity.this, email, password, value, new NetworkManager.OnResultListener<CommonInfo>() {
            @Override
            public void onSuccess(Request request, CommonInfo result) {
                if (result.error.equals(true)) {
                    Toast.makeText(ManagementActivity.this, "result.error:" + result.message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagementActivity.this, "비밀번호가 성공적으로 변경되어 로그아웃 됩니다.", Toast.LENGTH_LONG).show();
                    PropertyManager.getInstance().clearProperties();
                    Intent intent = new Intent(ManagementActivity.this, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(ManagementActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.linear_manage_pw:
                    PWDialogFragment pDialogFragment = new PWDialogFragment();  //자주 안쓰니까 newInstance 안함.
                    Bundle pb = new Bundle();
                    pb.putString("tag", PWDialogFragment.TAG_PW_CHANGE);
//                    pb.putString("email", PWDialogFragment.TAG_PW_CHANGE);
                    pDialogFragment.setArguments(pb);
                    pDialogFragment.show(getSupportFragmentManager(), "pwDialog");
                    break;
                case R.id.linear_manage_logout:
                    LogoutDialogFragment mDialogFragment = new LogoutDialogFragment();  //자주 안쓰니까 newInstance 안함.
                    Bundle b = new Bundle();
                    b.putString("tag", LogoutDialogFragment.TAG_LOGOUT);
                    b.putString("title","로그아웃 하시겠습니까?");
                    b.putString("body", "");
                    mDialogFragment.setArguments(b);
                    mDialogFragment.show(getSupportFragmentManager(), "logoutDialog");
                    break;
                case R.id.linear_manage_withdraw:
                    WithdrawDialogFragment wDialogFragment = new WithdrawDialogFragment();
                    Bundle wb = new Bundle();
                    wb.putString("tag", WithdrawDialogFragment.TAG_WITHDRAW);
                    wDialogFragment.setArguments(wb);
                    wDialogFragment.show(getSupportFragmentManager(), "withdrawDialog");
                    break;
            }
        }
    };
    String withdrawNum = null;
    @Override
    public void onDataReturned(String choice) {
        if(choice != null){
            switch (choice){
                case "0":
                case "1":
                case "2":
                case "3":
                    Log.e("choice", choice);
                    LogoutDialogFragment mDialogFragment = new LogoutDialogFragment();  //자주 안쓰니까 newInstance 안함.
                    Bundle b = new Bundle();
                    b.putString("tag", LogoutDialogFragment.TAG_WITHDRAW);
                    b.putString("title","정말 탈퇴 하시겠습니까?");
                    b.putString("body", "회원가입 정보와 일촌, 내가 쓴 글 등\n모든 데이터가 삭제되며 복구할 수 없습니다.");
                    mDialogFragment.setArguments(b);
                    mDialogFragment.show(getSupportFragmentManager(), "logoutDialog");
                    //리턴값이 _WITHDRAW_
                    withdrawNum = choice;
                    break;
                case "_LOGOUT_":
                    logout();
                    break;
                case "_WITHDRAW_":
                    if(withdrawView != null){
                        withdraw(withdrawNum);
                    }
                    break;

            }
        }
    }

    @Override
    public void onPWReturned(String value) {
        changePW(value);
    }

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
