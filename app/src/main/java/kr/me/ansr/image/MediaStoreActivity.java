package kr.me.ansr.image;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.R;
import kr.me.ansr.image.glide.ImageItem;
import kr.me.ansr.image.glide.SlideshowDialogFragment;
import kr.me.ansr.login.LoginActivity;

public class MediaStoreActivity extends AppCompatActivity{
    private static final String TAG = MediaStoreActivity.class.getSimpleName();
    private final int ALBUM_PICKER_COUNT = 3;
    ArrayList<ImageItem> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_store);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
                if(images.size() > 0 ){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", images);
                    bundle.putInt("position", 4);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                } else {
                    Toast.makeText(MediaStoreActivity.this, "Library 버튼을 통해 이미지를 가져올 것", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        getSupportLoaderManager().initLoader(0, null ,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    List<String> path = data.getStringArrayListExtra(Define.INTENT_PATH);
                    Toast.makeText(this, "path : " + path, Toast.LENGTH_LONG).show();
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
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                    break;
                }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if(id == android.R.id.home){
            Intent intent = new Intent(MediaStoreActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
