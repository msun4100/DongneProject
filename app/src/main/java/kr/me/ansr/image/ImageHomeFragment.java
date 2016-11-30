package kr.me.ansr.image;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.List;

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CommonInfo;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.PhotoChangeDialogFragment;
import kr.me.ansr.image.upload.AndroidMultiPartEntity;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.model.Sns;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-18.
 */
public class ImageHomeFragment extends Fragment {
    private String TAG = ImageHomeFragment.class.getSimpleName();

    public static final int RC_NUM_PHOTO_CHANGE = 155;

    AppCompatActivity activity;
    private ImageView profileView;
    TextView changeIcon;
    private TextView textView1, textView3;

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    long totalSize = 0;

    //copied from ProfileSettingActivity
    ImageView iconPhoto;
    FriendsResult mItem;
//    TextView inputUnivName, inputEmail;
    TextView inputEnterYear, inputDept;
    CustomEditText inputComp, inputJob,inputFb, inputInsta, inputDesc1, inputDesc2;
    SwitchCompat sw;

    public static ImageHomeFragment newInstance() {
        ImageHomeFragment f = new ImageHomeFragment();
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle b = getArguments();
        if(b != null){
            mItem = (FriendsResult)b.getSerializable("mItem");
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_home, container, false);

        activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("ImageHomeFragment");
        //copied from ProfileSettingActivity
        textView1 = (TextView) v.findViewById(R.id.textView);
        textView3 = (TextView) v.findViewById(R.id.textView3);
        txtPercentage = (TextView) v.findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        profileView = (ImageView) v.findViewById(R.id.image_image_home_profile);
        changeIcon = (TextView) v.findViewById(R.id.text_prof_set_change);
        profileView.setOnClickListener(mListener);
        changeIcon.setOnClickListener(mListener);

        sw = (SwitchCompat) v.findViewById(R.id.sw_prof_set_location);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(getActivity(), "isChecked:"+ isChecked, Toast.LENGTH_SHORT).show();
                if(isChecked){
                    PropertyManager.getInstance().setUsingLocation(1);
                } else {
                    PropertyManager.getInstance().setUsingLocation(0);
                }
            }
        });


        inputEnterYear = (TextView) v.findViewById(R.id.image_home_default2);
        inputDept = (TextView) v.findViewById(R.id.image_home_dept);

        inputComp = (CustomEditText) v.findViewById(R.id.image_home_input1);
        inputJob = (CustomEditText) v.findViewById(R.id.image_home_input2);
        inputFb = (CustomEditText) v.findViewById(R.id.image_home_input3);
        inputInsta = (CustomEditText) v.findViewById(R.id.image_home_input4);
        inputDesc1 = (CustomEditText) v.findViewById(R.id.image_home_input5);
        inputDesc2 = (CustomEditText) v.findViewById(R.id.image_home_input6);

