package kr.me.ansr.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.SearchDeptDialogFragment;
import kr.me.ansr.common.SearchUnivDialogFragment;
import kr.me.ansr.gcmchat.model.User;
import kr.me.ansr.login.autocomplete.dept.DeptInfo;
import kr.me.ansr.login.autocomplete.dept.DeptResult;
import kr.me.ansr.login.autocomplete.dept.MyDeptAdapter;
import kr.me.ansr.login.autocomplete.univ.MyUnivAdapter;
import kr.me.ansr.login.autocomplete.univ.UnivInfo;
import kr.me.ansr.login.autocomplete.univ.UnivResult;
import kr.me.ansr.login.spinner.MySpinnerAdapter;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-11.
 */

public class SignupActivity extends AppCompatActivity {
	private static final String TAG = SignupActivity.class.getSimpleName();
//	public AutoCompleteTextView textViewDept;
	public TextView textViewUniv; //,textViewDept;
	public EditText inputDept;
//	ArrayAdapter<String> mUnivAdapter, mDeptAdapter;
	public MyUnivAdapter mUnivAdapter;
	public MyDeptAdapter mDeptAdapter;

	Spinner spinner;
	MySpinnerAdapter mySpinnerAdapter;

	public EditText inputName, inputCompany, inputJob;
	public String email, password, mSpinnerItem;
	public int mUnivId, mDeptId;
	public String mUnivname, mDeptname;

	public Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setNavigationIcon(R.drawable.common_back_selector);
		toolbar.setBackgroundResource(R.drawable.a_join_titlebar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		Intent intent = getIntent();
		if(intent.getExtras() != null){
			email = intent.getExtras().getString("email");
			password = intent.getExtras().getString("password");
		}
		if(email != null){
			Toast.makeText(getApplicationContext(), email, Toast.LENGTH_SHORT).show();
		} else {
			email = null;
			password = null;
			Toast.makeText(getApplicationContext(), "Account is null", Toast.LENGTH_SHORT).show();
		}
		textViewUniv = (TextView)findViewById(R.id.text_signup_univ);
//		mUnivAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		mUnivAdapter = new MyUnivAdapter();
		textViewUniv.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				SearchUnivDialogFragment mDialogFragment = new SearchUnivDialogFragment();
				Bundle b = new Bundle();
				b.putString("tag", SearchUnivDialogFragment.TAG_SIGN_UP);
//				b.putSerializable("item", mItem);
				mDialogFragment.setArguments(b);
				mDialogFragment.show(getSupportFragmentManager(), "customDialog");
			}
		});

		inputDept = (EditText)findViewById(R.id.edit_signup_dept);
