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
import android.os.AsyncTask;
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

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.playlog.internal.LogEvent;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginDefine;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by KMS on 2016-07-11.
 */

public class LoginActivity extends AppCompatActivity implements IFindAccountReturned, IDataReturned{
	@Override
	public void onDataReturned(String email) {
		switch(email){
			case "DUP_EMAIL":	//네이버 로그인시 프로바이더:로컬로 이미 가입 된 경우
				Log.e(TAG, "onDataReturned: DUP_EMAIL");
				break;
			case "DEFAULT":
			default:
				findPW(email);
				break;
		}

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
				doLogin();
			}
		});

		btn = (Button) findViewById(R.id.btn_scroll);
		assert btn != null;
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, OAuthSampleActivity.class);
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

		//init NAVER api
		mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
//		mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);	//이 코드는 샘플의 verifying 처럼 이미 인증처리 되었다면 expire 될 여지가 있음.(네이버로그인 실행 후 다른 액티비티 들어갔다 나오는 등)
		mOAuthLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOAuthLoginInstance.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
			}
		});
		mContext = this;
		initApiData();

		//init GA
		Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName(getClass().getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());

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
					PropertyManager.getInstance().setUserId(result.result.user_id);
					PropertyManager.getInstance().setUnivId(result.result.univId);
					PropertyManager.getInstance().setUserName(result.result.name);
					//for chatting PropertyManager
					User user = new User(Integer.parseInt(result.result.user_id), result.result.name, result.result.email);
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
	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	@Override
	protected void onResume() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onResume();
	}
	//Naver Login api
	private static String OAUTH_CLIENT_ID = "wQ1ellq2nJFLA5N5Vubl";
	private static String OAUTH_CLIENT_SECRET = "Bav2X1aE8b";
	private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";

	private static OAuthLogin mOAuthLoginInstance;
	private static Context mContext;

	//API Values
	String accessToken ;
	String refreshToken ;
	long expiresAt ;
	String tokenType ;
	String state;

	private OAuthLoginButton mOAuthLoginButton;

	private void initApiData() {
		OAuthLoginDefine.DEVELOPER_VERSION = true;  //Debug mode
		mOAuthLoginInstance = OAuthLogin.getInstance();
		mOAuthLoginInstance.init(mContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);
	}

	/**
	 * startOAuthLoginActivity() 호출시 인자로 넘기거나, OAuthLoginButton 에 등록해주면 인증이 종료되는 걸 알 수 있다.
	 */
	private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
		@Override
		public void run(boolean success) {
			if (success) {
				accessToken = mOAuthLoginInstance.getAccessToken(mContext);
				refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
				expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
				tokenType = mOAuthLoginInstance.getTokenType(mContext);
				state = mOAuthLoginInstance.getState(mContext).toString();
//				mOauthAT.setText(accessToken);
//				mOauthRT.setText(refreshToken);
//				mOauthExpires.setText(String.valueOf(expiresAt));
//				mOauthTokenType.setText(tokenType);
//				mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());

				//로그인이 성공하면  네이버에 계정값들을 가져온다. (Parsing)
				new RequestApiTask().execute();
			} else {
				String errorCode = mOAuthLoginInstance.getLastErrorCode(mContext).getCode();
				String errorDesc = mOAuthLoginInstance.getLastErrorDesc(mContext);
				Toast.makeText(mContext, "Naver login\nerrorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
//				Toast.makeText(mContext, "로그인이 취소/실패 하였습니다.!", Toast.LENGTH_SHORT).show();
			}
		}
	};
	private void updateApiValues() {
//		mOauthAT.setText(mOAuthLoginInstance.getAccessToken(mContext));
//		mOauthRT.setText(mOAuthLoginInstance.getRefreshToken(mContext));
//		mOauthExpires.setText(String.valueOf(mOAuthLoginInstance.getExpiresAt(mContext)));
//		mOauthTokenType.setText(mOAuthLoginInstance.getTokenType(mContext));
//		mOAuthState.setText(mOAuthLoginInstance.getState(mContext).toString());
		accessToken = mOAuthLoginInstance.getAccessToken(mContext);
		refreshToken = mOAuthLoginInstance.getRefreshToken(mContext);
		expiresAt = mOAuthLoginInstance.getExpiresAt(mContext);
		tokenType = mOAuthLoginInstance.getTokenType(mContext);
		state = mOAuthLoginInstance.getState(mContext).toString();
	}


	private class DeleteTokenTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			boolean isSuccessDeleteToken = mOAuthLoginInstance.logoutAndDeleteToken(mContext);

			if (!isSuccessDeleteToken) {
				// 서버에서 token 삭제에 실패했어도 클라이언트에 있는 token 은 삭제되어 로그아웃된 상태이다
				// 실패했어도 클라이언트 상에 token 정보가 없기 때문에 추가적으로 해줄 수 있는 것은 없음
				Log.d(TAG, "errorCode:" + mOAuthLoginInstance.getLastErrorCode(mContext));
				Log.d(TAG, "errorDesc:" + mOAuthLoginInstance.getLastErrorDesc(mContext));
			}

			return null;
		}
		protected void onPostExecute(Void v) {
			updateApiValues();
		}
	}


	private class RequestApiTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
