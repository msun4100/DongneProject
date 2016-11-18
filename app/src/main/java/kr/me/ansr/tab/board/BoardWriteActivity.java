package kr.me.ansr.tab.board;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomDialogFragment;
import kr.me.ansr.common.IDataReturned;
import kr.me.ansr.image.ImageItem;
import kr.me.ansr.image.SlideshowFragment;
import kr.me.ansr.image.upload.AndroidMultiPartEntity;
import kr.me.ansr.image.upload.Config;
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
    private ProgressBar progressBar;
    private TextView txtPercentage;
    long totalSize = 0;

    ImageView bodyImage, removeImage;
    View rootView;

    ScrollView mScrollView;
    LinearLayout inputLayout;
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

        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mScrollView = (ScrollView)  findViewById(R.id.scrollView) ;
        bodyImage = (ImageView) findViewById(R.id.image_write_body);
        bodyImage.setVisibility(View.GONE);
        removeImage = (ImageView) findViewById(R.id.image_write_remove);
        removeImage.setVisibility(View.GONE);
        removeImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(bodyImage.getVisibility() == View.VISIBLE){
                    bodyImage.setVisibility(View.GONE);
                    removeImage.setVisibility(View.GONE);

                    if(mItem != null && mItem.pic.size() > 0){
                        mItem.pic.remove(0);
                    }
                }
            }
        });
        inputLayout = (LinearLayout) findViewById(R.id.LinearLayout2);
