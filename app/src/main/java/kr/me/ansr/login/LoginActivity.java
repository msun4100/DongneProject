package kr.me.ansr.login;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.gcmchat.model.User;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.image.MediaStoreActivity;
import okhttp3.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by KMS on 2016-07-11.
 */

public class LoginActivity extends AppCompatActivity {


	private static final String TAG = LoginActivity.class.getSimpleName();

	InputMethodManager imm;

	EditText emailView;
	EditText passwordView;
	TextView gotoSignupView;
	ImageView idFindIcon, pwFindIcon;
	TextInputLayout inputLayoutPw, inputLayoutEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		emailView = (EditText) findViewById(R.id.login_editText1);
		passwordView = (EditText) findViewById(R.id.login_editText2);

		Button btn = (Button) findViewById(R.id.btn_login);
		assert btn != null;
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doLogin();
			}
		});

		btn = (Button) findViewById(R.id.btn_scroll);
		assert btn != null;
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, MediaStoreActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		gotoSignupView = (TextView)findViewById(R.id.text_login_signup);
		gotoSignupView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignupWayActivity.class);
				startActivity(intent);
				finish();
			}
		});

		idFindIcon = (ImageView)findViewById(R.id.image_login_idfind);
		idFindIcon.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Toast.makeText(LoginActivity.this, "idfind", Toast.LENGTH_SHORT).show();
			}
		});
		pwFindIcon = (ImageView)findViewById(R.id.image_login_pwfind);
		pwFindIcon.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				Toast.makeText(LoginActivity.this, "pwfind", Toast.LENGTH_SHORT).show();
			}
		});

		//add textWatcher
		emailView.addTextChangedListener(new MyTextWatcher(emailView));
		passwordView.addTextChangedListener(new MyTextWatcher(passwordView));
		passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND){
					//....
					doLogin();
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
		inputLayoutPw = (TextInputLayout) findViewById(R.id.input_layout_pw);
		inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
	}

	private void requestFocus(View view) {
		if (view.requestFocus()) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	// Validating name
	private boolean validatePassword() {
		String str = passwordView.getText().toString();
		if (str.isEmpty() || str.length() < 8) {
//			inputLayoutPw.setError(getString(R.string.err_msg_pw));
			passwordView.setTextColor(ContextCompat.getColor(this, R.color.invalid_input));
			requestFocus(passwordView);
			return false;
		} else {
			passwordView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
			inputLayoutPw.setErrorEnabled(false);
		}
		return true;
	}
	// Validating email
	private boolean validateEmail() {
		String email = emailView.getText().toString().trim();
		if (email.isEmpty() || !isValidEmail(email)) {
//			inputLayoutEmail.setError(getString(R.string.err_msg_email));
			emailView.setTextColor(ContextCompat.getColor(this, R.color.invalid_input));
			requestFocus(emailView);
			return false;
		} else {
			emailView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
			inputLayoutEmail.setErrorEnabled(false);
		}
		return true;
	}

	private static boolean isValidEmail(String email) {
		return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private class MyTextWatcher implements TextWatcher {
		private View view;
		private MyTextWatcher(View view) {
			this.view = view;
		}
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}

		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
		}
		public void afterTextChanged(Editable editable) {
			switch (view.getId()) {
				case R.id.login_editText1:
					validateEmail();
					break;
				case R.id.login_editText2:
					validatePassword();
					break;
				default:break;
			}
		}
	}

	private void doLogin(){
		if(!validateEmail()){
			Toast.makeText(LoginActivity.this, "올바른 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!validatePassword()){
			Toast.makeText(LoginActivity.this, "비밀번호를 8자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}

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
					PropertyManager.getInstance().setUserId(result.user.user_id);
					PropertyManager.getInstance().setUnivId(result.user.univId);
					PropertyManager.getInstance().setUserName(result.user.name);
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
}
