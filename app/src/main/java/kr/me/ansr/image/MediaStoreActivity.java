package kr.me.ansr.image;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.R;

public class MediaStoreActivity extends AppCompatActivity{
    private static final String TAG = MediaStoreActivity.class.getSimpleName();
    private static final String F1_TAG = "tab1";
    private static final String F2_TAG = "tab2";
    private final int ALBUM_PICKER_COUNT = 3;
    ArrayList<ImageItem> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        images = new ArrayList<>();
        images.clear();
        Button btn = (Button)findViewById(R.id.btn_media_store_select);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MediaStoreActivity.this, "Library 버튼을 통해 이미지를 가져올 것", Toast.LENGTH_SHORT).show();
            }
        });

        btn = (Button)findViewById(R.id.btn_media_store_library);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FishBun.with(MediaStoreActivity.this)
                        .setAlbumThumnaliSize(150)//you can resize album thumnail size
                        .setPickerCount(ALBUM_PICKER_COUNT)//you can restrict photo count
                        .startAlbum();
            }
        });

        btn = (Button)findViewById(R.id.btn_media_store_glide);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        } else {
            init();
        }
    }   //onCreate

    public void startAlbum(){
        FishBun.with(MediaStoreActivity.this)
                .setAlbumThumnaliSize(150)//you can resize album thumnail size
                .setPickerCount(ALBUM_PICKER_COUNT)//you can restrict photo count
                .startAlbum();
    }
    private void init(){
        Bundle bundle = new Bundle();
        bundle.putString("filePath", "");
//        bundle.putSerializable("images", images);
//        bundle.putInt("position", 0);
        callImageHomeFragment(bundle);
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

    public static final int RC_SELECT_PROFILE_CODE = 111;
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
                    }
                    //==================
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", images);
                    bundle.putInt("position", 0);

                    callSlideshowFragment(bundle);
                    break;
                }
            case RC_SELECT_PROFILE_CODE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "in case RC_RESULT_PROFILE_CODE\n"+data.getExtras(), Toast.LENGTH_LONG).show();
                    String filePath = data.getStringExtra("filePath");
                    Toast.makeText(this, "filePath : " + filePath, Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("filePath", filePath);

                    callImageHomeFragment(bundle);
                    break;
                }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
//        if(id == android.R.id.home){
//            Intent intent = new Intent(MediaStoreActivity.this, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        }

        return super.onOptionsItemSelected(item);
    }

    public void callImageHomeFragment(Bundle bundle){
        Fragment f = getSupportFragmentManager().findFragmentByTag(F1_TAG);
        if (f == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ImageHomeFragment newFragment = ImageHomeFragment.newInstance();
            newFragment.setArguments(bundle);
            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
            ft.replace(R.id.containerforimage, newFragment, F1_TAG);
            ft.commit();
        }
    }

    public void callSlideshowFragment(Bundle bundle){
        Fragment f = getSupportFragmentManager().findFragmentByTag(F2_TAG);
        if (f == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            SlideshowFragment newFragment = SlideshowFragment.newInstance();
            newFragment.setArguments(bundle);
            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
            ft.replace(R.id.containerforimage, newFragment, F2_TAG);
            ft.commit();
        }
    }
}
