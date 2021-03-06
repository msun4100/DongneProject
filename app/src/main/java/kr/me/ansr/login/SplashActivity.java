package kr.me.ansr.login;


import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PermissionUtil;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.gcm.QuickstartPreferences;
import kr.me.ansr.gcm.RegistrationIntentService;
import kr.me.ansr.gcmchat.model.User;
import kr.me.ansr.image.upload.Config;
import okhttp3.Request;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.playlog.internal.LogEvent;

import java.io.IOException;
/**
 * Created by KMS on 2016-07-11.
 */
public class SplashActivity extends Activity {

    //for GCM
    private static final String SENDER_ID = "146117892280"; //my app key
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = SplashActivity.class.getSimpleName();
//    private Button mRegistrationButton;
//    private ProgressBar mRegistrationProgressBar;
//    private TextView mInformationTextView;
//    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }



    boolean tokenComplete = false;
    //    String token=null;   //registBroadcastReceiver()의 Complete에서 받아오는 토큰 이값이 있으면 쓰레드 부분 패스함
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        mProvider = mLM.getBestProvider(criteria, true);
        mProvider = LocationManager.NETWORK_PROVIDER;   //1116 수정
        Log.e(TAG, "onCreate: " );
    }// onCreate

    LocationManager mLM;
    String mProvider;
    //	public String latitude="37.4762397";
//	public String longitude="126.9583907";
    public String latitude = "";
    public String longitude = "";

    LocationListener mListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                    // GPS였으면 네트워크로 바꿔야됨
                    break;
                }
                case LocationProvider.AVAILABLE:
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            // 위치정보 기능 온오프 리스너
            Log.e(TAG, "onProvider Enabled: ");
        }

        @Override
        public void onProviderDisabled(String provider) {
            // 위치정보 기능 온오프 리스너
            Log.e(TAG, "onProvider Disabled: ");
        }

        @Override
        public void onLocationChanged(Location location) {
            // 수신될 위치가 넘어옴
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            PropertyManager.getInstance().setLatitude(latitude);
            PropertyManager.getInstance().setLongitude(longitude);
            Log.e(TAG, "onLocationChanged: " +location );
        }
    };
    private boolean isProviderEnabled = true;
    private boolean isFirst = true;
    private boolean isPermissionOK = true;
    @Override
    protected void onStart() {
        super.onStart();
        // 3.어떤 조건을 만족하는 프로바이더를 얻어오기 - 조건을 기술하는 클래스 == criteria
        setBoardResizePixel();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); //1116 fine에서 수정
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setCostAllowed(true);
        //type 1 for genymotion device
//        mProvider = LocationManager.GPS_PROVIDER;
//		mProvider = mLM.getBestProvider(criteria, true);
//        Toast.makeText(SplashActivity.this, "bestProvider->" + mProvider , Toast.LENGTH_SHORT).show();
        //type 2 for personal device
        mProvider = LocationManager.NETWORK_PROVIDER;
