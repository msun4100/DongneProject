package kr.me.ansr.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import kr.me.ansr.NetworkManager;
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
	String email, password;
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

		textViewDept = (AutoCompleteTextView)findViewById(R.id.auto_text_signup_dept);
		mDeptAdapter = new MyDeptAdapter();
//		mDeptAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1);
		textViewDept.setAdapter(mDeptAdapter);

		spinner = (Spinner) findViewById(R.id.spinner);
		mySpinnerAdapter = new MySpinnerAdapter();
		spinner.setAdapter(mySpinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(SignupActivity.this, "Selected : " + position, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		inputName = (EditText)findViewById(R.id.edit_signup_name);
		inputCompany = (EditText)findViewById(R.id.edit_signup_company);
		inputJob = (EditText)findViewById(R.id.edit_signup_job);
		Button btn = (Button)findViewById(R.id.btn_signup_send);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),"btn_send", Toast.LENGTH_SHORT).show();
				getDept(1);
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
						Toast.makeText(SignupActivity.this, TAG + "result:" + result.result, Toast.LENGTH_SHORT).show();
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
						Toast.makeText(SignupActivity.this, TAG + "result:" + result.result, Toast.LENGTH_SHORT).show();
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
//		String
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

}