//			mApiResultText.setText((String) "");
		}
		@Override
		protected String doInBackground(Void... params) {
			String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
//			https://apis.naver.com/nidlogin/nid/getUserProfile.json?response_type=json	//JSON 타입으로 받기
			String at = mOAuthLoginInstance.getAccessToken(mContext);
			return mOAuthLoginInstance.requestApi(mContext, at, url);
//			ParsingVersionData(mOAuthLoginInstance.requestApi(mContext, at, url));
//			return null;
		}
		protected void onPostExecute(String content) {
//			mApiResultText.setText((String) content);
			ParsingVersionData(content);
			Log.d("myLog", "email " + email);
			Log.d("myLog", "name " + name);
			if (email == null) {
				Toast.makeText(LoginActivity.this, "로그인 실패하였습니다.  잠시후 다시 시도해 주세요!!", Toast.LENGTH_SHORT).show();
			} else {
				//서버 로그인 요청
				Toast.makeText(LoginActivity.this, "인증 성공", Toast.LENGTH_SHORT).show();
				checkEmail();
			}
		}
	}	//RequestApiTask

	private class RefreshTokenTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			return mOAuthLoginInstance.refreshAccessToken(mContext);
		}
		protected void onPostExecute(String res) {
			updateApiValues();
		}
	}

	String email = "";
	String nickname = "";
	String enc_id = "";
	String profile_image = "";
	String age = "";
	String gender = "";
	String id = "";
	String name = "";
	String birthday = "";
	private void ParsingVersionData(String data) { // xml 파싱
		String f_array[] = new String[9];
		try {
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
			parser.setInput(input, "UTF-8");
			int parserEvent = parser.getEventType();
			String tag;
			boolean inText = false;
			boolean lastMatTag = false;
			int colIdx = 0;
			while (parserEvent != XmlPullParser.END_DOCUMENT) {
				switch (parserEvent) {
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						if (tag.compareTo("xml") == 0) {
							inText = false;
						} else if (tag.compareTo("data") == 0) {
							inText = false;
						} else if (tag.compareTo("result") == 0) {
							inText = false;
						} else if (tag.compareTo("resultcode") == 0) {
							inText = false;
						} else if (tag.compareTo("message") == 0) {
							inText = false;
						} else if (tag.compareTo("response") == 0) {
							inText = false;
						} else {
							inText = true;
						}
						break;

					case XmlPullParser.TEXT:
						tag = parser.getName();
						if (inText) {
							if (parser.getText() == null) {
								f_array[colIdx] = "";
							} else {
								f_array[colIdx] = parser.getText().trim();
							}
							colIdx++;
						}
						inText = false;
						break;

					case XmlPullParser.END_TAG:
						tag = parser.getName();
						inText = false;
						break;
				}
				parserEvent = parser.next();
			}
		} catch (Exception e) {
			Log.e("dd", "Error in network call", e);
		}
		email = f_array[0];
		nickname = f_array[1];
		enc_id = f_array[2];
		profile_image = f_array[3];
		age = f_array[4];
		gender = f_array[5];
		id = f_array[6];
		name = f_array[7];
		birthday = f_array[8];
	}

	private void checkEmail(){
		NetworkManager.getInstance().postDongneUserEmail(LoginActivity.this, email, new NetworkManager.OnResultListener<LoginInfo>() {
			@Override
			public void onSuccess(Request request, LoginInfo result) {
				if (result.error.equals(true)) {
					Toast.makeText(LoginActivity.this, TAG + "result.error:" + result.message, Toast.LENGTH_SHORT).show();
				} else {
					String msg = result.message.toString();
					if(msg.equals("DUP_EMAIL") && result.result.provider.equals("local")){
						Log.e(TAG, "onSuccess: "+ result.result.toString() );
						ConfirmDialogFragment mDialogFragment = new ConfirmDialogFragment();  //자주 안쓰니까 newInstance 안함.
						Bundle b = new Bundle();
						b.putString("tag", ConfirmDialogFragment.TAG_CHECK_EMAIL);
						b.putString("title","이미 가입된 회원입니다.");
						String body = "";
						body += "고객님은 '";
						body += email;
						body += "' 아이디로\n";
						body += MyApplication.getTimeStamp(result.result.created_at);
						body += " 에 회원가입 하셨습니다.\n해당 계정으로 로그인 시\n";
						body += "네이버 아이디가 자동으로 연동됩니다.";
						b.putString("body",  body);
						mDialogFragment.setArguments(b);
						mDialogFragment.show(getSupportFragmentManager(), "logoutDialog");
					} else if(msg.equals("DUP_EMAIL") && result.result.provider.equals("naver")){
						Log.e(TAG, "onSuccess: provider === naver" );
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(intent);
						finish();
						//메인액티비티로
					} else if(msg.equals("AVAILABLE_EMAIL")){
						Intent intent = new Intent(LoginActivity.this, TermsActivity.class);
						intent.putExtra("email", email);
						intent.putExtra("username", name);
						intent.putExtra("provider", "naver");
						startActivity(intent);
						finish();
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




}