//        기존 코드는 NETWORK_PROVIDER만을 사용했었음. 지금은 지니모션과 병행하기 위해 bestProvider로 함
//        onCreate()에도 mProvider를 초기화하는 코드도 있었고... <- 이코드는 필요 없는거 같은데?
//        mProvider = LocationManager.GPS_PROVIDER;

        // 3-1비용이 발생해도 좋은지 여부 false면 절대 비용발생 true인 걸로 검색하지 않음
        // 3-2true면 켜져있는GPS,네트웤으로만 false면 모든장치에서 찾아옴
        // 3-3현재까지는 GPS와 네트웤 두가지 밖에 없기 때문에 일반적으로getBestProvider()호출안하고
        // 지정하는 방식 == LocationManager.NETWORK_PROVIDER 사용

        Log.d(TAG, "onStart: mProvider " + mProvider.toString());
        Log.d(TAG, "onStart: passive " + mProvider.equals(LocationManager.PASSIVE_PROVIDER));
        Log.d(TAG, "onStart: isProviderEnabled "+ mLM.isProviderEnabled(mProvider));
        Log.e(TAG, "onStart: usingLocation "+PropertyManager.getInstance().getUsingLocation() );
        //상단 바 위치 끄면 isProviderEnabled === false
        if (mProvider == null || mProvider.equals(LocationManager.PASSIVE_PROVIDER) || !mLM.isProviderEnabled(mProvider)) {
            if (PropertyManager.getInstance().getUsingLocation() == 2) {
                PropertyManager.getInstance().setUsingLocation(2);
            } else if(PropertyManager.getInstance().getUsingLocation() == 1){
                PropertyManager.getInstance().setUsingLocation(1);
            } else if (PropertyManager.getInstance().getUsingLocation() == 0){
                Toast.makeText(SplashActivity.this, "계속 사용하려면 위치 정보를 켜주세요. 1", Toast.LENGTH_SHORT).show();
                PropertyManager.getInstance().setUsingLocation(0);
            }
//               Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), RC_FINE_LOCATION_ON_ACTIVITY_RESULT);
//                //startActivity(intent);
//            return;
            isProviderEnabled = false;  // return 대신 이 변수를 넣어서 6.0 이상은 그냥 기존 플로우 따라가도록 하고 낮은 버전은 따로 세팅창 불러옴
        } else {
            isProviderEnabled = true;
        }
        Log.e(TAG, "onStart: isProviderEnabled == " +isProviderEnabled );
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for Marshmallow version and newer versions
            Log.e(TAG, "onStart: newer versions" );
            isPermissionOK = false; //기본값이 true임. 낮은 버전위해. updateLocation에서 requestPermission호출 후에 true로 바뀜.
            PropertyManager.getInstance().setUsingLocation(0);
            updateLocation();
        } else {
            Log.e(TAG, "onStart: below M versions" );
            isPermissionOK = true;
            //6.0 이하 버전에서는 checkSelfPermission 호출은 그냥 무시됨 return 0 반환
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    Log.d(TAG, "onStart: 1");
                }
//                Log.d(TAG, "onStart: 2");
            } else {
                //*************낮은 API 버전에서는 무조건 여기 else ****************
//                if(PropertyManager.getInstance().getUsingLocation() == 0){
                /*
                        위치 정보를 끄더라도 getLastKnownLocation 함수가 최근 좌표를 불러 오는 듯
                        일단은 usinglocation이 0이 아니면 아래 라인타서 좌표 받아오게 함.
                */
                Log.d(TAG, "onStart: 3 else...................... " + mProvider.toString() + " " + PropertyManager.getInstance().getUsingLocation());
            }
            Log.e(TAG, "onStart: isFirst: "+ isFirst );
            if(isProviderEnabled == false && isFirst == true){
                Log.e(TAG, "onStart: 4 .........." );
                Toast.makeText(SplashActivity.this, "계속 사용하려면 위치 정보를 켜주세요. 2", Toast.LENGTH_SHORT).show();
                PropertyManager.getInstance().setLatitude("");
                PropertyManager.getInstance().setLongitude("");
                isPermissionOK = false;
//                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK), RC_COARSE_LOCATION_ON_ACTIVITY_RESULT);
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), RC_COARSE_LOCATION_ON_ACTIVITY_RESULT);
                isFirst = false;
//                return;
            } else {
                Location location = mLM.getLastKnownLocation(mProvider);
                if (location != null) {
                    mListener.onLocationChanged(location);
                } else {
                    Log.d(TAG, "onStart: location is null");
                }
//    		mLM.requestLocationUpdates(mProvider, 1000*60*60, 5.0f, mListener);// 프로바이더,2000==2초,// 5미터
                mLM.requestLocationUpdates(mProvider, 2000, 5.0f, mListener);
            }

        } //else below M version

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLM.removeUpdates(mListener);
            return;
        }
        mLM.removeUpdates(mListener);
