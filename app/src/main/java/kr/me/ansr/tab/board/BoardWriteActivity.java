package kr.me.ansr.tab.board;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomDialogFragment;
import okhttp3.Request;

public class BoardWriteActivity extends AppCompatActivity {

    private final static String TAG = BoardWriteActivity.class.getSimpleName();
    public static final String TYPE_ANONYMOUS = ""+0;
    public static final String TYPE_PUBLIC = ""+1;
    public String currentTab = null;

    EditText inputBody;
    LinearLayout addImageView;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            currentTab = intent.getStringExtra("putkey");
            getSupportActionBar().setTitle(currentTab);
        }
        checkBox = (CheckBox)findViewById(R.id.check_write_anonymous1);
        inputBody = (EditText)findViewById(R.id.edit_write_input);
        addImageView = (LinearLayout)findViewById(R.id.linear_write_add_image_layout);
        addImageView.setOnClickListener(mListener);
        Button btn = (Button)findViewById(R.id.btn_write_send);
        btn.setOnClickListener(mListener);

    }


    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_write_send:
                    Toast.makeText(BoardWriteActivity.this, "btn click", Toast.LENGTH_SHORT).show();
                    postWrite();
                    break;
                case R.id.linear_write_add_image_layout:
                    Toast.makeText(BoardWriteActivity.this, "add image view click", Toast.LENGTH_SHORT).show();

                    break;
                default:
                break;
            }
        }
    };

    private void postWrite(){
        if (!validateBody()) {
            Toast.makeText(BoardWriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String type = getBoardType();
        //임시 pageId == univId
        int pageId = Integer.valueOf(PropertyManager.getInstance().getUserId());
        String title = "Title";
        String content = inputBody.getText().toString();
        NetworkManager.getInstance().postDongneBoardWrite(BoardWriteActivity.this, pageId, type, title, content, new NetworkManager.OnResultListener<WriteInfo>() {
            @Override
            public void onSuccess(Request request, WriteInfo result) {
                if(result.error.equals(false)){
                    Log.e(TAG+" result.message: ", result.message);
                    finishAndReturnData(true);
                } else {
                    Toast.makeText(BoardWriteActivity.this, "error: true\n" + result.message, Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(BoardWriteActivity.this, "onFailure: " + cause, Toast.LENGTH_LONG).show();
            }
        });
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading....");
        dialog.show();
    }
    ProgressDialog dialog = null;

    private String getBoardType(){
        if(checkBox.isChecked()){
            return TYPE_ANONYMOUS; //체크했으면 비공개 type == "0"
        } else {
            return TYPE_PUBLIC;
        }
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    // Validating name
    private boolean validateBody() {
        if (inputBody.getText().toString().trim().isEmpty()) {
//				inputLayoutPw.setError(getString(R.string.err_msg_pw));
            inputBody.setText("");
            requestFocus(inputBody);
            return false;
        }
        return true;
    }



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home){
            CustomDialogFragment mDialogFragment = new CustomDialogFragment();
            Bundle b = new Bundle();
            b.putString("tag", CustomDialogFragment.TAG_BOARD_WRITE);
            b.putString("title","게시물 작성을 취소 하시겠습니까?");
            b.putString("body", "작성중이던 글은 삭제됩니다.");
            mDialogFragment.setArguments(b);
            mDialogFragment.show(getSupportFragmentManager(), "customDialog");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    private void finishAndReturnData(boolean result){

        Intent intent = new Intent();
        if(result){
            intent.putExtra("return", "success");
        } else {
            intent.putExtra("return", "false");
        }
        this.setResult(RESULT_OK, intent);//RESULT_OK를 BoardFragment의 온리절트에서 받음
        finish();
    }
    private void forcedFinish(){
        finish();
    }

    public void nextProcess(){
        Toast.makeText(MyApplication.getContext(), "next Process", Toast.LENGTH_SHORT).show();
        forcedFinish();
    }

}
