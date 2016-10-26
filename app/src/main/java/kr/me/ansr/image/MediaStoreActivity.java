package kr.me.ansr.image;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.R;
import kr.me.ansr.common.PhotoChangeFragment;
import kr.me.ansr.tab.friends.model.FriendsInfo;
import kr.me.ansr.tab.friends.model.FriendsResult;

public class MediaStoreActivity extends AppCompatActivity{
    private static final String TAG = MediaStoreActivity.class.getSimpleName();
    public static final String TAG_FRIENDS_DETAIL= "friendsDetail";
    public static final String TAG_MY_PAGE= "myPage";

    private static final String F1_TAG = "tab1";
    private static final String F2_TAG = "tab2";
    private final int ALBUM_PICKER_COUNT = 1;
    ArrayList<ImageItem> images;

    //using for toolbar menu and background
    TextView toolbarTitle; ImageView iconPhoto, toolbarMenu;
    public static FriendsResult mItem;
    public static String tag = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.c_artboard_final_title_cancel);
        toolbar.setBackgroundResource(R.drawable.c_artboard_final_setting_title1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle = (TextView)toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("");
        toolbarMenu = (ImageView)toolbar.findViewById(R.id.toolbar_menu1);
        toolbarMenu.setImageResource(R.drawable.d_title_confirm_selector);
        Intent intent = getIntent();
        if (intent != null) {
            mItem = (FriendsResult)intent.getSerializableExtra("mItem");
            tag = intent.getStringExtra("tag");
            if(mItem == null) forcedFinish();
        } else {
            forcedFinish();
        }
        toolbarMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MediaStoreActivity.this, "toolbar_menu", Toast.LENGTH_SHORT).show();
            }
        });
        toolbarMenu.setVisibility(View.GONE);
//이하 합치기 전 코드 들
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
                        .setAlbumThumnaliSize(120)//you can resize album thumnail size
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

    public void startFishBunAlbum(){
        FishBun.with(MediaStoreActivity.this)
                .setAlbumThumnaliSize(150)//you can resize album thumnail size
                .setPickerCount(ALBUM_PICKER_COUNT)//you can restrict photo count
                .startAlbum();
    }
    private void init(){
        Bundle bundle = new Bundle();
        bundle.putString("filePath", "");
        bundle.putSerializable("mItem", mItem);
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
                }
                break;
            case RC_SELECT_PROFILE_CODE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "in case RC_RESULT_PROFILE_CODE\n"+data.getExtras(), Toast.LENGTH_LONG).show();
                    String filePath = data.getStringExtra("filePath");
                    Toast.makeText(this, "filePath : " + filePath, Toast.LENGTH_LONG).show();

                    Bundle bundle = new Bundle();
                    bundle.putString("filePath", filePath);

                    callImageHomeFragment(bundle);
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empty_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
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

    private void forcedFinish(){
        Log.e("mItem", "is null");
        finish();
    }

    public void finishAndReturnData(){
        Intent intent = new Intent();
        intent.putExtra("mItem", mItem);
        setResult(RESULT_OK, intent);
        finish();
    }

}
