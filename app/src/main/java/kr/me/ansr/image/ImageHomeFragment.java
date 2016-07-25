package kr.me.ansr.image;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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

import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.image.upload.AndroidMultiPartEntity;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.login.LoginActivity;

/**
 * Created by KMS on 2016-07-18.
 */
public class ImageHomeFragment extends Fragment {
    private String TAG = ImageHomeFragment.class.getSimpleName();
    AppCompatActivity activity;
    private ImageView profileView;
    private TextView textView1, textView3;
    private Button btn;

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    long totalSize = 0;

    public static ImageHomeFragment newInstance() {
        ImageHomeFragment f = new ImageHomeFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_home, container, false);

        activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("ImageHomeFragment");
        textView1 = (TextView) v.findViewById(R.id.textView);
        textView3 = (TextView) v.findViewById(R.id.textView3);
        txtPercentage = (TextView) v.findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        profileView = (ImageView) v.findViewById(R.id.image_image_home_profile);
        profileView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "imageview clicked", Toast.LENGTH_SHORT).show();
                ((MediaStoreActivity)getActivity()).startAlbum();
            }
        });

        btn = (Button) v.findViewById(R.id.btn_image_home_confirm);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "filePath is: "+ filePath, Toast.LENGTH_SHORT).show();
            }
        });

        //file upload view settings
        filePath = getArguments().getString("filePath","");
        if(filePath == null || filePath ==""){
            profileView.setImageResource(R.drawable.ic_launcher);
        } else {
            new UploadFileToServer().execute();
        }

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
//    @Override
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
            Toast.makeText(getActivity(), "ImageHomeFragment home selected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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
//                entity.addPart("email", new StringBody("abc@gmail.com"));
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
                btn.setText("완료");
//                Glide.with(getActivity()).load(filePath).into(profileView);
                final String url = Config.FILE_GET_URL.replace(":userId", ""+1);
//                Glide.with(getActivity()).load(url).into(profileView);
                profileView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(url).into(profileView);
                    }
                },1500);
            } else {
                showAlert(result);
                txtPercentage.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                textView3.setVisibility(View.VISIBLE);
                textView3.setText("프로필 사진을 다시 등록해주세요.");
                btn.setText("확인");
                profileView.setImageResource(R.mipmap.ic_launcher);
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
