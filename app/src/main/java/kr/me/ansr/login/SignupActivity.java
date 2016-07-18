package kr.me.ansr.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.login.autocomplete.ex.dept.DeptInfo;
import kr.me.ansr.login.autocomplete.ex.dept.DeptResult;
import kr.me.ansr.login.autocomplete.ex.dept.MyDeptAdapter;
import kr.me.ansr.login.autocomplete.ex.univ.MyUnivAdapter;
import kr.me.ansr.login.autocomplete.ex.univ.UnivInfo;
import kr.me.ansr.login.autocomplete.ex.univ.UnivResult;
import okhttp3.Request;

public class SignupActivity extends Activity {
	private static final String TAG = SignupActivity.class.getSimpleName();
	AutoCompleteTextView textViewUniv, textViewDept;
//	ArrayAdapter<String> mUnivAdapter, mDeptAdapter;
	MyUnivAdapter mUnivAdapter;
	MyDeptAdapter mDeptAdapter;

	Spinner spinner;
	MySpinnerAdapter mySpinnerAdapter;

	EditText inputName, inputCompany, inputJob;
	String email, password, mSpinnerItem;
	int mUnivId, mDeptId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

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
		textViewUniv = (AutoCompleteTextView)findViewById(R.id.auto_text_signup_univ);
//		mUnivAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		mUnivAdapter = new MyUnivAdapter();
		textViewUniv.setAdapter(mUnivAdapter);
		textViewUniv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Toast.makeText(getApplicationContext(), "univId: "+mUnivAdapter.getUnivId(position), Toast.LENGTH_SHORT).show();
				mUnivId = mUnivAdapter.getUnivId(position);
			}
		});


		textViewDept = (AutoCompleteTextView)findViewById(R.id.auto_text_signup_dept);
		mDeptAdapter = new MyDeptAdapter();
//		mDeptAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1);
		textViewDept.setAdapter(mDeptAdapter);
		textViewDept.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Toast.makeText(getApplicationContext(), "deptId: "+mDeptAdapter.getDeptId(position), Toast.LENGTH_SHORT).show();
				mDeptId = mDeptAdapter.getDeptId(position);
			}
		});

		spinner = (Spinner) findViewById(R.id.spinner);
		mySpinnerAdapter = new MySpinnerAdapter();
		spinner.setAdapter(mySpinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSpinnerItem = (String)mySpinnerAdapter.getItem(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				spinner.setSelection(0);
			}
		});

		inputName = (EditText)findViewById(R.id.edit_signup_name);
		inputCompany = (EditText)findViewById(R.id.edit_signup_company);
		inputJob = (EditText)findViewById(R.id.edit_signup_job);
		Button btn = (Button)findViewById(R.id.btn_signup_send);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				getDept(1);
				doSignUp();
			}
		});

		initData();
	}
	private void getUniv(){
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
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(SignupActivity.this, TAG + "onFailure: " + cause, Toast.LENGTH_SHORT).show();
			}
		});
	}
	private void getDept(int univId) {
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
					} else {	//docs.size() returned zero
						mDeptAdapter.clearAll();
						Toast.makeText(SignupActivity.this, TAG + "result.size is zero", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(SignupActivity.this, TAG + "result.error:" + result.message, Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {
				Toast.makeText(SignupActivity.this, TAG + "onFailure: " + cause, Toast.LENGTH_SHORT).show();
			}
		});
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
		if (!validateDept()) {
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
		String name = inputName.getText().toString();
		String jobname = inputCompany.getText().toString();
		String jobteam = inputJob.getText().toString();
		int isGraduate = 1;
		NetworkManager.getInstance().postDongneRegister(SignupActivity.this,
				email, password, name, mUnivId, mDeptId, mSpinnerItem, isGraduate, jobname, jobteam,
				new NetworkManager.OnResultListener<LoginInfo>() {
			@Override
			public void onSuccess(Request request, LoginInfo result) {
				if (result.error.equals(false)) {
					Toast.makeText(SignupActivity.this, "error:false" + result.toString(), Toast.LENGTH_SHORT).show();
					PropertyManager.getInstance().setUserId(result.user.user_id);
				} else {
					Toast.makeText(SignupActivity.this, "error:true" + result.toString(), Toast.LENGTH_SHORT).show();
					PropertyManager.getInstance().setUserId("");
				}
			}

			@Override
			public void onFailure(Request request, int code, Throwable cause) {

			}
		});



	}

	private void initData() {
		//서버에 요청해서 대학교 / 학과 리스트를 미리 받아서 리스트에 저장해둠.
		String[] univArray = getResources().getStringArray(R.array.univ);
		String[] deptArray = getResources().getStringArray(R.array.dept);
		String[] yearArray = getResources().getStringArray(R.array.spinner_year_item);
//		for (int i = 0; i < univArray.length; i++) {
//			mUnivAdapter.add(univArray[i]);
//		}
//		for (int i = 0; i < deptArray.length; i++) {
//			mDeptAdapter.add(deptArray[i]);
//		}
		for (int i = 0; i < yearArray.length; i++) {
			mySpinnerAdapter.add(yearArray[i]);
		}
		spinner.setSelection(60);
//		spinner.setDropDownVerticalOffset(100);

		getUniv();
		getDept(0);
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
		if (textViewDept.getText().toString().trim().isEmpty()) {
			textViewDept.setText("");
			requestFocus(textViewDept);
			return false;
		}
		return true;
	}
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

}
