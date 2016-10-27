package kr.me.ansr.login;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.common.CommonInfo;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.common.account.ConfirmDialogFragment;
import kr.me.ansr.common.account.FindAccountDialogFragment;
import kr.me.ansr.common.account.FindAccountInfo;
import kr.me.ansr.common.account.FindAccountModel;
import kr.me.ansr.common.account.FindPWDialogFragment;
import kr.me.ansr.common.account.IFindAccountReturned;
import kr.me.ansr.common.account.LogoutDialogFragment;
import kr.me.ansr.gcmchat.model.User;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.image.MediaStoreActivity;
import kr.me.ansr.image.TakePhotoActivity;
import okhttp3.Request;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity implements IFindAccountReturned, IDataReturned{
	@Override
	public void onDataReturned(String email) {
		findPW(email);
	}

	@Override
	public void onDataReturned(FindAccountModel info) {
		findID(info);
	}
	private void findPW(final String email){
		NetworkManager.getInstance().postDongneFindPW(LoginActivity.this, email, new NetworkManager.OnResultListener<CommonInfo>() {
			@Override
			public void onSuccess(Request request, CommonInfo result) {
				if (result.error.equals(true)) {
					ConfirmDialogFragment mDialogFragment = new ConfirmDialogFragment();  //자주 안쓰니까 newInstance 안함.
					Bundle b = new Bundle();
					b.putString("tag", ConfirmDialogFragment.TAG_FIND_PW);
					b.putString("title","회원정보를 찾을 수 없습니다.");
					b.putString("body",  email);
					mDialogFragment.setArguments(b);
					mDialogFragment.show(getSupportFragmentManager(), "logoutDialog");
				} else {
					ConfirmDialogFragment mDialogFragment = new ConfirmDialogFragment();  //자주 안쓰니까 newInstance 안함.
					Bundle b = new Bundle();
					b.putString("tag", ConfirmDialogFragment.TAG_FIND_PW);
					b.putString("title","임시 비밀번호가 발송되었습니다.");
					b.putString("body", email);
					mDialogFragment.setArguments(b);
					mDialogFragment.show(getSupportFragmentManager(), "logoutDialog");
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(LoginActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		dialog = new ProgressDialog(LoginActivity.this);
		dialog.setTitle("Loading....");
		dialog.show();
	}
	private void findID(FindAccountModel info){
		NetworkManager.getInstance().postDongneFindID(LoginActivity.this, info, new NetworkManager.OnResultListener<FindAccountInfo>() {
			@Override
			public void onSuccess(Request request, FindAccountInfo result) {
				if (result.error.equals(true)) {
					ConfirmDialogFragment mDialogFragment = new ConfirmDialogFragment();  //자주 안쓰니까 newInstance 안함.
					Bundle b = new Bundle();
					b.putString("tag", ConfirmDialogFragment.TAG_FIND_ID);
					b.putString("title","아이디를 찾을 수 없습니다.");
					b.putString("body", "회원정보를 확인해주세요.");
					mDialogFragment.setArguments(b);
					mDialogFragment.show(getSupportFragmentManager(), "logoutDialog");
				} else {
					if(result.result != null && result.result.size() > 0){
						String str = "";
						for(String s : result.result){
							Log.e("s", s);
							int num = s.indexOf('@');
							StringBuilder sb = new StringBuilder();
							sb.append(s.substring(0, num-3)); //@ 앞 4칸까지 저장하고
							for (int i = num-2; i < num+1; i++) { // 그 다음 칸부터 '@' 전(num)까지 '*' 저장
								sb.append("*");
							}
							sb.append(s.substring(num, s.length()));
							str+=sb +"\n";
						}
						str.substring(0, str.length()-2);
						ConfirmDialogFragment mDialogFragment = new ConfirmDialogFragment();  //자주 안쓰니까 newInstance 안함.
						Bundle b = new Bundle();
						b.putString("tag", ConfirmDialogFragment.TAG_FIND_ID);
						b.putString("title","회원님의 아이디는");
						b.putString("body", str + " 입니다.");
						mDialogFragment.setArguments(b);
						mDialogFragment.show(getSupportFragmentManager(), "logoutDialog");
					}

				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(LoginActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		dialog = new ProgressDialog(LoginActivity.this);
		dialog.setTitle("Loading....");
		dialog.show();

	}
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
//				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
				doLogin();
			}
		});

		btn = (Button) findViewById(R.id.btn_scroll);
		assert btn != null;
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, TakePhotoActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
//				finish();
//				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
//				startActivity(intent);
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
				FindAccountDialogFragment pDialogFragment = new FindAccountDialogFragment();  //자주 안쓰니까 newInstance 안함.
				Bundle pb = new Bundle();
				pb.putString("tag", FindAccountDialogFragment.TAG_FIND_ACCOUNT);
				pDialogFragment.setArguments(pb);
				pDialogFragment.show(getSupportFragmentManager(), "findAccount");
			}
		});
		pwFindIcon = (ImageView)findViewById(R.id.image_login_pwfind);
		pwFindIcon.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				FindPWDialogFragment pDialogFragment = new FindPWDialogFragment();  //자주 안쓰니까 newInstance 안함.
				Bundle pb = new Bundle();
				pb.putString("tag", FindPWDialogFragment.TAG_FIND_PW);
				pDialogFragment.setArguments(pb);
				pDialogFragment.show(getSupportFragmentManager(), "findPW");
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
		if (str.isEmpty() || str.length() < 4) {
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
	ProgressDialog dialog = null;

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
					User user = new User(Integer.parseInt(result.user.user_id), result.user.name, result.user.email);
					MyApplication.getInstance().getPrefManager().storeUser(user);
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(LoginActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
				Log.e(TAG, cause.toString());
				dialog.dismiss();
			}
		});
		dialog = new ProgressDialog(LoginActivity.this);
		dialog.setTitle("Loading....");
		dialog.show();
	}
}