//		textViewDept = (TextView)findViewById(R.id.text_signup_dept);
//		mDeptAdapter = new MyDeptAdapter();
//		textViewDept.setOnClickListener(new View.OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				if(textViewUniv == null || textViewUniv.getText().toString().equals("")){
//					Toast.makeText(getApplicationContext(), "대학교명을 입력해주세요.", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				SearchDeptDialogFragment mDialogFragment = new SearchDeptDialogFragment();
//				Bundle b = new Bundle();
//				b.putString("tag", SearchDeptDialogFragment.TAG_SIGN_UP);
////				b.putSerializable("item", mItem);
//				mDialogFragment.setArguments(b);
//				mDialogFragment.show(getSupportFragmentManager(), "customDialog");
//			}
//		});
//		mDeptAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1);
//		textViewDept.setAdapter(mDeptAdapter);
//		textViewDept.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////				Toast.makeText(getApplicationContext(), "deptId: "+mDeptAdapter.getDeptId(position), Toast.LENGTH_SHORT).show();
//				mDeptId = mDeptAdapter.getDeptId(position);
//				mDeptname = mDeptAdapter.getDeptName(position);
//			}
//		});

		spinner = (Spinner) findViewById(R.id.spinner);
		mySpinnerAdapter = new MySpinnerAdapter();
		spinner.setAdapter(mySpinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String str = (String)mySpinnerAdapter.getItem(position);
				try {
					int num = Integer.valueOf(str);
					mSpinnerItem = str;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					mSpinnerItem = "";
					spinner.setSelection(0);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				spinner.setSelection(0);
			}
		});

		inputName = (EditText)findViewById(R.id.edit_signup_name);
		inputCompany = (EditText)findViewById(R.id.edit_signup_company);
		inputJob = (EditText)findViewById(R.id.edit_signup_job);
		inputJob.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
		Button btn = (Button)findViewById(R.id.btn_signup_send);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDeptname = inputDept.getText().toString().trim();
				doSignUp();
			}
		});

		initData();

		Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
		t.setScreenName(getClass().getSimpleName());
		t.send(new HitBuilders.AppViewBuilder().build());
	}


	ProgressDialog dialog = null;

	public void getUniv(){
		NetworkManager.getInstance().getDongneUniv(getApplicationContext(), new NetworkManager.OnResultListener<UnivInfo>() {
			@Override
			public void onSuccess(Request request, UnivInfo result) {
				if (result.error.equals(false)) {
					String[] arr;
					if(result.result != null){
						arr = new String[result.result.size()];
						ArrayList<UnivResult> list = result.result;
//						mUnivAdapter.clear();
						for(int i=0; i < list.size(); i ++){
							arr[i] = list.get(i).univname;
							mUnivAdapter.add(list.get(i));
						}
					}
				} else {
					mUnivAdapter.clearAll();
					Toast.makeText(SignupActivity.this, TAG + "result.error:" + result.message, Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(SignupActivity.this, TAG + "onFailure: " + cause, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		dialog = new ProgressDialog(SignupActivity.this);
		dialog.setTitle("Loading....");
		dialog.show();
	}
	public void getDept(int univId) {
		NetworkManager.getInstance().getDongneDept(getApplicationContext(), univId, new NetworkManager.OnResultListener<DeptInfo>() {
			@Override
			public void onSuccess(Request request, DeptInfo result) {
				if (result.error.equals(false)) {
					if(result.result.size() > 0){
						ArrayList<DeptResult> list = result.result;
						mDeptAdapter.clearAll();
						for(int i=0; i < list.size(); i ++){
							mDeptAdapter.add(list.get(i));
						}
						Toast.makeText(SignupActivity.this, "학과명을 입력해 주세요.", Toast.LENGTH_SHORT).show();
					} else {	//docs.size() returned zero
						mDeptAdapter.clearAll();
						Toast.makeText(SignupActivity.this, TAG + "result.size is zero", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(SignupActivity.this, TAG + "error: true" + result.message, Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(SignupActivity.this, TAG + "onFailure: " + cause, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		dialog = new ProgressDialog(SignupActivity.this);
		dialog.setTitle("Loading....");
		dialog.show();
	}

	private void doSignUp(){
		if (!validateName()) {
			Toast.makeText(SignupActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!validateUniv()) {
			Toast.makeText(SignupActivity.this, "대학교명을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!validateUnivName()) {
			Toast.makeText(SignupActivity.this, "대학교명을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!validateDept()) {
			Toast.makeText(SignupActivity.this, "학과명을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!validateDeptName()) {
			Toast.makeText(SignupActivity.this, "학과명을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!validateEnterYear()) {
			Toast.makeText(SignupActivity.this, "학번을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!validateCompany()) {
			Toast.makeText(SignupActivity.this, "회사명을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!validateJob()) {
			Toast.makeText(SignupActivity.this, "직무를 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		final String username = inputName.getText().toString().trim();
		String jobname = inputCompany.getText().toString().trim();
		String jobteam = inputJob.getText().toString().trim();

		final int isGraduate = 1;
		NetworkManager.getInstance().postDongneRegister(SignupActivity.this,
				email, password, username, mUnivId, mDeptId, mDeptname, mSpinnerItem, isGraduate, jobname, jobteam,
				new NetworkManager.OnResultListener<LoginInfo>() {
			@Override
			public void onSuccess(Request request, LoginInfo result) {
				if (result.error.equals(false)) {
					Toast.makeText(SignupActivity.this, "error:false" + result.toString(), Toast.LENGTH_SHORT).show();
					Log.e(TAG, "onSuccess: "+ result.toString() );
//					Log.e(TAG, result.result.toString());
					PropertyManager.getInstance().setUserId(result.result.user_id);
					PropertyManager.getInstance().setUnivId(""+mUnivId);
					PropertyManager.getInstance().setEmail(email);
					PropertyManager.getInstance().setPassword(password);
					PropertyManager.getInstance().setUserName(username);
					PropertyManager.getInstance().setUnivName(mUnivname);
					PropertyManager.getInstance().setDeptName(mDeptname);
					doLogin();
//					PropertyManager.getInstance().setProfile("");
//					PropertyManager.getInstance().setDeptName(mDeptname);
//					PropertyManager.getInstance().setDeptId(String.valueOf(mDeptId));
//					PropertyManager.getInstance().setEnterYear(mSpinnerItem);
//					PropertyManager.getInstance().setIsGraduate(String.valueOf(isGraduate));
					//프로퍼티 저장할 것들 저장하고 로그인 요청. --> 로그인 성공하면 메인액티비티로
					//...
				} else {
					Toast.makeText(SignupActivity.this, "error:true" + result.toString(), Toast.LENGTH_SHORT).show();
					PropertyManager.getInstance().setUserId("");
					PropertyManager.getInstance().setUnivId("");
					PropertyManager.getInstance().setEmail("");
					PropertyManager.getInstance().setPassword("");
					PropertyManager.getInstance().setUserName("");
					PropertyManager.getInstance().setUnivName("");
					PropertyManager.getInstance().setDeptName("");
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(SignupActivity.this, TAG+" onFailure:" + cause, Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		});
		dialog = new ProgressDialog(SignupActivity.this);
		dialog.setTitle("Loading....");
		dialog.show();
	}
	private void doLogin(){
		final String email = PropertyManager.getInstance().getEmail();
		final String password = PropertyManager.getInstance().getPassword();
		NetworkManager.getInstance().postDongneLogin(SignupActivity.this, email, password, new NetworkManager.OnResultListener<LoginInfo>() {
			@Override
			public void onSuccess(Request request, LoginInfo result) {
				if (result.error.equals(true)) {
					Toast.makeText(SignupActivity.this, TAG + "result.error:" + result.message, Toast.LENGTH_SHORT).show();
				} else {
					PropertyManager.getInstance().setUserId(result.result.user_id);
					PropertyManager.getInstance().setUnivId(result.result.univId);
					//for chatting PropertyManager
					User user = new User(Integer.parseInt(result.result.user_id), result.result.name, result.result.email);
					MyApplication.getInstance().getPrefManager().storeUser(user);
					Intent intent = new Intent(SignupActivity.this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
//					finish();
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(SignupActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		dialog = new ProgressDialog(SignupActivity.this);
		dialog.setTitle("Loading....");
		dialog.show();
	}

	private void initData() {
		//서버에 요청해서 대학교 / 학과 리스트를 미리 받아서 리스트에 저장해둠.
		String[] univArray = getResources().getStringArray(R.array.univ);
//		for (int i = 0; i < univArray.length; i++) {
//			mUnivAdapter.add(univArray[i]);
//		}
		String[] yearArray = getResources().getStringArray(R.array.spinner_year_item);
		for (int i = yearArray.length-1; i >= 0; i--) {
			mySpinnerAdapter.add(yearArray[i]);
		}
//		for (int i = 0; i < yearArray.length; i++) {
//			mySpinnerAdapter.add(yearArray[i]);
//		}
		spinner.setSelection(0);
//		spinner.setDropDownVerticalOffset(100);

		getUniv();
	}


	private void requestFocus(View view) {
		if (view.requestFocus()) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	// Validating name
	private boolean validateName() {
		if (inputName.getText().toString().trim().isEmpty()) {
//				inputLayoutPw.setError(getString(R.string.err_msg_pw));
			inputName.setText("");
			requestFocus(inputName);
			return false;
		}
		return true;
	}
	private boolean validateUniv() {
		if (textViewUniv.getText().toString().trim().isEmpty()) {
			textViewUniv.setText("");
			requestFocus(textViewUniv);
			return false;
		}
		return true;
	}
	private boolean validateDept() {
		if (inputDept.getText().toString().trim().isEmpty()) {
			inputDept.setText("");
			requestFocus(inputDept);
			return false;
		}
		return true;
	}
//	private boolean validateDept() {
//		if (textViewDept.getText().toString().trim().isEmpty()) {
//			textViewDept.setText("");
//			requestFocus(textViewDept);
//			return false;
//		}
//		return true;
//	}
	private boolean validateEnterYear() {
		if (mSpinnerItem == null || mSpinnerItem == "") {
//			textViewDept.setText("");
//			requestFocus(mSpinnerItem);
			return false;
		}


		return true;
	}
	private boolean validateCompany() {
		if (inputCompany.getText().toString().trim().isEmpty()) {
			inputCompany.setText("");
			requestFocus(inputCompany);
			return false;
		}
		return true;
	}
	private boolean validateJob() {
		if (inputJob.getText().toString().trim().isEmpty()) {
			inputJob.setText("");
			requestFocus(inputJob);
			return false;
		}
		return true;
	}

	private boolean validateDeptName() {
		if (mDeptname == null || mDeptname == "") {
			return false;
		}
		return true;
	}
	private boolean validateUnivName() {
		if (mUnivname == null || mUnivname == "") {
			return false;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
}
