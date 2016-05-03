package kr.me.ansr.login;


import kr.me.ansr.MainActivity;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.NetworkManager.OnResultListener;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		String name = PropertyManager.getInstnace().getUserName();
		if (!name.equals("")) {
			String password = PropertyManager.getInstnace().getPassword();
			NetworkManager.getInstance().login(name, password, new NetworkManager.OnLoginResultListener() {
				
				@Override
				public void onSuccess(String message) {
					Intent intent = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			});
		} else {
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}, 1000);
		}
	}

}
