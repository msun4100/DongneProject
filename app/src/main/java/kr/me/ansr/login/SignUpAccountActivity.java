package kr.me.ansr.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CommonInfo;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-11.
 */

public class SignUpAccountActivity extends AppCompatActivity {

    private String TAG = SignUpAccountActivity.class.getSimpleName();
    private EditText inputPw, inputPwConfirm, inputEmail;
    private TextInputLayout inputLayoutPw, inputLayoutPwConfirm, inputLayoutEmail;
    private Button btnEnter;

    private TextView textConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.common_back_selector);
        toolbar.setBackgroundResource(R.drawable.a_join_titlebar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        inputLayoutPw = (TextInputLayout) findViewById(R.id.input_layout_pw);
        inputLayoutPwConfirm = (TextInputLayout) findViewById(R.id.input_layout_pw_confirm);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);

        inputPw = (EditText) findViewById(R.id.input_pw);
        inputPwConfirm = (EditText) findViewById(R.id.input_pw_confirm);
        inputEmail = (EditText) findViewById(R.id.input_email);

        btnEnter = (Button) findViewById(R.id.btn_enter);
        textConfirm = (TextView) findViewById(R.id.text_signup_account_confirm);

        inputPw.addTextChangedListener(new MyTextWatcher(inputPw));
        inputPwConfirm.addTextChangedListener(new MyTextWatcher(inputPwConfirm));
        inputPwConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                login();
                runNextStep();
            }
        });
        textConfirm.setVisibility(View.GONE);
        Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName(getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    ProgressDialog dialog = null;
    /**
     * logging in user. Will make http post request with name, email
     * as parameters
     */
    private void runNextStep(){
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword(1)) {
            return;
        }
        if (!validatePassword(2)) {
            return;
        }
        if(isConfirmPassword() == true){
            String email = inputEmail.getText().toString();
            NetworkManager.getInstance().postDongneUserEmail(SignUpAccountActivity.this, email, new NetworkManager.OnResultListener<CommonInfo>() {
                @Override
                public void onSuccess(Request request, CommonInfo result) {
                    if (result.error.equals(true)) {
                        Toast.makeText(SignUpAccountActivity.this, TAG + "result.error:" + result.message, Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = result.message.toString();
                        Intent intent = new Intent(SignUpAccountActivity.this, SignupActivity.class);
                        if(msg.equals("DUP_EMAIL")){
                            Toast.makeText(SignUpAccountActivity.this, "회원가입 된 이메일입니다.", Toast.LENGTH_SHORT).show();
                            inputLayoutEmail.setError("이메일을 확인해주세요.");
                            requestFocus(inputEmail);
                        } else if(msg.equals("AVAILABLE_EMAIL")){
//                            inputPw.setText("");
//                            inputPwConfirm.setText("");
//                            inputEmail.setText("");
                            intent.putExtra("email", inputEmail.getText().toString());
                            intent.putExtra("password", inputPwConfirm.getText().toString());
                            startActivity(intent);
                        }
                    }
                    dialog.dismiss();
                }
                @Override
                public void onFailure(Request request, int code, Throwable cause) {
                    Toast.makeText(SignUpAccountActivity.this, "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            dialog = new ProgressDialog(SignUpAccountActivity.this);
            dialog.setTitle("Loading....");
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), "pw confirm error", Toast.LENGTH_SHORT).show();
        }


    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating name
    private boolean validatePassword(int step) {
        if(step == 1){
            if (inputPw.getText().toString().isEmpty()) {
                inputLayoutPw.setError(getString(R.string.err_msg_pw));
                requestFocus(inputPw);
                return false;
            } else {
                inputLayoutPw.setErrorEnabled(false);
            }
        } else if(step == 2){
            if (inputPwConfirm.getText().toString().isEmpty()) {
                textConfirm.setVisibility(View.GONE);
                inputLayoutPwConfirm.setError(getString(R.string.err_msg_pw_confirm));
                requestFocus(inputPwConfirm);
                return false;
            } else if(!isConfirmPassword()){
                inputLayoutPwConfirm.setError(getString(R.string.err_msg_pw_confirm));
                inputLayoutPwConfirm.setErrorEnabled(false);
                requestFocus(inputPwConfirm);
                return false;
            } else {
                inputLayoutPwConfirm.setErrorEnabled(false);
            }
        }
        return true;
    }
    private boolean isConfirmPassword(){
        String pw1 = inputPw.getText().toString();
        String pw2 = inputPwConfirm.getText().toString();
        if(!pw1.equals(pw2)){
            textConfirm.setVisibility(View.VISIBLE);
//            textConfirm.setTextColor(R.color.invalid_input);
            textConfirm.setTextColor(ContextCompat.getColor(this, android.support.design.R.color.design_textinput_error_color_light));
            textConfirm.setText(getString(R.string.err_msg_pw_confirm));
//            Toast.makeText(getApplicationContext(), "같지 않음", Toast.LENGTH_SHORT).show();
            return false;
        }
        textConfirm.setVisibility(View.VISIBLE);
        textConfirm.setTextColor(ContextCompat.getColor(this, R.color.valid_input));
//        textConfirm.setTextColor(0xffEA80FC);
        textConfirm.setText("입력하신 비밀번호가 일치합니다.");
        return true;
    }
    // Validating email
    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
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
                case R.id.input_pw:
                    validatePassword(1);
                    break;
                case R.id.input_pw_confirm:
                    validatePassword(2);
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }
}