//        initUserInfo();   //resume에서 호출
        //file upload view settings
        filePath = getArguments().getString("filePath","");
        if(filePath == null || filePath ==""){
            if(mItem != null){
                if( !TextUtils.isEmpty(mItem.pic.large) && mItem.pic.large.equals("1") ){
                    String url = Config.FILE_GET_URL.replace(":userId", ""+mItem.userId).replace(":size", "large");
                    Glide.with(getActivity()).load(url)
                            .placeholder(R.drawable.e__who_icon)
                            .centerCrop()
                            .signature(new StringSignature(mItem.getUpdatedAt()))
                            .into(profileView);
                } else {
                    profileView.setImageResource(R.drawable.e__who_icon);
                }
            } else {
                profileView.setImageResource(R.drawable.e__who_icon);
            }
        } else {
            new UploadFileToServer().execute();
        }

        Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName("ProfileFragment");
        t.send(new HitBuilders.AppViewBuilder().build());
        return v;
    }
    public View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Tracker t = ((MyApplication)getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
            switch (v.getId()){
                default:
                    t.send(new HitBuilders.EventBuilder().setCategory("ProfileFragment").setAction("Press Button").setLabel("Photo view Click").build());
                    PhotoChangeDialogFragment mDialogFragment = PhotoChangeDialogFragment.newInstance();
                    Bundle b = new Bundle();
                    b.putSerializable("userInfo", mItem);
                    b.putString("tag", PhotoChangeDialogFragment.TAG_IMAGE_HOME);
                    mDialogFragment.setTargetFragment(ImageHomeFragment.this, RC_NUM_PHOTO_CHANGE);
                    mDialogFragment.setArguments(b);
                    mDialogFragment.show(getActivity().getSupportFragmentManager(), "photoChangeDialog");
                    break;

            }
        }
    };

    private void initUserInfo(){
        if (PropertyManager.getInstance().getUsingLocation() > 0){
            sw.setChecked(true);
        } else {sw.setChecked(false);}
        if(mItem != null){
            String stuId = String.valueOf(mItem.univ.get(0).getEnterYear());
            inputEnterYear.setText(""+stuId.substring(2, 4)+" 학번");
            inputDept.setText(mItem.getUniv().get(0).getDeptname());
            if (mItem.getJob() != null){
                inputComp.setText(mItem.getJob().getName());
                inputJob.setText(mItem.getJob().getTeam());
            }
            if(mItem.getSns() != null){
                for(int i=0; i<mItem.getSns().size(); i++){
                    if (i == 0){ inputFb.setText(mItem.getSns().get(0).url); }
                    if (i == 1){ inputInsta.setText(mItem.getSns().get(1).url); }
                }
            }

            if(mItem.getDesc() != null){
                for(int i=0; i< mItem.getDesc().size(); i++){
                    if (i == 0){ inputDesc1.setText(mItem.getDesc().get(i).toString()); }
                    if (i == 1){ inputDesc2.setText(mItem.getDesc().get(i).toString()); }
                }
            }
        }
    }

    ProgressDialog dialog = null;

    private void editUser(){
        final String dept = inputDept.getText().toString().trim();
        final String desc1 = inputDesc1.getText().toString();
        final String desc2 = inputDesc2.getText().toString();
        final String jobname = inputComp.getText().toString();
        final String jobteam = inputJob.getText().toString();
        final String fb = inputFb.getText().toString();
        final String insta = inputInsta.getText().toString();

        final String reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        Log.e(TAG, "editUser: "+reqDate );
        if( dept != null && TextUtils.isEmpty(dept)){
            Toast.makeText(getActivity(), "학과명은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        NetworkManager.getInstance().putDongnePutEditUser(getActivity(), reqDate, dept, desc1, desc2, jobname, jobteam, fb, insta, new NetworkManager.OnResultListener<CommonInfo>() {
            @Override
            public void onSuccess(Request request, CommonInfo result) {
                if (result.error.equals(true)) {
                    Toast.makeText(getActivity(), "회원정보 수정을 실패하였습니다. 다시 요청해주세요.", Toast.LENGTH_SHORT).show();
                    Log.e("error on edit:", result.message);
                } else {
                    mItem.getDesc().clear();
                    mItem.getDesc().add(desc1); mItem.getDesc().add(desc2);

                    mItem.getJob().setName(jobname); mItem.getJob().setTeam(jobteam);

                    mItem.getSns().clear();
                    if(fb != null) { mItem.getSns().add(new Sns("fb", fb)); }
                    if(insta != null) { mItem.getSns().add(new Sns("insta", insta)); }
                    mItem.updatedAt = reqDate;
                    PropertyManager.getInstance().setJobName(mItem.job.name);
                    PropertyManager.getInstance().setJobTeam(mItem.job.team);
                    mItem.univ.get(0).deptname = dept;
                    PropertyManager.getInstance().setDeptName(dept);
                    MediaStoreActivity.mItem = mItem;   //액티비티 닫힐때 리턴해주기 위해
//                    MediaStoreActivity.copyItem.updatedAt = MyApplication.getInstance().getCurrentTimeStampString();  //온백프레스용
//                    initUserInfo();   //수정이 성공하면 창이 닫힘.
                    Toast.makeText(getActivity(), "회원정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    ((MediaStoreActivity)getActivity()).finishAndReturnData();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(getActivity(), "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading....");
        dialog.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("ImageHome-mitem", mItem.toString());
        initUserInfo();
    }

    MenuItem menuNext;
    ImageView imageNext;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_f_board, menu);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        menuNext = menu.findItem(R.id.menu_board_write);
        imageNext = new ImageView(getActivity());
        imageNext.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageNext.setPadding(16, 0, 16, 0);
        imageNext.setImageResource(R.drawable.d_title_confirm_selector);
        imageNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MediaStoreActivity.tag.equals(MediaStoreActivity.TAG_FRIENDS_DETAIL)){
//                    editUser();
                } else if(MediaStoreActivity.tag.equals(MediaStoreActivity.TAG_MY_PAGE)){

                }
                editUser();
            }
        });
        menuNext.setActionView(imageNext);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            Toast.makeText(getActivity(), "filePath is: "+ filePath, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getActivity(), LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            getActivity().finish();
            ((MediaStoreActivity)getActivity()).finishAndCancel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            // updating textView1 value
//            textView1.setVisibility(View.INVISIBLE);
//            textView3.setVisibility(View.INVISIBLE);
//            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible and updates value
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setProgress(progress[0]);
//            // updating percentage value
//            txtPercentage.setVisibility(View.VISIBLE);
//            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            String userId = PropertyManager.getInstance().getUserId();
            String url = Config.FILE_UPLOAD_URL.replace(":userId", ""+userId);
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
                    //set Profile Property. "result":" 부터 마지막 "} 자르고
//                    int start = responseString.indexOf("\"result\":\"");
//                    String objectId = responseString.substring(start + "\"result\":\"".length(), responseString.length()-2 );
//                    PropertyManager.getInstance().setProfile(objectId);
                    try {
                        JSONObject obj = new JSONObject(responseString);
                        if (obj.getBoolean("error") == false) {
                            String resultUrl = obj.getString("result");
//                            mItem.updatedAt = obj.getString("message");
                            mItem.updatedAt = reqDate;  //마지막에 글라이드 호출할때 갱신해주기 위해
                            //리턴했을 때 이미지 갱신되도록
                            MediaStoreActivity.mItem.updatedAt = reqDate;
                            MediaStoreActivity.mItem.pic.small = "1";
                            MediaStoreActivity.mItem.pic.large = "1";
                            //백프레스를 할경우에도 이미지 갱신되도록
                            MediaStoreActivity.copyItem.updatedAt = reqDate;
                            MediaStoreActivity.copyItem.pic.small = "1";
                            MediaStoreActivity.copyItem.pic.large = "1";
                            PropertyManager.getInstance().setProfile(resultUrl);
                        } else { //when {"error": true, ..}
                            PropertyManager.getInstance().setProfile("");
                        }
                    } catch (JSONException e) {
                        PropertyManager.getInstance().setProfile("");
                        e.printStackTrace();
                    }
                } else {    //Server response error
                    responseString = "Error occurred! Http Status Code: " + statusCode;
                    PropertyManager.getInstance().setProfile("");
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
            Toast.makeText(getActivity(), "프로필 이미지가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            //mItem.updateAt 수정은 위에서 에러 false 검사에서 수행함. 여기서 하면 result json 파싱 또 해야 돼
            // showing the server response in an alert dialog
            String userId = PropertyManager.getInstance().getUserId();
            final String url = Config.FILE_GET_URL.replace(":userId", ""+userId).replace(":size", "large");
//            Glide.with(getActivity()).load(url)
//                    .placeholder(R.drawable.e__who_icon)
//                    .centerCrop()
//                    .signature(new StringSignature(mItem.getUpdatedAt()))
//                    .into(profileView);
            Glide.with(getActivity()).load(filePath)
                    .placeholder(R.drawable.e__who_icon)
                    .centerCrop()
                    .signature(new StringSignature( mItem.updatedAt ))
                    .into(profileView);

            super.onPostExecute(result);
        }
    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle extraBundle;
        switch (requestCode) {
            case RC_NUM_PHOTO_CHANGE:
                if (resultCode == getActivity().RESULT_OK && data != null) {
                    extraBundle = data.getExtras();
                    String next = extraBundle.getString("next");
                    if(next != null)
                        nextProcess(next);
                }
                break;

        }
    }

    private void nextProcess(String next){
        switch (next){
            case "_START_ALBUM_":
                ((MediaStoreActivity)getActivity()).startFishBunAlbum();
                break;
            case "_DEFAULT_IMG_":
                deletePic();
                break;
            default:break;
        }
    }

    private void deletePic(){
        if(mItem.pic.small.equals("0") && mItem.pic.large.equals("0")){
            Log.e(TAG, "deletePic: "+ "This user has already default img");
            return;
        }
        final String userId = PropertyManager.getInstance().getUserId();
        final String reqDate = MyApplication.getInstance().getCurrentTimeStampString();
        Log.e(TAG, "deletePic: "+reqDate );
        NetworkManager.getInstance().deleteDongnePicUser(getActivity(), reqDate, Integer.parseInt(userId), new NetworkManager.OnResultListener<CommonInfo>() {
            @Override
            public void onSuccess(Request request, CommonInfo result) {
                if (result.error.equals(true)) {
                    Toast.makeText(getActivity(), "기본 이미지 설정에 실패하였습니다. 다시 요청해주세요.", Toast.LENGTH_SHORT).show();
                    Log.e("error on edit:", result.message);
                } else {
                    mItem.updatedAt = reqDate;
                    mItem.pic.small = "0";
                    mItem.pic.large = "0";
                    PropertyManager.getInstance().setProfile("");
                    MediaStoreActivity.mItem = mItem;   //액티비티 끌 때 리턴해주기 위해
                    //온백프레스드로 끄는 경우 카피아이템 리턴해주기 위해
                    MediaStoreActivity.copyItem.pic.small = "0";
                    MediaStoreActivity.copyItem.pic.large = "0";
                    MediaStoreActivity.copyItem.updatedAt = reqDate;
                    //===================================================
                    Toast.makeText(getActivity(), "프로필 사진이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    filePath = "";
                    profileView.setImageResource(R.drawable.e__who_icon);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Request request, int code, Throwable cause) {
                Toast.makeText(getActivity(), "onFailure cause:" + cause, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("기본 이미지 설정 중...");
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(getActivity()).reportActivityStart(getActivity());
    }

    @Override
    public void onStop() {
        GoogleAnalytics.getInstance(getActivity()).reportActivityStop(getActivity());
        super.onStop();
    }

}
