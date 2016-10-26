package kr.me.ansr.image;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

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

import kr.me.ansr.MyApplication;
import kr.me.ansr.NetworkManager;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CommonInfo;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.PhotoChangeFragment;
import kr.me.ansr.image.upload.AndroidMultiPartEntity;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.login.LoginActivity;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.model.Sns;
import okhttp3.Request;

/**
 * Created by KMS on 2016-07-18.
 */
public class ImageHomeFragment extends Fragment {
    private String TAG = ImageHomeFragment.class.getSimpleName();
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
    TextView inputUnivName, inputUnivDesc, inputEmail;
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
        changeIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "imageview clicked", Toast.LENGTH_SHORT).show();
                ((MediaStoreActivity)getActivity()).startFishBunAlbum();
            }
        });
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

        inputUnivName = (TextView) v.findViewById(R.id.image_home_default1);
        inputUnivDesc = (TextView) v.findViewById(R.id.image_home_default2);
        inputEmail = (TextView) v.findViewById(R.id.image_home_default3);

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
                String url = Config.FILE_GET_URL.replace(":userId", ""+mItem.userId).replace(":size", "small");
                Glide.with(getActivity()).load(url)
                        .placeholder(R.drawable.e__who_icon)
                        .centerCrop()
                        .signature(new StringSignature(mItem.getUpdatedAt()))
                        .into(profileView);
            } else {
                profileView.setImageResource(R.drawable.e__who_icon);
            }
        } else {
            new UploadFileToServer().execute();
        }

        return v;
    }

    private void initUserInfo(){
        if (PropertyManager.getInstance().getUsingLocation() > 0){
            sw.setChecked(true);
        } else {sw.setChecked(false);}
        if(mItem != null){
            inputUnivName.setText(PropertyManager.getInstance().getUnivName());
            String stuId = String.valueOf(mItem.univ.get(0).getEnterYear());
            inputUnivDesc.setText(mItem.getUniv().get(0).getDeptname()+ " / " + stuId.substring(2, 4));
            inputEmail.setText(mItem.getEmail());
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
        final String desc1 = inputDesc1.getText().toString();
        final String desc2 = inputDesc2.getText().toString();
        final String jobname = inputComp.getText().toString();
        final String jobteam = inputJob.getText().toString();
        final String fb = inputFb.getText().toString();
        final String insta = inputInsta.getText().toString();

        NetworkManager.getInstance().putDongnePutEditUser(getActivity(), desc1, desc2, jobname, jobteam, fb, insta, new NetworkManager.OnResultListener<CommonInfo>() {
            @Override
            public void onSuccess(Request request, CommonInfo result) {
                if (result.error.equals(true)) {
                    Toast.makeText(getActivity(), "회원정보 수정을 실패하였습니다. 다시 요청해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    mItem.getDesc().clear();
                    mItem.getDesc().add(desc1); mItem.getDesc().add(desc2);

                    mItem.getJob().setName(jobname); mItem.getJob().setTeam(jobteam);

                    mItem.getSns().clear();
                    if(fb != null) { mItem.getSns().add(new Sns("fb", fb)); }
                    if(insta != null) { mItem.getSns().add(new Sns("insta", insta)); }

                    MediaStoreActivity.mItem = mItem;   //액티비티 닫힐때 리턴해주기 위해
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
            getActivity().finish();
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
                            String objectId = obj.getString("result");
                            PropertyManager.getInstance().setProfile(objectId);
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
            // showing the server response in an alert dialog
            String userId = PropertyManager.getInstance().getUserId();
            final String url = Config.FILE_GET_URL.replace(":userId", ""+userId).replace(":size", "small");
//                Glide.with(getActivity()).load(url).into(profileView);
            profileView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(getActivity()).load(url)
                            .placeholder(R.drawable.e__who_icon)
                            .centerCrop()
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis() / (24 * 60 * 60 * 1000))))
                            .into(profileView);
                }
            },1000);
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

}
