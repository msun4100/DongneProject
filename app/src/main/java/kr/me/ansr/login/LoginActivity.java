package kr.me.ansr.login;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.gcmchat.model.User;
import kr.me.ansr.login.LoginResult;
import kr.me.ansr.NetworkManager;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class LoginActivity extends Activity {


	private static final String TAG = LoginActivity.class.getSimpleName();

	EditText emailView;
	EditText passwordView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		emailView = (EditText) findViewById(R.id.login_editText1);
		passwordView = (EditText) findViewById(R.id.login_editText2);
		Button btn = (Button) findViewById(R.id.btn_login);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String email = emailView.getText().toString();
				final String password = passwordView.getText().toString();
				NetworkManager.getInstance().postDongneLogin(LoginActivity.this, email, password, new NetworkManager.OnResultListener<LoginInfo>() {
					@Override
					public void onSuccess(Request request, LoginInfo result) {
						if (result.error.equals(true)) {
							Toast.makeText(LoginActivity.this, TAG + "result.error:" + result.message, Toast.LENGTH_SHORT).show();
						} else {
							PropertyManager.getInstance().setEmail(email);
							PropertyManager.getInstance().setPassword(password);

							//for chatting PropertyManager
							User user = new User("" + result.user.user_id, result.user.name, result.user.email);
							MyApplication.getInstance().getPrefManager().storeUser(user);

							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
							finish();
						}
					}

					@Override
					public void onFailure(Request request, int code, Throwable cause) {
						Toast.makeText(LoginActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});

		btn = (Button) findViewById(R.id.btn_signup);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignupWayActivity.class);
				startActivity(intent);
//				finish();
			}
		});
		btn = (Button) findViewById(R.id.btn_scrolling);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				startActivity(intent);
			}
		});
	}



}
