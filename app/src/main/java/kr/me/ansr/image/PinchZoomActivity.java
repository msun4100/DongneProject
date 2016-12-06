package kr.me.ansr.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kr.me.ansr.MyApplication;
import kr.me.ansr.R;
import kr.me.ansr.image.model.PinchMetrics;
import kr.me.ansr.image.upload.Config;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PinchZoomActivity extends AppCompatActivity {
    private final String TAG = PinchZoomActivity.class.getSimpleName();

//    PhotoView mImageView;
    ImageView mImageView;
    ImageView exitView;
    PhotoViewAttacher mAttacher;
    int userId;
    String updatedAt;
    String url;
    ProgressBar progressBar;
    Bitmap bitmap;

    ArrayList<String> boardPics;

    // If you later call mImageView.setImageDrawable/setImageBitmap/setImageResource/etc then you just need to call
//    mAttacher.update();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinch_zoom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent != null) {
            bitmap = intent.getParcelableExtra("bitmap");
            userId = intent.getIntExtra("userId", -1);
            updatedAt = intent.getStringExtra("updatedAt");
//            boardPic = intent.getStringExtra("boardPic");
            boardPics = intent.getStringArrayListExtra("boardPics");
            if(userId == -1) {
                finish();
            }
            if(updatedAt == null){
                updatedAt = MyApplication.getInstance().getCurrentTimeStampString();
            }
            if(boardPics != null && boardPics.size() > 0){
                url = Config.BOARD_FILE_GET_URL.replace(":imgKey", ""+boardPics.get(0));
            } else {
                url = Config.FILE_GET_URL.replace(":userId", ""+userId).replace(":size", "large");
            }
        } else {
            finish();
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mImageView = (ImageView) findViewById(R.id.iv_photo); //        mImageView = (PhotoView) findViewById(R.id.iv_photo);
//        Drawable bitmap = getResources().getDrawable(R.drawable.ic_launcher);
//        mImageView.setImageDrawable(bitmap);
        mAttacher = new PhotoViewAttacher(mImageView);

        exitView = (ImageView) findViewById(R.id.iv_exit);
        exitView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Tracker t = ((MyApplication)getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        t.setScreenName("PinchZoomActivity");
        t.send(new HitBuilders.AppViewBuilder().build());

        if(bitmap != null){
            initBitmap();
        }else {
            initWithGlideV2();
//            initWithGlide();    //context 때문인가? --> getAppContext() 해도 마찬가지임 V2가 답
//            initWithPicasso();
        }

    }
    private void initBitmap(){
        progressBar.setVisibility(View.GONE);   //프로그레스바 안보임
        final PinchMetrics pm = getResizePixel();
        Log.e(TAG, "initBitmap: "+pm.toString() );
        mImageView.setImageBitmap(bitmap);
        mAttacher.update();
    }

    private void initWithPicasso(){
        if(url == null) return;
        progressBar.setVisibility(View.VISIBLE);
        final PinchMetrics pm = getResizePixel();
        Picasso.with(this)
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
//                .resize(pm.width, pm.height)
                .placeholder(R.drawable.e__who_icon)
                .error(R.drawable.e__who_icon)
                .into(mImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if(mAttacher!=null) {
                            mAttacher.update();
                        } else {
                            mAttacher = new PhotoViewAttacher(mImageView);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {
                        Toast.makeText(PinchZoomActivity.this, "이미지 로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initWithGlideV2(){
        if(url == null || mAttacher == null) return;
        progressBar.setVisibility(View.VISIBLE);
        final PinchMetrics pm = getResizePixel();
//        Log.e(TAG, "initWithGlideV2: pm"+pm.toString() );
        Glide.with(this)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)   //원본 사이즈 캐시
                .signature(new StringSignature(updatedAt))
                .into(new SimpleTarget<Bitmap>(pm.width, pm.height) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        // Do something with bitmap here.
                        mImageView.setImageBitmap(bitmap);
                        mAttacher.update();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void initWithGlide(){
        /*
        1. isFromMemoryCache == false 인 첫 호출에서는 이미지가 원본사이즈로 넘쳐 보이게 되는 버그가 있어서
        2. 피카소로 해결 --> 그러나 피카소는 캐시하는데 문제가 있음
        3. initWithGlideV2 와 같이 리스너가 아닌 asBitmap() + new SimpleTarget()을 통해 문제 해결
        */
        if(url == null) return;
        progressBar.setVisibility(View.VISIBLE);
        final PinchMetrics pm = getResizePixel();
        Glide.with(getApplicationContext()).load(url)
                .placeholder(R.drawable.e__who_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   //원본 사이즈 캐시
//                .centerCrop()
                .override(pm.width, pm.height)
                .signature(new StringSignature(updatedAt))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "onException: "+e.getMessage());
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Log.e(TAG, "onResourceReady: isFromMemoryCache " +isFromMemoryCache);   //true 일때 이미지가 안넘치네.. 첫호출땐 false라서 넘치는건가?
                        Log.e(TAG, "onResourceReady: isFirstResource " +isFirstResource);
                        progressBar.setVisibility(View.GONE);
                        if ( mAttacher != null ) {
                            mAttacher.update();
                        }
                        return false;   // return false only
                    }
                })
                .into(mImageView);
    }

    public PinchMetrics getResizePixel(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = 0;
        int height = 0;
        try {
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
            return new PinchMetrics(Config.resizeValue, Config.resizeValue);
        }
        return new PinchMetrics(width, height);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
