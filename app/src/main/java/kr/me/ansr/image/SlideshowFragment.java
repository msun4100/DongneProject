package kr.me.ansr.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;

import kr.me.ansr.R;
import kr.me.ansr.image.model.ImageItem;
import kr.me.ansr.image.model.PinchMetrics;
import kr.me.ansr.image.upload.Config;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by KMS on 2016-07-18.
 */
public class SlideshowFragment extends Fragment {
    private String TAG = SlideshowFragment.class.getSimpleName();
    private ArrayList<ImageItem> images;
//    private ViewPager viewPager;
    private CustomViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    AppCompatActivity activity;
    private TextView lblComplete, lblBack;

    PhotoViewAttacher mAttacher;

    public static SlideshowFragment newInstance() {
        SlideshowFragment f = new SlideshowFragment();
        return f;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);

        activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("SlideshowFragment");

        viewPager = (CustomViewPager) v.findViewById(R.id.viewpager);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.title);
        lblDate = (TextView) v.findViewById(R.id.date);

        lblComplete = (TextView) v.findViewById(R.id.lbl_complete);
        lblComplete.setVisibility(View.GONE);
        lblComplete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                nextProcess();
            }
        });
        lblBack = (TextView) v.findViewById(R.id.lbl_back);
        lblBack.setVisibility(View.GONE);
        lblBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                backProcess();
            }
        });
        images = (ArrayList<ImageItem>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");

//        Log.e(TAG, "position: " + selectedPosition);
//        Log.e(TAG, "images size: " + images.size());
//        Log.e(TAG, "images: " + images);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
    @Override
    public void onResume() {
        super.onResume();
        // destroy all menu and re-call onCreateOptionsMenu
//        getActivity().invalidateOptionsMenu();
//        Toast.makeText(getActivity(),"Slideshow f onresume", Toast.LENGTH_SHORT).show();
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " of " + images.size());

        ImageItem image = images.get(position);
        lblTitle.setText(image.getName());
        lblDate.setText(image.getTimestamp());
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            final ImageView imageViewPreview = (ImageView) view.findViewById(R.id.image_preview);
            mAttacher = new PhotoViewAttacher(imageViewPreview);
            ImageItem image = images.get(position);

//            Glide.with(getActivity()).load(image.getLarge())
//                    .thumbnail(0.5f)  // 미리 가져와 흐릿하게 보여줌
//                    .crossFade()      //
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imageViewPreview);
            final PinchMetrics pm = getResizePixel();
            Glide.with(getActivity())
                    .load(image.getLarge())
                    .asBitmap()
//                    .crossFade() // asBitmap() 이면 크로스페이드 사용 못함.
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   //원본 사이즈 캐시
//                    .signature(new StringSignature(image.getTimestamp() ))
                    .into(new SimpleTarget<Bitmap>(pm.width, pm.height) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            // Do something with bitmap here.
                            imageViewPreview.setImageBitmap(bitmap);
                            mAttacher.update();
                        }
                    });

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_slide_show, menu);
//
//    }
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
                nextProcess();
                return;
            }
        });
        menuNext.setActionView(imageNext);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_image_home){
            //Do whatever you want to do
            nextProcess();
            return true;
        }
        if(id == android.R.id.home){
            backProcess();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendImageToOnActivityResult(String path){
        String filePath = path;
        Intent intent = new Intent();
        intent.putExtra("filePath", filePath);
//        프래그먼트에서 부모액티비티에 대한 setResult를 어떻게 하지
        getActivity().setResult(MediaStoreActivity.RC_SELECT_PROFILE_CODE, intent);
    }

    public PinchMetrics getResizePixel(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
    private void backProcess() {
        Bundle bundle = new Bundle();
        bundle.putString("filePath", "");
        bundle.putSerializable("mItem", MediaStoreActivity.mItem);
        ((MediaStoreActivity)getActivity()).callImageHomeFragment(bundle);
    }

    private void nextProcess() {
        if(images.size() > 0){
            String path = images.get(0).getLarge();
//                sendImageToOnActivityResult(path);
            Bundle bundle = new Bundle();
            bundle.putString("filePath", path);
            bundle.putSerializable("mItem", MediaStoreActivity.mItem);
            ((MediaStoreActivity)getActivity()).callImageHomeFragment(bundle);
        }
    }

}