//        Log.e(TAG, "onStop: "+"==========removeUpdates===========" );

    }
    //=========위치정보 세팅===========

    boolean mIsFirst = true;
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onResumeAction();
            }
        }, 800);

        /*
        아래의 기존 코드(onResumeAction())로 시작하면 스플래시 화면이 뜨기전에 메인액티비티로 넘어가져서 스플래시 이미지가
        리쥼될때 보이지 않음
        */
//        onResumeAction();

    }
    private void onResumeAction(){
        if (checkPlayServices()) {
            String regId = PropertyManager.getInstance().getRegistrationId();
            if (!regId.equals("")) {
                if(isPermissionOK){
                    runOnUiThread(nextAction);
                }
            } else {
                registerInBackground();
            }
        } else {
            if (mIsFirst) {
                mIsFirst = false;
            } else {
                // Toast...
                Toast.makeText(SplashActivity.this, "onResumeAction else clause", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Runnable nextAction = new Runnable() {

        @Override
        public void run() {
            final String email = PropertyManager.getInstance().getEmail();
            if (!email.equals("")) {
                final String password = PropertyManager.getInstance().getPassword();
                NetworkManager.getInstance().postDongneLogin(SplashActivity.this, email, password, new NetworkManager.OnResultListener<LoginInfo>(){
                    @Override
                    public void onSuccess(Request request, LoginInfo result) {
                        if (result.error.equals(true)) {
                            Toast.makeText(SplashActivity.this, "로그인 요청이 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            PropertyManager.getInstance().setEmail(email);
                            PropertyManager.getInstance().setPassword(password);
                            PropertyManager.getInstance().setUserId(result.result.user_id);
                            PropertyManager.getInstance().setUnivId(result.result.univId);
                            PropertyManager.getInstance().setUserName(result.result.name);
                            //for chatting PropertyManager
                            User user = new User(Integer.parseInt(result.result.user_id), result.result.name, result.result.email);
                            MyApplication.getInstance().getPrefManager().storeUser(user);
//                        Toast.makeText(SplashActivity.this, TAG+ "" + result, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(SplashActivity.this, getString(R.string.res_err_msg), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: " + cause );
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } else {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
//                        Intent intent = new Intent(SplashActivity.this, SplashBActivity.class);
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        } //run()
    };

    GoogleCloudMessaging gcm;

    private void registerInBackground() {
        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
/*
                아래 주석친 기존 SDAM 코드로도 실행 되지만 최신 deprecate 안된 권장 코드로 되도록.
                    getInstanceIdToken(); 으로 하면 RegistrationIntentService에서  COMPLETE로 바꿔서 리시버가 받는 동안의
                    텀 때문에 postExcute 이후 코드가 정상 동작하기 힘듬. 그래서 직접 ID얻어오는 부분만 하드코딩
*/
//                getInstanceIdToken();

                //RegistrationIntentService.java의 ID 얻어오는 부분만 Copy & Paste
                // GCM을 위한 Instance ID를 가져온다.
                InstanceID instanceID = InstanceID.getInstance(SplashActivity.this);
                String regId = null;
                try {
                    synchronized (TAG) {
                        // GCM 앱을 등록하고 획득한 설정파일인 google-services.json을 기반으로 SenderID를 자동으로 가져온다.
                        String default_senderId = getString(R.string.gcm_defaultSenderId);
                        // GCM 기본 scope는 "GCM"이다.
                        String scope = GoogleCloudMessaging.INSTANCE_ID_SCOPE;
                        // Instance ID에 해당하는 토큰을 생성하여 가져온다.
                        regId = instanceID.getToken(default_senderId, scope, null);
                        PropertyManager.getInstance().setRegistrationId(regId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //RegistrationIntentService.java의 ID 얻어오는 부분만 Copy & Paste
                msg = regId;//msg값이 넘어가는디 postExcute에서 check

//                Log.i("After doInBack:", ""+regId);
                //아래는 기존 코드
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging
//                                .getInstance(SplashActivity.this);
//                    }
//                    String regid = gcm.register(SENDER_ID);
//					Log.i("Splash regid", ""+regid);
//                    PropertyManager.getInstance().setRegistrationId(regid);
//
//                } catch (IOException ex) {
//                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(SplashActivity.this, "onPostExcute msg:\n"+msg, Toast.LENGTH_SHORT).show();
                if(isPermissionOK){
                    runOnUiThread(nextAction);
                } else {
                    Toast.makeText(SplashActivity.this, "onPostExcute else:\n", Toast.LENGTH_SHORT).show();
                }

            }
        }.execute(null, null, null);
    }


    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.i(TAG, "This device is not supported.");
//                finish();
//            }
//            return false;
//        }
//        return true;
//    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog dialog = apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                dialog.show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void updateLocation() {
//        if (!mLM.isProviderEnabled(mProvider)) {
//            Log.e(TAG, "updateLocation: isProviderEnabled: "+ isProviderEnabled );
//            return;
//        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        isPermissionOK = true;
        PropertyManager.getInstance().setUsingLocation(1); //isPerm.이랑 2개씩 세트로 true면 1 false면 0
        Location location = mLM.getLastKnownLocation(mProvider);
        if (location != null) {
            displayLocation(location);
            mListener.onLocationChanged(location);
        }
        mLM.requestLocationUpdates(mProvider, 5000, 5, mListener);
    }
    private void displayLocation(Location location) {
        Log.d(TAG, "displayLocation: \n" + "lat : " + location.getLatitude() + " lng : " + location.getLongitude() );
    }

    private static final int RC_COARSE_LOCATION = 100;
    private static final int RC_COARSE_LOCATION_ON_ACTIVITY_RESULT = 101;
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, RC_COARSE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_COARSE_LOCATION) {
            if (permissions != null && permissions.length > 0) {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(PermissionUtil.verifyPermissions(grantResults)){
                    isPermissionOK = true;
                    PropertyManager.getInstance().setUsingLocation(1);
                    updateLocation();
                } else {
                    // 권한을 얻지 못했다. Show Rational Dialog
                    isPermissionOK = false;  //퍼미션 트루를 해야 넥스트액션을 할 수 있음
                    PropertyManager.getInstance().setUsingLocation(0);
                    Context mContext = SplashActivity.this;
                    String message = PermissionUtil.getRationalMessage(mContext, PermissionUtil.PERMISSION_LOCATION);
//                    PermissionUtil.showRationalDialog(mContext, message); //이걸로 호출하면 ForResult를 못함.
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("");
                    builder.setMessage(message);
                    builder.setCancelable(false);
                    builder.setNegativeButton(getString(R.string.word_close), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PropertyManager.getInstance().setUsingLocation(0);
                            Log.d(TAG, "onClick: Alert cancel");
                            Intent intent = new Intent(SplashActivity.this, SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                    });
                    builder.setPositiveButton(getString(R.string.word_settings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, RC_COARSE_LOCATION_ON_ACTIVITY_RESULT);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                startActivityForResult(intent, RC_COARSE_LOCATION_ON_ACTIVITY_RESULT);
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: resultCode " + resultCode + " requestCode  "+ requestCode );
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_COARSE_LOCATION_ON_ACTIVITY_RESULT:
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    if(permissionCheck== PackageManager.PERMISSION_DENIED){
                        Log.e(TAG, "onActivityResult: denied" );
                        // 권한 없음
                        isPermissionOK = false;
                        PropertyManager.getInstance().setUsingLocation(0);
                    }else{
                        // 권한 있음
                        Log.e(TAG, "onActivityResult: permission_ok" );
                        isPermissionOK = true;
                        PropertyManager.getInstance().setUsingLocation(1);
                        updateLocation();
                    }
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.e(TAG, "onActivityResult: RESULT_CANCELED" );
        }
    }

    public void setBoardResizePixel(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Config.resizeValue = width;
    }

}


