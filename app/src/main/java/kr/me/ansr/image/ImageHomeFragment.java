package kr.me.ansr.image;

import android.app.AlertDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.common.CustomEditText;
import kr.me.ansr.common.PhotoChangeFragment;
import kr.me.ansr.image.upload.AndroidMultiPartEntity;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.login.LoginActivity;
import kr.me.ansr.tab.friends.model.FriendsResult;

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
    CustomEditText inputUniv, inputComp, inputJob;
    CustomEditText inputEmail, inputFb;
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
//            tag = b.getString("tag", "0");
//            title = b.getString("title","default title");
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
                ((MediaStoreActivity)getActivity()).startAlbum();
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

        inputUniv = (CustomEditText) v.findViewById(R.id.image_home_input1);
        inputComp = (CustomEditText) v.findViewById(R.id.image_home_input2);
        inputJob = (CustomEditText) v.findViewById(R.id.image_home_input3);
        inputEmail = (CustomEditText) v.findViewById(R.id.image_home_input4);
        inputFb = (CustomEditText) v.findViewById(R.id.image_home_input5);

        initUserInfo();
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
        if (PropertyManager.getInstance().getUsingLocation() == 1){
            sw.setChecked(true);
        } else {sw.setChecked(false);}
        if(mItem != null){
            inputUniv.setText(PropertyManager.getInstance().getUnivName());
            inputComp.setText(mItem.getJob().getName());
            inputJob.setText(mItem.getJob().getTeam());
            inputEmail.setText(mItem.getEmail());
            inputFb.setText(mItem.getSns().get(0).url);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // destroy all menu and re-call onCreateOptionsMenu
//        getActivity().invalidateOptionsMenu();
//        Toast.makeText(getActivity(),"ImageHome onresume", Toast.LENGTH_SHORT).show();
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
            textView1.setVisibility(View.INVISIBLE);
            textView3.setVisibility(View.INVISIBLE);
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
            String url = Config.FILE_UPLOAD_URL.replace(":userId", ""+1);
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
            if(PropertyManager.getInstance().getProfile() != ""){
                showAlert(result);
                textView3.setVisibility(View.VISIBLE);
                textView3.setText("프로필 사진 등록 완료");
//                Glide.with(getActivity()).load(filePath).into(profileView);
                String userId = PropertyManager.getInstance().getUserId();
                final String url = Config.FILE_GET_URL.replace(":userId", ""+1).replace(":size", "small");
//                Glide.with(getActivity()).load(url).into(profileView);
                profileView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(url)
                                .placeholder(R.drawable.e__who_icon)
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis() / (24 * 60 * 60 * 1000))))
                                .into(profileView);
                    }
                },1500);
            } else {
                showAlert(result);
                txtPercentage.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                textView3.setVisibility(View.VISIBLE);
                textView3.setText("프로필 사진을 다시 등록해주세요.");
                profileView.setImageResource(R.drawable.e__who_icon);
            }
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
