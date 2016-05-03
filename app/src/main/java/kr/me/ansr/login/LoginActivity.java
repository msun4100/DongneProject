package kr.me.ansr.login;

import kr.me.ansr.MainActivity;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.NetworkManager.OnResultListener;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.gcm.QuickstartPreferences;
import kr.me.ansr.R;
import kr.me.ansr.gcm.RegistrationIntentService;
import okhttp3.Request;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginActivity extends Activity {

	//for GCM
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = "LoginActivity";

	private Button mRegistrationButton;
	private ProgressBar mRegistrationProgressBar;
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	private TextView mInformationTextView;

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
					mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
					mInformationTextView.setVisibility(View.GONE);
				} else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){
					// 액션이 GENERATING일 경우
					mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
					mInformationTextView.setVisibility(View.VISIBLE);
					mInformationTextView.setText(getString(R.string.registering_message_generating));
				} else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
					// 액션이 COMPLETE일 경우
					mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
					mRegistrationButton.setText(getString(R.string.registering_message_complete));
					mRegistrationButton.setEnabled(false);
					String token = intent.getStringExtra("token");
					mInformationTextView.setText(token);
				}

			}
		};
	}


	EditText idView;
	EditText passwordView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		idView = (EditText)findViewById(R.id.login_editText1);
		passwordView = (EditText)findViewById(R.id.login_editText2);
		Button btn = (Button)findViewById(R.id.btn_login);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String id = "user01@gmail.com";
				final String password = "1234";
				NetworkManager.getInstance().postDongneLogin(LoginActivity.this, id, password, new NetworkManager.OnResultListener<LoginInfo>(){
					@Override
					public void onSuccess(Request request, LoginInfo result) {
                        Toast.makeText(LoginActivity.this, "result:"+ result, Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(LoginActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
//		btn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				final String id = idView.getText().toString();
//				final String password = passwordView.getText().toString();
//				NetworkManager.getInstance().login(id, password, new NetworkManager.OnLoginResultListener() {
//
//					@Override
//					public void onSuccess(String message) {
//						PropertyManager.getInstnace().setUserName(id);
//						PropertyManager.getInstnace().setPassword(password);
//						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//						startActivity(intent);
//						finish();
//					}
//				});
//			}
//		});
		
		btn = (Button)findViewById(R.id.btn_signup);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
			}
		});

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

	}

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