//        inputLayout.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//                imm.showSoftInputFromInputMethod(inputBody.getApplicationWindowToken(),InputMethodManager.SHOW_FORCED);
//                Log.d(TAG, "onClick: ");
//            }
//        });
        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
        images = new ArrayList<>();
        images.clear();
        if(mItem != null && type != null && type.equals("edit")){
            initData();
        }

        rootView = (View)findViewById(R.id.rootView);
        setupParent(rootView);
    }

    protected void setupParent(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }
        //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(innerView);
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void initData(){
        Log.e(TAG, "initData: "+mItem.toString() );
        if(mItem.type.equals("00") || mItem.type.equals("10")){
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        checkBox.setEnabled(false);
        inputBody.setText(mItem.body);
        btn.setText("수 정 하 기");
        if(mItem.pic.size() > 0){
            bodyImage.setVisibility(View.VISIBLE);
            String url = Config.BOARD_FILE_GET_URL.replace(":imgKey", ""+mItem.pic.get(0));
            Glide.with(this).load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(final GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mScrollView.post(new Runnable() {
                                public void run() {
                                    mScrollView.scrollTo(0, Config.resizeValue / 2);
                                }
                            });
                            return false;
                        }
                    })
                    .override(Config.resizeValue, Config.resizeValue)
                    .signature(new StringSignature(mItem.updatedAt)).into(bodyImage);
            removeImage.setVisibility(View.VISIBLE);
        } else {
            bodyImage.setVisibility(View.GONE);
            removeImage.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_write_send:
                    switch (type){
                        case "new":
                            postWrite();
                            break;
                        case "edit":
                            putWrite();
                            break;
                    }
                    break;
                case R.id.linear_write_add_image_layout:
                    startFishBunAlbum();
//                    if(mItem == null || mItem.pic.get(0).isEmpty()){
//                        startFishBunAlbum();
//                    } else {
//                        Toast.makeText(BoardWriteActivity.this, "이미지 삭제 후 이용해주세요.", Toast.LENGTH_SHORT).show();
//                    }
                    break;
                default:
                break;
            }
        }
    };
    private void putWrite(){
        if (!validateBody() && !validateFilePath() ) {
            Toast.makeText(BoardWriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String type = getBoardType();
        Log.e("putWriteType:", type);
        //임시 pageId == univId
        int pageId = Integer.valueOf(PropertyManager.getInstance().getUserId());
        int boardId = mItem.boardId;
        String title = "Title";
        String content = inputBody.getText().toString();
        String pic = null;
        if(mItem.pic.size() == 1){
            pic = mItem.pic.get(0);
        }
        NetworkManager.getInstance().putDongneBoardWrite(BoardWriteActivity.this, pageId, boardId,type, title, content, pic, new NetworkManager.OnResultListener<WriteInfo>() {
            @Override
            public void onSuccess(Request request, WriteInfo result) {
                if(result.error.equals(false)){
                    Log.e(TAG+" result.message: ", result.message);
//                    mItem = result.result;
                    mItem.body = result.result.body;
                    mItem.pic = result.result.pic;
                    mItem.updatedAt = result.result.updatedAt;

                    if(filePath != null){
                        imageUpload(""+result.result.boardId);
                    } else {
                        finishAndReturnData(true);
                    }
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
        if (!validateBody() && !validateFilePath() ) {
            Toast.makeText(BoardWriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (!validateBody()) {
//            Toast.makeText(BoardWriteActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
//            return;
//        }
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
                    if(filePath != null){
                        imageUpload(""+result.result.boardId);
                    } else {
                        finishAndReturnData(true);
                    }
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

    private boolean validateBody() {
        if (inputBody.getText().toString().trim().isEmpty()) {
//				inputLayoutPw.setError(getString(R.string.err_msg_pw));
            inputBody.setText("");
            requestFocus(inputBody);
            return false;
        }
        return true;
    }

    private boolean validateFilePath() {
        if (filePath == null || filePath.equals("")) {
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
            if(mItem != null){
//                _EDIT_ 모드 일때만 로그 찍히게 postWrite 일땐 어차피 리스트 갱신이라 mItem 리턴을 안함.
                Log.e(TAG, "finishAndReturnData: " + mItem.toString() );
            }
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


    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    private static final int ALBUM_PICKER_COUNT = 1;
    public void startFishBunAlbum(){
        FishBun.with(BoardWriteActivity.this)
                .setAlbumThumnaliSize(150)//you can resize album thumnail size
//                .setActionBarColor(getResources().getColor(R.color.pressed), getResources().getColor(R.color.pressed))
                .setPickerCount(ALBUM_PICKER_COUNT)//you can restrict photo count
                .startAlbum();
    }


    ArrayList<ImageItem> images;
    public String filePath = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    List<String> path = data.getStringArrayListExtra(Define.INTENT_PATH);
                    //==Custom codes====
                    images.clear();
                    for(int i=0; i<path.size(); i++){
                        ImageItem image = new ImageItem();
                        image.setName("path"+i);
                        image.setSmall(path.get(i));
                        image.setMedium(path.get(i));
                        image.setLarge(path.get(i));
                        image.setTimestamp("custom timeStamp "+i);
                        images.add(image);
                        Log.e("img:", ""+images.get(i));
                    }
                    //==================
                    filePath = path.get(0); //실제론 이 변수 사용
                    bodyImage.setVisibility(View.VISIBLE);
                    removeImage.setVisibility(View.VISIBLE);
                    Glide.with(this).load(filePath)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    mScrollView.post(new Runnable() {
                                        public void run() {
//                                            mScrollView.scrollTo(0, mScrollView.getBottom());
                                            Log.d(TAG, "run: "+mScrollView.getBottom());
                                            mScrollView.scrollTo(0, Config.resizeValue / 2);
                                        }
                                    });
                                    return false;
                                }
                            })
                            .override(Config.resizeValue, Config.resizeValue)
                            .into(bodyImage);
//                    mScrollView.post(new Runnable() {
//                        public void run() {
//                            mScrollView.scrollTo(0, mScrollView.getBottom());
//                        }
//                    });
                    if(mItem != null){
                        if(mItem.pic.size() == 0){
                            mItem.pic.add("board_"+mItem.boardId);
                        } else if(mItem.pic.size() == 1){
                            mItem.pic.remove(0);
                            mItem.pic.add("board_"+mItem.boardId);
                        }
                        Log.e("get(0)", mItem.pic.get(0));
                    }
                }
                break;

        }
    }

    private String returnBoardId = null;
    String imgKey = null;
    private void imageUpload(String boardId){
        returnBoardId = boardId;
        new UploadFileToServer().execute();
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible and updates value
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress[0]);
            // updating percentage value
            txtPercentage.setVisibility(View.VISIBLE);
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            String url = Config.BOARD_FILE_UPLOAD_URL.replace(":boardId", ""+returnBoardId);
            HttpPost httppost = new HttpPost(url);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                File sourceFile = new File(filePath);
                // Adding file data to http body
                entity.addPart("photo", new FileBody(sourceFile));
                // Extra parameters if you want to pass to server
                String reqDate = MyApplication.getInstance().getCurrentTimeStampString();
                entity.addPart("reqDate", new StringBody(reqDate));
//                entity.addPart("title", new StringBody("new title string"));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    try {
                        JSONObject obj = new JSONObject(responseString);
                        if (obj.getBoolean("error") == false) {
                            imgKey = obj.getString("result");
                        } else { //when {"error": true, ..}

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {    //Server response error
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            // showing the server response in an alert dialog
            finishAndReturnData(true);
//            final String url = Config.BOARD_FILE_GET_URL.replace(":imgKey", ""+imgKey);
//                Glide.with(getActivity()).load(url).into(profileView);
//                profileView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Glide.with(BoardWriteActivity.this).load(url)
//                                .placeholder(R.drawable.e__who_icon)
//                                .centerCrop()
//                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis() / (24 * 60 * 60 * 1000))))
//                                .into(profileView);
//                    }
//                },1500);
            super.onPostExecute(result);
        }
    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BoardWriteActivity.this);
        builder.setMessage(message).setTitle("Response from Server")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        Toast.makeText(getActivity(), "objectId: "+PropertyManager.getInstance().getProfile(),Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



}
