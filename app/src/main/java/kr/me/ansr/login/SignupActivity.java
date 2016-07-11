package kr.me.ansr.login;

import kr.me.ansr.MainActivity;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignupActivity extends Activity {

	EditText idView;
	EditText passwordView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		idView = (EditText)findViewById(R.id.signup_editText1);
		passwordView = (EditText)findViewById(R.id.signup_editText3);
		Button btn = (Button)findViewById(R.id.signup_btn_send);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String id = idView.getText().toString();
				final String password = passwordView.getText().toString();
				NetworkManager.getInstance().login(id, password, new NetworkManager.OnLoginResultListener() {
					
					@Override
					public void onSuccess(String message) {
						PropertyManager.getInstance().setUserName(id);
						PropertyManager.getInstance().setPassword(password);
						Intent intent = new Intent(SignupActivity.this, MainActivity.class);						
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
					}
				});
				
			}
		});


		
	}


}
