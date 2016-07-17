package kr.me.ansr.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import kr.me.ansr.R;
import kr.me.ansr.login.LoginActivity;

/**
 * Created by KMS on 2016-07-18.
 */
public class ImageHomeFragment extends Fragment {
    private String TAG = ImageHomeFragment.class.getSimpleName();
    AppCompatActivity activity;
    private ImageView profileView;

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

        profileView = (ImageView) v.findViewById(R.id.image_image_home_profile);
        profileView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "imageview clicked", Toast.LENGTH_SHORT).show();
            }
        });

        Button btn = (Button) v.findViewById(R.id.btn_image_home_confirm);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        // destroy all menu and re-call onCreateOptionsMenu
//        getActivity().invalidateOptionsMenu();
//    }

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

}
