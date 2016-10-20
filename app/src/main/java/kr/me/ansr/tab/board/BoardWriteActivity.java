package kr.me.ansr.tab.board;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomDialogFragment;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.tab.board.one.BoardResult;
import okhttp3.Request;

public class BoardWriteActivity extends AppCompatActivity implements IDataReturned{

    private final static String TAG = BoardWriteActivity.class.getSimpleName();
    public static final int BOARD_WRITE_RC_NEW = 100;
    public static final int BOARD_WRITE_RC_EDIT = 101;

    public static final String TYPE_ANONYMOUS = ""+0;
    public static final String TYPE_PUBLIC = ""+1;
    public String currentTab = null;
    public String type = null;
    public BoardResult mItem = null;
    TextView toolbarTitle;


    EditText inputBody;
    LinearLayout addImageView;
    CheckBox checkBox;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.e__cancle_2);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title); toolbarTitle.setText("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.e__titlebar_3));

        Intent intent = getIntent();
        if (intent != null) {
            currentTab = intent.getStringExtra("currentTab");
            type = intent.getStringExtra("type");   //안씀
            mItem = (BoardResult) intent.getSerializableExtra("mItem");
            if(currentTab == null || type == null)
                return;
        }
        checkBox = (CheckBox)findViewById(R.id.check_write_anonymous1);
        inputBody = (EditText)findViewById(R.id.edit_write_input);
        addImageView = (LinearLayout)findViewById(R.id.linear_write_add_image_layout);
        addImageView.setOnClickListener(mListener);
        btn = (Button)findViewById(R.id.btn_write_send);
        btn.setOnClickListener(mListener);

        if(mItem != null && type != null && type.equals("edit")){
            initData();
        }
    }

    private void initData(){
        if(mItem.type.equals("00") || mItem.type.equals("10")){
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        checkBox.setEnabled(false);
        inputBody.setText(mItem.body);
        btn.setText("수 정 하 기");

    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_write_send:
                    switch (type){
                        case "new":
//                            if(filePath != null){
//                                upload();
//                            }
                            postWrite();
                            break;
                        case "edit":
                            putWrite();
                            break;
                    }
                    break;
                case R.id.linear_write_add_image_layout:
                    Toast.makeText(BoardWriteActivity.this, "add image view click", Toast.LENGTH_SHORT).show();

                    break;
                default:
                break;
            }
        }
    };
    private void putWrite(){
        if (!validateBody()) {
            Toast.makeText(BoardWriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String type = getBoardType();
        Log.e("postWriteType:", type);
        //임시 pageId == univId
        int pageId = Integer.valueOf(PropertyManager.getInstance().getUserId());
        int boardId = mItem.boardId;
        String title = "Title";
        String content = inputBody.getText().toString();
        NetworkManager.getInstance().putDongneBoardWrite(BoardWriteActivity.this, pageId, boardId,type, title, content, new NetworkManager.OnResultListener<WriteInfo>() {
            @Override
            public void onSuccess(Request request, WriteInfo result) {
                if(result.error.equals(false)){
                    Log.e(TAG+" result.message: ", result.message);
                    mItem = result.result;
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

    private void postWrite(){
        if (!validateBody()) {
            Toast.makeText(BoardWriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String type = getBoardType();
        Log.e("postWriteType:", type);
        int pageId = Integer.valueOf(PropertyManager.getInstance().getUserId()); //임시 pageId == univId
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
        String type = currentTab;
        if(checkBox.isChecked()){
            type += TYPE_ANONYMOUS; //체크했으면 비공개 type == "0"
        } else {
            type += TYPE_PUBLIC;
        }
        return type;
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
//            CustomDialogFragment mDialogFragment = new CustomDialogFragment();
            CustomDialogFragment mDialogFragment = CustomDialogFragment.newInstance();
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
            intent.putExtra("mItem", mItem);
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

    @Override
    public void onDataReturned(String choice) {
        if(choice != null){
            switch (choice){
                case "close":
                    forcedFinish();
                    break;
            }
        }
    }
}
