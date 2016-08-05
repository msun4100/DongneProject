package kr.me.ansr.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.me.ansr.MainActivity;
import kr.me.ansr.MyApplication;
import kr.me.ansr.R;
import kr.me.ansr.gcmchat.app.EndPoints;
import kr.me.ansr.gcmchat.model.User;

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
        setSupportActionBar(toolbar);
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
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                login();
                runNextStep();
            }
        });
        textConfirm.setVisibility(View.GONE);
    }

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
            Intent intent = new Intent(SignUpAccountActivity.this, SignupActivity.class);
            intent.putExtra("email", inputEmail.getText().toString());
            intent.putExtra("password", inputPwConfirm.getText().toString());
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "pw confirm error", Toast.LENGTH_SHORT).show();
        }


    }
    private void login() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword(1)) {
            return;
        }
        if (!validatePassword(2)) {
            return;
        }

        final String name = inputPw.getText().toString();
        final String email = inputEmail.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                EndPoints.LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "response: " + response);

                try {
                    JSONObject obj = new JSONObject(response);

                    // check for error flag
                    if (obj.getBoolean("error") == false) {
                        // user successfully logged in

                        JSONObject userObj = obj.getJSONObject("user");
                        User user = new User(userObj.getString("user_id"),
                                userObj.getString("name"),
                                userObj.getString("email"));

                        // storing user in shared preferences
                        MyApplication.getInstance().getPrefManager().storeUser(user);

                        // start main activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    } else {
                        // login error - simply toast the message
                        Toast.makeText(getApplicationContext(), "" + obj.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "json parsing error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "Volley error: " + error.getMessage() + ", code: " + networkResponse);
                Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);

                Log.e(TAG, "params: " + params.toString());
                return params;
            }
        };

        //Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating name
    private boolean validatePassword(int step) {
        if(step == 1){
            if (inputPw.getText().toString().trim().isEmpty()) {
                inputLayoutPw.setError(getString(R.string.err_msg_pw));
                requestFocus(inputPw);
                return false;
            } else {
                inputLayoutPw.setErrorEnabled(false);
            }
        } else if(step == 2){
            if (inputPwConfirm.getText().toString().trim().isEmpty()) {
                textConfirm.setVisibility(View.GONE);
                inputLayoutPwConfirm.setError(getString(R.string.err_msg_pw_confirm));
                requestFocus(inputPwConfirm);
                return false;
            } else if(!isConfirmPassword()){
                inputLayoutPwConfirm.setError("Invalid password");
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
            textConfirm.setTextColor(0xfff14249);
            textConfirm.setText("Invalid Password");
//            Toast.makeText(getApplicationContext(), "같지 않음", Toast.LENGTH_SHORT).show();
            return false;
        }
        textConfirm.setVisibility(View.VISIBLE);
        textConfirm.setTextColor(0xffEA80FC);
        textConfirm.setText("valid Password");
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

}
