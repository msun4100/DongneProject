package kr.me.ansr.login;


import kr.me.ansr.MainActivity;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.gcm.QuickstartPreferences;
import kr.me.ansr.gcm.RegistrationIntentService;
import okhttp3.Request;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
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

import java.io.IOException;

public class SplashActivity extends Activity {

	//for GCM
    private static final String SENDER_ID = "146117892280"; //my app key
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "SplashActivity";

	private Button mRegistrationButton;
	private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
	private BroadcastReceiver mRegistrationBroadcastReceiver;


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

	/**
	 * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
	 */

	public void registBroadcastReceiver(){
		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();


				if(action.equals(QuickstartPreferences.REGISTRATION_READY)){
					// 액션이 READY일 경우
//                    Toast.makeText(SplashActivity.this, QuickstartPreferences.REGISTRATION_READY, Toast.LENGTH_SHORT).show();
					mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
					mInformationTextView.setVisibility(View.GONE);

                    tokenComplete = false;
				} else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
					// 액션이 GENERATING일 경우
//                    Toast.makeText(SplashActivity.this, QuickstartPreferences.REGISTRATION_GENERATING, Toast.LENGTH_SHORT).show();
					mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
					mInformationTextView.setVisibility(View.VISIBLE);
					mInformationTextView.setText(getString(R.string.registering_message_generating));

                    tokenComplete = false;
				} else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
					// 액션이 COMPLETE일 경우
					mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
					mRegistrationButton.setText(getString(R.string.registering_message_complete));
					mRegistrationButton.setEnabled(false);
					String token = intent.getStringExtra("token");
                    tokenComplete = true;
                    //token을 프로퍼티매니저에 저장
					mInformationTextView.setText(token);
                    Toast.makeText(SplashActivity.this, QuickstartPreferences.REGISTRATION_COMPLETE + "!!!!!\n" + token, Toast.LENGTH_SHORT).show();
				}

			}
		};
	}
    boolean tokenComplete = false;
//    String token=null;   //registBroadcastReceiver()의 Complete에서 받아오는 토큰 이값이 있으면 쓰레드 부분 패스함
	Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

        //for GCM
        registBroadcastReceiver();

        // 토큰을 보여줄 TextView를 정의
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mInformationTextView.setVisibility(View.GONE);
        // 토큰을 가져오는 동안 인디케이터를 보여줄 ProgressBar를 정의
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        // 토큰을 가져오는 Button을 정의
        mRegistrationButton = (Button) findViewById(R.id.registrationButton);
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            /**
             * 버튼을 클릭하면 토큰을 가져오는 getInstanceIdToken() 메소드를 실행한다.
             * @param view
             */
            @Override
            public void onClick(View view) {
                getInstanceIdToken();
            }
        });

	}// onCreate

    boolean mIsFirst = true;

    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

        if (checkPlayServices()) {
            String regId = PropertyManager.getInstance().getRegistrationId();
            if (!regId.equals("")) {
                runOnUiThread(nextAction);
            } else {
                registerInBackground();
            }
        } else {
            if (mIsFirst) {
                mIsFirst = false;
            } else {
                // Toast...
                Toast.makeText(SplashActivity.this, "onResume GCM else clause", Toast.LENGTH_SHORT).show();
            }
        }

    }

    Runnable nextAction = new Runnable() {

        @Override
        public void run() {
//            PropertyManager.getInstance().setUserId("Todo..");
            String userId = PropertyManager.getInstance().getUserId();
            if (!userId.equals("")) {
//                String password = PropertyManager.getInstance().getPassword();
                final String id = "user01@gmail.com";
                final String password = "1234";
                NetworkManager.getInstance().postDongneLogin(SplashActivity.this, id, password, new NetworkManager.OnResultListener<LoginInfo>(){
                    @Override
                    public void onSuccess(Request request, LoginInfo result) {
                        Toast.makeText(SplashActivity.this, "result:"+ result, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Request request, int code, Throwable cause) {
                        Toast.makeText(SplashActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
                    }
                });
//                NetworkManager.getInstance().putSdamLogin(SplashActivity.this, userId,password,
////						"37.4762397",
////						"126.9583907",
//                        PropertyManager.getInstance().getLatitude(),
//                        PropertyManager.getInstance().getLongitude(),
//                        new OnResultListener<LoginInfo>() {
//
//                            @Override
//                            public void onSuccess(LoginInfo result) {
//                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                                startActivity(intent);
////								Toast.makeText(SplashActivity.this, PropertyManager.getInstance().getRegistrationId(), Toast.LENGTH_SHORT).show();
//                                finish();
//                            }
//
//                            @Override
//                            public void onFail(int code) {
//                                Toast.makeText(SplashActivity.this, "서버에 연결할 수 없습니다. #" + code, Toast.LENGTH_LONG).show();
//                            }
//                        });
            } else {
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, SplashBActivity.class);
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
                        Log.i(TAG, "GCM Registration Token: " + regId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //RegistrationIntentService.java의 ID 얻어오는 부분만 Copy & Paste
                msg = regId;//msg값이 넘어가는디 postExcute에서 check

                Log.i("After doInBack:", ""+regId);
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
                runOnUiThread(nextAction);
            }
        }.execute(null, null, null);
    }


    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
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

}
